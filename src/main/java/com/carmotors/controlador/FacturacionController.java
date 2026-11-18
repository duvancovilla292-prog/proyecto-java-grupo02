/*
 * ============================================================
 * Archivo: FacturacionController.java
 * Propósito: controlador del Módulo de Facturación.
 * Conceptos clave: MVC - Controlador, COMMAND (lambdas para opciones de una línea).
 * Justificación: coordina sin lógica propia: el servicio emite la factura
 *            (cálculos y BD), la vista arma su texto y el servicio guarda ese
 *            texto en el archivo .txt. Los reportes gerenciales tienen su
 *            propio módulo (ReporteController).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
 *   - Path / Files / BufferedWriter / StandardOpenOption -> S17Demo00RutasMain, S17Demo01EscrituraMain y S17Demo03GestionMain (Sesión 17)
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.modelo.FacturaDetallada;
import com.carmotors.servicio.FacturacionServicio;
import com.carmotors.vista.FacturacionVista;
import com.carmotors.vista.VistaConsola;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public class FacturacionController extends ControladorBase {
    private final FacturacionVista vista;
    private final FacturacionServicio facturacion;

    public FacturacionController(VistaConsola consola, FacturacionVista vista, FacturacionServicio facturacion) {
        super(consola, vista);
        this.vista = vista;
        this.facturacion = facturacion;
        registrar("1", () -> facturar());
        registrar("2", () -> vista.mostrarFacturas(facturacion.listarFacturas()));
        registrar("3", () -> vista.mostrarFactura(
                vista.lineasFactura(facturacion.obtenerDetalle(consola.leerEntero("Número de orden: ")))));
        registrar("4", () -> exportar());
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
