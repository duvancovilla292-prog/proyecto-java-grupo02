/*
 * ============================================================
 * Archivo: ReporteServiciosSolicitados.java
 * Propósito: presentar "Servicios más solicitados" por tipo de mantenimiento y por descripción.
 * Conceptos clave: PATRÓN TEMPLATE METHOD (subclase de ReporteGerencial,
 *            S14Demo05), Stream.map + toList para convertir datos en líneas.
 * Justificación: solo da FORMATO; los cálculos llegan hechos desde la capa
 *            de servicio como objetos del modelo (SRP / separación MVC).
 * ============================================================
 */
package com.carmotors.vista.reporte;

import com.carmotors.modelo.ResumenDescripcion;
import com.carmotors.modelo.ResumenTipoServicio;
import com.carmotors.vista.Formato;

import java.util.ArrayList;
import java.util.List;

public class ReporteServiciosSolicitados extends ReporteGerencial {
    private final List<ResumenTipoServicio> porTipo;
    private final List<ResumenDescripcion> porDescripcion;

    public ReporteServiciosSolicitados(List<ResumenTipoServicio> porTipo, List<ResumenDescripcion> porDescripcion) {
        this.porTipo = List.copyOf(porTipo);
        this.porDescripcion = List.copyOf(porDescripcion);
    }

    @Override
    protected String titulo() {
        return "Servicios más solicitados";
    }

    @Override
    protected List<String> cuerpo() {
        List<String> lineas = new ArrayList<>();
        if (porTipo.stream().map(ResumenTipoServicio::getSolicitudes).reduce(0, Integer::sum) == 0) {
            return lineas;
        }
        lineas.add("Por tipo de mantenimiento (ingresos de órdenes con trabajo terminado):");
        lineas.addAll(porTipo.stream()
                .map(r -> "  " + Formato.ajustar(r.getTipo().getDescripcion(), 12)
                        + " solicitudes: " + Formato.derecha(String.valueOf(r.getSolicitudes()), 3)
                        + " | mano de obra: " + Formato.derecha(Formato.pesos(r.getManoObra()), 12)
                        + " | repuestos: " + Formato.derecha(Formato.pesos(r.getVentaRepuestos()), 12)
                        + " | total: " + Formato.derecha(Formato.pesos(r.ingresoTotal()), 12))
                .toList());
        lineas.add("");
        lineas.add("Por descripción del trabajo (más frecuentes primero):");
        lineas.addAll(porDescripcion.stream()
                .map(r -> "  " + Formato.derecha(String.valueOf(r.getCantidad()), 3) + " x " + r.getDescripcion())
                .toList());
        return lineas;
    }
}
