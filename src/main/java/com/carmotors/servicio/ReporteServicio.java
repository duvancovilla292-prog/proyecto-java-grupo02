/*
 * ============================================================
 * Archivo: ReporteServicio.java
 * Propósito: calcular los reportes gerenciales: servicios más solicitados,
 *            productividad por técnico y análisis de consumo de repuestos.
 * Conceptos clave: capa de servicio, API Stream (filter, map, distinct,
 *            sorted con Comparator, count, mapToInt + sum, reduce - sesión 20),
 *            lambdas y referencias a método, Comparator declarado (sesión 19),
 *            Set/HashSet para ids sin duplicados (sesión 10).
 * Justificación: aquí solo se CALCULA (agrupar, contar, sumar, ordenar) y se
 *            devuelven objetos del modelo; la presentación la hacen las clases
 *            de vista/reporte (Template Method). Los agrupamientos se hacen con
 *            distinct + filter + count, operaciones vistas en clase.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - break / continue -> S03DemoFor (Sesión 03)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
 *   - conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - Set / HashSet -> S10Demo00SetMain (Sesión 10)
 *   - enum con atributos, values() y == -> S10Demo02 DiaSemana y S10Demo02EnumMain (Sesión 10)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 *   - Predicate<T> / Comparator<T> / compareTo -> S19Demo03ReglasOrdenMain (Sesión 19)
 *   - referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
 *   - Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
 *   - Stream: count / anyMatch / Arrays.stream -> S20Demo01FuentesMain (Sesión 20)
 *   - Stream: distinct / sorted / mapToInt+sum / reduce(0, Integer::sum) -> S20Demo02OrdenesMain (Sesión 20)
 * ============================================================
 */
package com.carmotors.servicio;

import com.carmotors.dao.AsignacionTecnicoDAO;
import com.carmotors.dao.DetalleRepuestoDAO;
import com.carmotors.dao.OrdenServicioDAO;
import com.carmotors.dao.RepuestoDAO;
import com.carmotors.dao.TecnicoDAO;
import com.carmotors.modelo.AsignacionTecnico;
import com.carmotors.modelo.ConsumoRepuesto;
import com.carmotors.modelo.DetalleRepuesto;
import com.carmotors.modelo.EstadoReserva;
import com.carmotors.modelo.OrdenServicio;
import com.carmotors.modelo.ProductividadTecnico;
import com.carmotors.modelo.Repuesto;
import com.carmotors.modelo.ResumenDescripcion;
import com.carmotors.modelo.ResumenTipoServicio;
import com.carmotors.modelo.TipoMantenimiento;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReporteServicio {
    // Más solicitados primero.
    private static final Comparator<ResumenTipoServicio> POR_SOLICITUDES =
            (a, b) -> Integer.compare(b.getSolicitudes(), a.getSolicitudes());
    // Más terminadas primero; desempata por mano de obra.
    private static final Comparator<ProductividadTecnico> POR_PRODUCTIVIDAD = (a, b) -> {
        int porTerminadas = Integer.compare(b.getTerminadas(), a.getTerminadas());
        return porTerminadas != 0 ? porTerminadas : Integer.compare(b.getManoObra(), a.getManoObra());
    };
    // Más consumido primero.
    private static final Comparator<ConsumoRepuesto> POR_CONSUMO = (a, b) -> Integer.compare(b.getUsadas(), a.getUsadas());

    private final OrdenServicioDAO ordenDAO;
    private final DetalleRepuestoDAO detalleDAO;
    private final TecnicoDAO tecnicoDAO;
    private final AsignacionTecnicoDAO asignacionDAO;
    private final RepuestoDAO repuestoDAO;

    public ReporteServicio(OrdenServicioDAO ordenDAO, DetalleRepuestoDAO detalleDAO, TecnicoDAO tecnicoDAO,
                           AsignacionTecnicoDAO asignacionDAO, RepuestoDAO repuestoDAO) {
        this.ordenDAO = ordenDAO;
        this.detalleDAO = detalleDAO;
        this.tecnicoDAO = tecnicoDAO;
        this.asignacionDAO = asignacionDAO;
        this.repuestoDAO = repuestoDAO;
    }

    // ------------------------------------------------------------ Servicios más solicitados

    /** Un resumen por tipo de mantenimiento, del más solicitado al menos solicitado. */
    public List<ResumenTipoServicio> serviciosPorTipo() throws SQLException {
        List<OrdenServicio> ordenes = ordenDAO.listar();
        List<DetalleRepuesto> detalles = detalleDAO.listar();
        return Arrays.stream(TipoMantenimiento.values())
                .map(tipo -> new ResumenTipoServicio(tipo,
                        (int) ordenes.stream().filter(o -> o.getTipoMantenimiento() == tipo).count(),
                        manoObraTerminada(ordenes, tipo),
                        ventaRepuestos(ordenes, detalles, tipo)))
                .sorted(POR_SOLICITUDES)
                .toList();
    }

    /** Las "maximo" descripciones de trabajo más repetidas (desempate alfabético). */
    public List<ResumenDescripcion> serviciosPorDescripcion(int maximo) throws SQLException {
        List<OrdenServicio> ordenes = ordenDAO.listar();
        List<ResumenDescripcion> todas = ordenes.stream()
                .map(o -> o.getDescripcionProblema().toLowerCase())
                .distinct()
                .map(d -> new ResumenDescripcion(d, (int) ordenes.stream()
                        .filter(o -> o.getDescripcionProblema().equalsIgnoreCase(d)).count()))
                .sorted((a, b) -> {
                    int porCantidad = Integer.compare(b.getCantidad(), a.getCantidad());
                    return porCantidad != 0 ? porCantidad : a.getDescripcion().compareTo(b.getDescripcion());
                })
                .toList();
        List<ResumenDescripcion> primeras = new ArrayList<>();
        for (ResumenDescripcion resumen : todas) {
            if (primeras.size() == maximo) {
                break;  // break de la sesión 03
            }
            primeras.add(resumen);
        }
        return primeras;
    }

    private int manoObraTerminada(List<OrdenServicio> ordenes, TipoMantenimiento tipo) {
        return ordenes.stream()
                .filter(o -> o.getTipoMantenimiento() == tipo && o.getEstado().trabajoTerminado())
                .map(OrdenServicio::getCostoManoObra)
                .reduce(0, Integer::sum);
    }

    private int ventaRepuestos(List<OrdenServicio> ordenes, List<DetalleRepuesto> detalles, TipoMantenimiento tipo) {
        Set<Integer> idsDelTipo = new HashSet<>(ordenes.stream()
                .filter(o -> o.getTipoMantenimiento() == tipo)
                .map(OrdenServicio::getId)
                .toList());
        return detalles.stream()
                .filter(d -> d.getEstado() == EstadoReserva.DEDUCIDO && idsDelTipo.contains(d.getServicioId()))
                .map(DetalleRepuesto::subtotal)
                .reduce(0, Integer::sum);
    }

    // ------------------------------------------------------------ Productividad por técnico

    /**
     * Para cada técnico: órdenes terminadas, en curso y mano de obra generada.
     * La mano de obra de una orden se reparte en partes iguales entre sus técnicos.
     * Orden: más terminadas primero; desempata por mano de obra.
     */
    public List<ProductividadTecnico> productividadTecnicos() throws SQLException {
        List<OrdenServicio> ordenes = ordenDAO.listar();
        List<AsignacionTecnico> asignaciones = asignacionDAO.listar();
        return tecnicoDAO.listar().stream()
                .map(tecnico -> {
                    // Set: un técnico cuenta cada orden una sola vez.
                    Set<Integer> ids = new HashSet<>(asignaciones.stream()
                            .filter(a -> a.getTecnicoId() == tecnico.getId())
                            .map(AsignacionTecnico::getServicioId)
                            .toList());
                    List<OrdenServicio> propias = ordenes.stream().filter(o -> ids.contains(o.getId())).toList();
                    int terminadas = (int) propias.stream().filter(o -> o.getEstado().trabajoTerminado()).count();
                    int manoObra = propias.stream()
                            .filter(o -> o.getEstado().trabajoTerminado())
                            .map(o -> o.getCostoManoObra()
                                    / (int) asignaciones.stream().filter(a -> a.getServicioId() == o.getId()).count())
                            .reduce(0, Integer::sum);
                    return new ProductividadTecnico(tecnico, terminadas, propias.size() - terminadas, manoObra);
                })
                .sorted(POR_PRODUCTIVIDAD)
                .toList();
    }

    // ------------------------------------------------------------ Consumo de repuestos

    /** Repuestos con movimiento (usados o reservados), del más consumido al menos consumido. */
    public List<ConsumoRepuesto> consumoRepuestos() throws SQLException {
        List<DetalleRepuesto> detalles = detalleDAO.listar();
        List<Repuesto> repuestos = repuestoDAO.listar();
        return repuestos.stream()
                .map(r -> {
                    int usadas = unidades(detalles, r, EstadoReserva.DEDUCIDO);
                    int ingreso = detalles.stream()
                            .filter(d -> d.getRepuestoId() == r.getId() && d.getEstado() == EstadoReserva.DEDUCIDO)
                            .map(DetalleRepuesto::subtotal)
                            .reduce(0, Integer::sum);
                    return new ConsumoRepuesto(r, usadas, unidades(detalles, r, EstadoReserva.RESERVADO), ingreso,
                            ingreso - usadas * r.getPrecioCompra());
                })
                .filter(c -> c.getUsadas() > 0 || c.getReservadas() > 0)
                .sorted(POR_CONSUMO)
                .toList();
    }

    private int unidades(List<DetalleRepuesto> detalles, Repuesto repuesto, EstadoReserva estado) {
        return detalles.stream()
                .filter(d -> d.getRepuestoId() == repuesto.getId() && d.getEstado() == estado)
                .mapToInt(DetalleRepuesto::getCantidad)
                .sum();
    }
}
