/*
 * ============================================================
 * Archivo: FacturacionServicio.java
 * Propósito: lógica del Módulo de Facturación: validar que la orden pueda
 *            facturarse, calcular totales e impuestos, registrar la factura,
 *            pasar la orden a "Facturado" y GUARDAR la factura como archivo
 *            de texto plano.
 * Conceptos clave: PATRÓN STRATEGY (PoliticaImpuesto inyectada), Streams
 *            (map + reduce para sumar subtotales, S20Demo02), escritura de
 *            archivos con Files.newBufferedWriter y StandardOpenOption
 *            (S17Demo01), Path.resolve y Files.createDirectories (S17Demo00).
 * Justificación: el servicio calcula y persiste; el TEXTO de la factura lo
 *            arma la vista (FacturacionVista) a partir de FacturaDetallada y el
 *            controlador entrega esas líneas a guardarArchivo(). Así el servicio
 *            no contiene formato de presentación y la vista no escribe en disco.
 *            La factura solo se emite si la orden está Completada (lo valida el
 *            patrón State en OrdenServicioServicio).
 * ============================================================
 */
package com.carmotors.servicio;

import com.carmotors.dao.ClienteDAO;
import com.carmotors.dao.FacturaDAO;
import com.carmotors.excepcion.RecursoNoEncontradoException;
import com.carmotors.excepcion.ReglaNegocioException;
import com.carmotors.modelo.DetalleRepuesto;
import com.carmotors.modelo.EstadoServicio;
import com.carmotors.modelo.Factura;
import com.carmotors.modelo.FacturaDetallada;
import com.carmotors.modelo.OrdenServicio;
import com.carmotors.modelo.Vehiculo;
import com.carmotors.servicio.impuesto.PoliticaImpuesto;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.List;

public class FacturacionServicio {
    private final FacturaDAO facturaDAO;
    private final ClienteDAO clienteDAO;
    private final OrdenServicioServicio ordenes;    // reutiliza reglas de órdenes (State)
    private final PoliticaImpuesto politicaImpuesto; // Strategy
    private final Path carpetaFacturas;              // destino de los .txt

    public FacturacionServicio(FacturaDAO facturaDAO, ClienteDAO clienteDAO, OrdenServicioServicio ordenes,
                               PoliticaImpuesto politicaImpuesto, Path carpetaFacturas) {
        this.facturaDAO = facturaDAO;
        this.clienteDAO = clienteDAO;
        this.ordenes = ordenes;
        this.politicaImpuesto = politicaImpuesto;
        this.carpetaFacturas = carpetaFacturas;
    }

    /**
     * Emite la factura de una orden COMPLETADA:
     * 1) valida estado y que no exista factura, 2) calcula totales con la
     * estrategia de impuesto, 3) guarda la factura, 4) cambia la orden a Facturado.
     * Devuelve todos los datos para que la vista la presente y se guarde el archivo.
     */
    public FacturaDetallada facturar(int servicioId) throws SQLException {
        OrdenServicio orden = ordenes.obtenerOrden(servicioId);
        ordenes.validarTransicion(orden, EstadoServicio.FACTURADO);
        if (facturaDAO.buscarPorServicio(servicioId) != null) {
            throw new ReglaNegocioException("La orden " + servicioId + " ya tiene factura.");
        }
        // map + reduce: suma de subtotales; 0L es el valor inicial si no hubo repuestos.
        int totalRepuestos = ordenes.repuestosDe(servicioId).stream()
                .map(DetalleRepuesto::subtotal)
                .reduce(0, Integer::sum);
        int manoObra = orden.getCostoManoObra();
        int impuestos = politicaImpuesto.calcular(totalRepuestos + manoObra);
        facturaDAO.insertar(new Factura(0, servicioId, null, totalRepuestos, manoObra, impuestos,
                totalRepuestos + manoObra + impuestos));
        ordenes.marcarFacturado(servicioId);
        return obtenerDetalle(servicioId);
    }

    public List<Factura> listarFacturas() throws SQLException {
        return facturaDAO.listar();
    }

    /** Reúne factura, orden, cliente, vehículo, repuestos y técnicos de una orden facturada. */
    public FacturaDetallada obtenerDetalle(int servicioId) throws SQLException {
        Factura factura = facturaDAO.buscarPorServicio(servicioId);
        if (factura == null) {
            throw new RecursoNoEncontradoException("La orden " + servicioId + " no tiene factura.");
        }
        OrdenServicio orden = ordenes.obtenerOrden(servicioId);
        Vehiculo vehiculo = ordenes.vehiculoDe(orden);
        return new FacturaDetallada(factura, orden, clienteDAO.buscarPorId(vehiculo.getClienteId()), vehiculo,
                ordenes.repuestosDe(servicioId), ordenes.tecnicosDe(servicioId), politicaImpuesto.descripcion());
    }

    /**
     * Guarda las líneas de la factura en facturas/FAC-xxxxxx.txt.
     * CREATE + TRUNCATE_EXISTING: el archivo siempre refleja el contenido actual.
     */
    public Path guardarArchivo(FacturaDetallada detalle, List<String> lineas) throws IOException {
        if (lineas == null || lineas.isEmpty()) {
            throw new ReglaNegocioException("No hay contenido para guardar en la factura.");
        }
        Files.createDirectories(carpetaFacturas);
        Path archivo = carpetaFacturas.resolve(detalle.numero() + ".txt");
        try (BufferedWriter escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            for (String linea : lineas) {
                escritor.write(linea);
                escritor.newLine();
            }
        }
        return archivo;
    }
}
