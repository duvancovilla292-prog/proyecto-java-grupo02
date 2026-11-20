/*
 * ============================================================
 * Archivo: FacturacionController.java
 * Propósito: controlador del Módulo de Facturación.
 * Conceptos clave: MVC - Controlador, COMMAND (lambdas para opciones de una línea).
 * Justificación: coordina sin lógica propia: el servicio emite la factura
 *            (cálculos y BD), la vista arma su texto y el servicio guarda ese
 *            texto en el archivo .txt. Los reportes gerenciales tienen su
 *            propio módulo (ReporteController).
 *            Antes de pedir el número de orden muestra las órdenes
 *            Completadas (para facturar) o las facturas emitidas (para ver o
 *            exportar); reutiliza OrdenServicioVista para la tabla de órdenes.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.modelo.FacturaDetallada;
import com.carmotors.servicio.FacturacionServicio;
import com.carmotors.vista.FacturacionVista;
import com.carmotors.vista.OrdenServicioVista;
import com.carmotors.vista.VistaConsola;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

// Visto en clase: herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
// Visto en clase: PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public class FacturacionController extends ControladorBase {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final FacturacionVista vista;
    private final FacturacionServicio facturacion;
    private final OrdenServicioVista ordenes;  // reutiliza la tabla de órdenes como referencia

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    /** Recibe su vista (como UsuarioController de S18Demo01) y crea su servicio; se instancia solo cuando el usuario entra al módulo. */
    public FacturacionController(VistaConsola consola, FacturacionVista vista) {
        super(consola, vista);
        this.vista = vista;
        this.facturacion = new FacturacionServicio();
        this.ordenes = new OrdenServicioVista(consola);
        // Visto en clase: expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
        registrar("1", () -> facturar());
        registrar("2", () -> vista.mostrarFacturas(facturacion.listarFacturas()));
        registrar("3", () -> verFactura());
        registrar("4", () -> exportar());
    }

    // Emite (servicio) -> arma el texto (vista) -> lo guarda en archivo (servicio) -> lo muestra (vista).
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    private void facturar() throws SQLException, IOException {
        referencia("Órdenes completadas pendientes de factura",
                () -> ordenes.mostrarOrdenes(facturacion.listarOrdenesPorFacturar()));
        FacturaDetallada detalle = facturacion.facturar(consola.leerEntero("Número de orden completada: "));
        // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
        List<String> lineas = vista.lineasFactura(detalle);
        // Visto en clase: Path / Files / BufferedWriter / StandardOpenOption -> S17Demo00RutasMain, S17Demo01EscrituraMain y S17Demo03GestionMain (Sesión 17)
        Path archivo = facturacion.guardarArchivo(detalle, lineas);
        vista.mostrarFactura(lineas);
        consola.mostrarExito("Factura emitida y guardada en " + archivo.toAbsolutePath());
    }

    private void verFactura() throws SQLException {
        referenciaFacturas();
        vista.mostrarFactura(vista.lineasFactura(facturacion.obtenerDetalle(consola.leerEntero("Número de orden: "))));
    }

    private void exportar() throws SQLException, IOException {
        referenciaFacturas();
        FacturaDetallada detalle = facturacion.obtenerDetalle(consola.leerEntero("Número de orden: "));
        Path archivo = facturacion.guardarArchivo(detalle, vista.lineasFactura(detalle));
        consola.mostrarExito("Factura exportada en " + archivo.toAbsolutePath());
    }

    // La columna "Orden" es el número que se pide.
    private void referenciaFacturas() throws SQLException {
        referencia("Facturas emitidas (use el número de la columna Orden)",
                () -> vista.mostrarFacturas(facturacion.listarFacturas()));
    }
}
