/*
 * ============================================================
 * Archivo: ReporteController.java
 * Propósito: controlador del Módulo de Reportes: pide los parámetros de cada
 *            reporte, solicita los datos a ReporteServicio y se los entrega a
 *            ReporteVista para mostrarlos.
 * Conceptos clave: MVC - Controlador, COMMAND (lambdas para opciones de una
 *            línea), herencia de ControladorBase (TEMPLATE METHOD del menú).
 * Justificación: los reportes tienen su propia ruta completa
 *            (ReporteController -> ReporteServicio -> ReporteDAO ->
 *            modelo.reporte, y ReporteVista para el formato) en lugar de
 *            vivir repartidos dentro de las vistas de otros módulos. El
 *            controlador coordina sin lógica propia (SRP).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.servicio.ReporteServicio;
import com.carmotors.vista.ReporteVista;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;

// Visto en clase: herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
// Visto en clase: PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public class ReporteController extends ControladorBase {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private static final int DESCRIPCIONES_TOP = 5;     // top de trabajos más repetidos
    private static final int DIAS_CADUCIDAD = 30;       // anticipación por defecto para lotes
    private static final int MESES_PREVENTIVO = 6;      // frecuencia por defecto del preventivo
    private static final int DIAS_RECORDATORIO = 30;    // anticipación por defecto para recordatorios

    private final ReporteVista vista;
    private final ReporteServicio servicio;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    /** Recibe su vista (como UsuarioController de S18Demo01) y crea su servicio; se instancia solo cuando el usuario entra al módulo. */
    public ReporteController(VistaConsola consola, ReporteVista vista) {
        super(consola, vista);
        this.vista = vista;
        this.servicio = new ReporteServicio();
        // Visto en clase: PATRÓN COMMAND -> S14Demo03 GestorComandos (Sesión 14)
        // Visto en clase: expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
        registrar("1", () -> vista.mostrarServiciosSolicitados(servicio.serviciosPorTipo(),
                servicio.serviciosPorDescripcion(DESCRIPCIONES_TOP)));
        registrar("2", () -> vista.mostrarProductividad(servicio.productividadTecnicos()));
        registrar("3", () -> vista.mostrarConsumo(servicio.consumoRepuestos()));
        registrar("4", () -> alertas());
        registrar("5", () -> recordatorios());
        registrar("6", () -> vista.mostrarDesempeno(servicio.desempenoProveedores()));
    }

    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    private void alertas() throws SQLException {
        int dias = consola.leerEnteroConDefecto("Días de anticipación para caducidad", DIAS_CADUCIDAD);
        vista.mostrarAlertas(servicio.repuestosBajoMinimo(), servicio.lotesPorVencer(dias), dias);
    }

    private void recordatorios() throws SQLException {
        int meses = consola.leerEnteroConDefecto("Frecuencia del preventivo en meses", MESES_PREVENTIVO);
        int dias = consola.leerEnteroConDefecto("Incluir los que vencen en los próximos días", DIAS_RECORDATORIO);
        vista.mostrarRecordatorios(servicio.recordatoriosMantenimiento(meses, dias), meses);
    }
}
