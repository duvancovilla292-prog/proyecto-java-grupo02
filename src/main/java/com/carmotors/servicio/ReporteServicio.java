/*
 * ============================================================
 * Archivo: ReporteServicio.java
 * Propósito: lógica del Módulo de Reportes: servicios más solicitados,
 *            productividad por técnico, consumo de repuestos, alertas de
 *            inventario, recordatorios de mantenimiento preventivo y
 *            desempeño de proveedores.
 * Conceptos clave: capa de servicio, API Stream (filter, map,
 *            sorted con Comparator, toList - sesión 20), lambdas y referencias
 *            a método, Comparator declarado (sesión 19).
 * Justificación: depende solo de ReporteDAO (Inversión de Dependencias):
 *            MySQL agrupa y suma, y aquí se aplican las REGLAS de negocio
 *            (validar parámetros, ordenar rankings, top N, filtrar a quién
 *            notificar). Devuelve objetos de modelo.reporte; la presentación
 *            la hace ReporteVista y el flujo lo coordina ReporteController.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.servicio;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.jdbc.ReporteDAOJdbc;
import com.carmotors.dao.ReporteDAO;
import com.carmotors.excepcion.DatoInvalidoException;
import com.carmotors.modelo.reporte.AlertaStock;
import com.carmotors.modelo.reporte.CalendarioPreventivo;
import com.carmotors.modelo.reporte.ConsumoRepuesto;
import com.carmotors.modelo.reporte.DesempenoProveedor;
import com.carmotors.modelo.reporte.LotePorVencer;
import com.carmotors.modelo.reporte.ProductividadTecnico;
import com.carmotors.modelo.reporte.RecordatorioMantenimiento;
import com.carmotors.modelo.reporte.ResumenDescripcion;
import com.carmotors.modelo.reporte.ResumenTipoServicio;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReporteServicio {
    // Más solicitados primero.
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: Predicate<T> / Comparator<T> / compareTo -> S19Demo03ReglasOrdenMain (Sesión 19)
    // Visto en clase: expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
    private static final Comparator<ResumenTipoServicio> POR_SOLICITUDES =
            (a, b) -> Integer.compare(b.getSolicitudes(), a.getSolicitudes());
    // Más repetida primero; desempate alfabético.
    private static final Comparator<ResumenDescripcion> POR_CANTIDAD = (a, b) -> {
        int porCantidad = Integer.compare(b.getCantidad(), a.getCantidad());
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
        return porCantidad != 0 ? porCantidad : a.getDescripcion().compareTo(b.getDescripcion());
    };
    // Más terminadas primero; desempata por mano de obra.
    private static final Comparator<ProductividadTecnico> POR_PRODUCTIVIDAD = (a, b) -> {
        int porTerminadas = Integer.compare(b.getTerminadas(), a.getTerminadas());
        return porTerminadas != 0 ? porTerminadas : Integer.compare(b.getManoObra(), a.getManoObra());
    };
    // Más consumido primero.
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private static final Comparator<ConsumoRepuesto> POR_CONSUMO = (a, b) -> Integer.compare(b.getUsadas(), a.getUsadas());
    // Más crítico primero: el que está más por debajo de su mínimo.
    private static final Comparator<AlertaStock> POR_CRITICIDAD = (a, b) -> Integer.compare(a.holgura(), b.holgura());
    // El que vence antes va primero.
    private static final Comparator<RecordatorioMantenimiento> POR_PROXIMO =
            (a, b) -> a.getProximoPreventivo().compareTo(b.getProximoPreventivo());
    // Mayor puntaje primero; desempate alfabético.
    private static final Comparator<DesempenoProveedor> POR_PUNTAJE = (a, b) -> {
        // Puntaje con una cifra decimal convertido a entero (cast de S04) para comparar con Integer.compare.
        // Visto en clase: conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
        int porPuntaje = Integer.compare((int) (b.puntajeGeneral() * 10), (int) (a.puntajeGeneral() * 10));
        return porPuntaje != 0 ? porPuntaje : a.getProveedor().getNombre().compareTo(b.getProveedor().getNombre());
    };

    // Visto en clase: PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
    private final ReporteDAO reporteDAO;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    /** Crea su propio DAO de reportes con la conexión del Singleton. */
    public ReporteServicio() {
        this.reporteDAO = new ReporteDAOJdbc(ConexionFactory.getInstancia());
    }

    // ------------------------------------------------------------ Servicios más solicitados

    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    /** Un resumen por tipo de mantenimiento, del más solicitado al menos solicitado. */
    public List<ResumenTipoServicio> serviciosPorTipo() throws SQLException {
        // Visto en clase: Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
        // Visto en clase: Stream: distinct / sorted / mapToInt+sum / reduce(0, Integer::sum) -> S20Demo02OrdenesMain (Sesión 20)
        return reporteDAO.resumenPorTipo().stream()
                .sorted(POR_SOLICITUDES)
                .toList();
    }

    /** Las "maximo" descripciones de trabajo más repetidas (desempate alfabético). */
    public List<ResumenDescripcion> serviciosPorDescripcion(int maximo) throws SQLException {
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (maximo <= 0) {
            throw new DatoInvalidoException("El top de descripciones debe ser positivo.");
        }
        List<ResumenDescripcion> todas = reporteDAO.resumenPorDescripcion().stream()
                .sorted(POR_CANTIDAD)
                .toList();
        List<ResumenDescripcion> primeras = new ArrayList<>();
        // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
        for (ResumenDescripcion resumen : todas) {
            if (primeras.size() == maximo) {
                // Visto en clase: break / continue -> S03DemoFor (Sesión 03)
                break;  // break de la sesión 03
            }
            primeras.add(resumen);
        }
        return primeras;
    }

    // ------------------------------------------------------------ Productividad por técnico

    /**
     * Para cada técnico: órdenes terminadas, en curso y mano de obra generada.
     * La mano de obra de una orden se reparte en partes iguales entre sus técnicos.
     * Orden: más terminadas primero; desempata por mano de obra.
     */
    public List<ProductividadTecnico> productividadTecnicos() throws SQLException {
        return reporteDAO.productividadTecnicos().stream()
                .sorted(POR_PRODUCTIVIDAD)
                .toList();
    }

    // ------------------------------------------------------------ Consumo de repuestos

    /** Repuestos con movimiento (usados o reservados), del más consumido al menos consumido. */
    public List<ConsumoRepuesto> consumoRepuestos() throws SQLException {
        // Visto en clase: referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
        return reporteDAO.consumoRepuestos().stream()
                .filter(ConsumoRepuesto::tuvoMovimiento)
                .sorted(POR_CONSUMO)
                .toList();
    }

    // ------------------------------------------------------------ Alertas de inventario

    /** Alerta 1: repuestos cuyo disponible está por debajo del mínimo, del más crítico al menos crítico. */
    public List<AlertaStock> repuestosBajoMinimo() throws SQLException {
        return reporteDAO.repuestosBajoMinimo().stream()
                .sorted(POR_CRITICIDAD)
                .toList();
    }

    /** Alerta 2: lotes que caducan dentro de los próximos "dias" (o ya caducaron), el más próximo primero. */
    public List<LotePorVencer> lotesPorVencer(int dias) throws SQLException {
        if (dias < 0) {
            throw new DatoInvalidoException("Los días de anticipación no pueden ser negativos.");
        }
        // La fecha límite (hoy + días) y el orden por caducidad los resuelve MySQL en el DAO.
        return reporteDAO.lotesPorVencer(dias);
    }

    // ------------------------------------------------------------ Recordatorios preventivos

    /**
     * Clientes a notificar: vehículos cuyo próximo preventivo (último + meses)
     * ya venció o vence en los próximos "diasAnticipacion" días, y que no
     * tienen ya un preventivo abierto.
     */
    public List<RecordatorioMantenimiento> recordatoriosMantenimiento(int meses, int diasAnticipacion)
            throws SQLException {
        if (meses <= 0 || diasAnticipacion < 0) {
            throw new DatoInvalidoException("Meses positivos y días de anticipación no negativos.");
        }
        return reporteDAO.calendarioPreventivos(meses, diasAnticipacion).stream()
                .filter(CalendarioPreventivo::debeNotificarse)
                .map(c -> new RecordatorioMantenimiento(c.getCliente(), c.getVehiculo(), c.getUltimo(),
                        c.getProximo(), c.estaVencido()))
                .sorted(POR_PROXIMO)
                .toList();
    }

    // ------------------------------------------------------------ Desempeño de proveedores

    /** Ranking de proveedores por puntaje (promedio de calidad y puntualidad). */
    public List<DesempenoProveedor> desempenoProveedores() throws SQLException {
        return reporteDAO.desempenoProveedores().stream()
                .sorted(POR_PUNTAJE)
                .toList();
    }
}
