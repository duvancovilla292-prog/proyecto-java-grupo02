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
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN COMMAND -> S14Demo03 GestorComandos (Sesión 14)
 *   - PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.servicio.ReporteServicio;
import com.carmotors.vista.ReporteVista;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;

public class ReporteController extends ControladorBase {
    private static final int DESCRIPCIONES_TOP = 5;     // top de trabajos más repetidos
    private static final int DIAS_CADUCIDAD = 30;       // anticipación por defecto para lotes
    private static final int MESES_PREVENTIVO = 6;      // frecuencia por defecto del preventivo
    private static final int DIAS_RECORDATORIO = 30;    // anticipación por defecto para recordatorios

    private final ReporteVista vista;
    private final ReporteServicio servicio;

    /** Crea su vista y su servicio: el módulo se instancia solo cuando el usuario entra a él. */
    public ReporteController(VistaConsola consola) {
        this(consola, new ReporteVista(consola), new ReporteServicio());
    }

    public ReporteController(VistaConsola consola, ReporteVista vista, ReporteServicio servicio) {
        super(consola, vista);
        this.vista = vista;
        this.servicio = servicio;
        registrar("1", () -> vista.mostrarServiciosSolicitados(servicio.serviciosPorTipo(),
                servicio.serviciosPorDescripcion(DESCRIPCIONES_TOP)));
        registrar("2", () -> vista.mostrarProductividad(servicio.productividadTecnicos()));
        registrar("3", () -> vista.mostrarConsumo(servicio.consumoRepuestos()));
        registrar("4", () -> alertas());
        registrar("5", () -> recordatorios());
        registrar("6", () -> vista.mostrarDesempeno(servicio.desempenoProveedores()));
    }

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
