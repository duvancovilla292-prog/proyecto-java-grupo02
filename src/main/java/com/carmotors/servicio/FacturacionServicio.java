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
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN STRATEGY -> S14Demo00 CalculadorEnvio (Sesión 14)
 *   - Path / Files / BufferedWriter / StandardOpenOption -> S17Demo00RutasMain, S17Demo01EscrituraMain y S17Demo03GestionMain (Sesión 17)
 *   - PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
 *   - referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
 *   - Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
 *   - Stream: distinct / sorted / mapToInt+sum / reduce(0, Integer::sum) -> S20Demo02OrdenesMain (Sesión 20)
 * ============================================================
 */
package com.carmotors.servicio;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.jdbc.ClienteDAOJdbc;
import com.carmotors.dao.jdbc.FacturaDAOJdbc;
import com.carmotors.servicio.impuesto.ImpuestoIva;
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

    /** Crea sus DAO y toma el IVA y la carpeta de facturas de la configuración (Singleton). */
    public FacturacionServicio() {
        this.facturaDAO = new FacturaDAOJdbc(ConexionFactory.getInstancia());
        this.clienteDAO = new ClienteDAOJdbc(ConexionFactory.getInstancia());
        this.ordenes = new OrdenServicioServicio();
        this.politicaImpuesto = new ImpuestoIva(ConexionFactory.getInstancia().getIvaPorcentaje()); // Strategy elegida
        this.carpetaFacturas = ConexionFactory.getInstancia().getCarpetaFacturas();
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
