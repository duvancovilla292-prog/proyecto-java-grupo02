/*
 * ============================================================
 * Archivo: ReporteServicio.java
 * Propósito: calcular los reportes gerenciales: servicios más solicitados,
 *            productividad por técnico y análisis de consumo de repuestos.
 * Conceptos clave: capa de servicio, API Stream (filter, map, distinct,
 *            sorted con Comparator, count, mapToInt + sum, reduce - sesión 20),
 *            lambdas y referencias a método (sesión 19).
 * Justificación: aquí solo se CALCULA (agrupar, contar, sumar, ordenar) y se
 *            devuelven objetos del modelo; la presentación la hacen las clases
 *            de vista/reporte (Template Method). Los agrupamientos se hacen con
 *            distinct + filter + count, operaciones vistas en clase.
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
import java.util.List;

public class ReporteServicio {
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


    /** Un resumen por tipo de mantenimiento, del más solicitado al menos solicitado. */
    public List<ResumenTipoServicio> serviciosPorTipo() throws SQLException {
        List<OrdenServicio> ordenes = ordenDAO.listar();
        List<DetalleRepuesto> detalles = detalleDAO.listar();
        return Arrays.stream(TipoMantenimiento.values())
                .map(tipo -> new ResumenTipoServicio(tipo,
                        (int) ordenes.stream().filter(o -> o.getTipoMantenimiento() == tipo).count(),
                        manoObraTerminada(ordenes, tipo),
                        ventaRepuestos(ordenes, detalles, tipo)))
                .sorted((a, b) -> Integer.compare(b.getSolicitudes(), a.getSolicitudes()))
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
        List<Integer> idsDelTipo = ordenes.stream()
                .filter(o -> o.getTipoMantenimiento() == tipo)
                .map(OrdenServicio::getId)
                .toList();
        return detalles.stream()
                .filter(d -> d.getEstado() == EstadoReserva.DEDUCIDO && idsDelTipo.contains(d.getServicioId()))
                .map(DetalleRepuesto::subtotal)
                .reduce(0, Integer::sum);
    }


    public List<ProductividadTecnico> productividadTecnicos() throws SQLException {
        List<OrdenServicio> ordenes = ordenDAO.listar();
        List<AsignacionTecnico> asignaciones = asignacionDAO.listar();
        return tecnicoDAO.listar().stream()
                .map(tecnico -> {
                    List<Integer> ids = asignaciones.stream()
                            .filter(a -> a.getTecnicoId() == tecnico.getId())
                            .map(AsignacionTecnico::getServicioId)
                            .toList();
                    List<OrdenServicio> propias = ordenes.stream().filter(o -> ids.contains(o.getId())).toList();
                    int terminadas = (int) propias.stream().filter(o -> o.getEstado().trabajoTerminado()).count();
                    int manoObra = propias.stream()
                            .filter(o -> o.getEstado().trabajoTerminado())
                            .map(o -> o.getCostoManoObra()
                                    / (int) asignaciones.stream().filter(a -> a.getServicioId() == o.getId()).count())
                            .reduce(0, Integer::sum);
                    return new ProductividadTecnico(tecnico, terminadas, propias.size() - terminadas, manoObra);
                })
                .sorted((a, b) -> {
                    int porTerminadas = Integer.compare(b.getTerminadas(), a.getTerminadas());
                    return porTerminadas != 0 ? porTerminadas : Integer.compare(b.getManoObra(), a.getManoObra());
                })
                .toList();
    }


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
                .sorted((a, b) -> Integer.compare(b.getUsadas(), a.getUsadas()))
                .toList();
    }

    private int unidades(List<DetalleRepuesto> detalles, Repuesto repuesto, EstadoReserva estado) {
        return detalles.stream()
                .filter(d -> d.getRepuestoId() == repuesto.getId() && d.getEstado() == estado)
                .mapToInt(DetalleRepuesto::getCantidad)
                .sum();
    }
}
