/*
 * ============================================================
 * Archivo: FacturacionController.java
 * Propósito: controlador del Módulo de Facturación y Reportes.
 * Conceptos clave: MVC - Controlador, COMMAND (lambdas para opciones de una línea).
 * Justificación: coordina sin lógica propia: el servicio emite la factura
 *            (cálculos y BD), la vista arma su texto y el servicio guarda ese
 *            texto en el archivo .txt. Los reportes se calculan en
 *            ReporteServicio y se presentan en la vista.
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.modelo.FacturaDetallada;
import com.carmotors.servicio.FacturacionServicio;
import com.carmotors.servicio.ReporteServicio;
import com.carmotors.vista.FacturacionVista;
import com.carmotors.vista.VistaConsola;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public class FacturacionController extends ControladorBase {
    private static final int DESCRIPCIONES_TOP = 5;  // top de trabajos más repetidos

    private final FacturacionVista vista;
    private final FacturacionServicio facturacion;

    public FacturacionController(VistaConsola consola, FacturacionVista vista, FacturacionServicio facturacion,
                                 ReporteServicio reportes) {
        super(consola, vista);
        this.vista = vista;
        this.facturacion = facturacion;
        registrar("1", () -> facturar());
        registrar("2", () -> vista.mostrarFacturas(facturacion.listarFacturas()));
        registrar("3", () -> vista.mostrarFactura(
                vista.lineasFactura(facturacion.obtenerDetalle(consola.leerEntero("Número de orden: ")))));
        registrar("4", () -> exportar());
        registrar("5", () -> vista.mostrarServiciosSolicitados(reportes.serviciosPorTipo(),
                reportes.serviciosPorDescripcion(DESCRIPCIONES_TOP)));
        registrar("6", () -> vista.mostrarProductividad(reportes.productividadTecnicos()));
        registrar("7", () -> vista.mostrarConsumo(reportes.consumoRepuestos()));
    }

    // Emite (servicio) -> arma el texto (vista) -> lo guarda en archivo (servicio) -> lo muestra (vista).
    private void facturar() throws SQLException, IOException {
        FacturaDetallada detalle = facturacion.facturar(consola.leerEntero("Número de orden completada: "));
        List<String> lineas = vista.lineasFactura(detalle);
        Path archivo = facturacion.guardarArchivo(detalle, lineas);
        vista.mostrarFactura(lineas);
        consola.mostrarExito("Factura emitida y guardada en " + archivo.toAbsolutePath());
    }

    private void exportar() throws SQLException, IOException {
        FacturaDetallada detalle = facturacion.obtenerDetalle(consola.leerEntero("Número de orden: "));
        Path archivo = facturacion.guardarArchivo(detalle, vista.lineasFactura(detalle));
        consola.mostrarExito("Factura exportada en " + archivo.toAbsolutePath());
    }
}
