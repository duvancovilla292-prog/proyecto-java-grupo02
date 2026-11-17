/*
 * ============================================================
 * Archivo: ReporteDesempenoProveedores.java
 * Propósito: presentar el ranking de proveedores por calidad y puntualidad.
 * Conceptos clave: PATRÓN TEMPLATE METHOD (subclase de ReporteGerencial,
 *            S14Demo05), Stream.map + toList para convertir datos en líneas.
 * Justificación: solo da FORMATO; los cálculos llegan hechos desde la capa
 *            de servicio como objetos del modelo (SRP / separación MVC).
 * ============================================================
 */
package com.carmotors.vista.reporte;

import com.carmotors.modelo.DesempenoProveedor;
import com.carmotors.vista.Formato;

import java.util.List;

public class ReporteDesempenoProveedores extends ReporteGerencial {
    private final List<DesempenoProveedor> ranking;

    public ReporteDesempenoProveedores(List<DesempenoProveedor> ranking) {
        this.ranking = List.copyOf(ranking);
    }

    @Override
    protected String titulo() {
        return "Desempeño de proveedores (escala 1 a 5)";
    }

    @Override
    protected List<String> cuerpo() {
        return ranking.stream()
                .map(d -> Formato.ajustar(d.getProveedor().getNombre(), 26)
                        + " puntaje: " + Formato.unDecimal(d.puntajeGeneral())
                        + " | calidad: " + Formato.unDecimal(d.getPromedioCalidad())
                        + " | puntualidad: " + Formato.unDecimal(d.getPromedioPuntualidad())
                        + " | evaluaciones: " + d.getEvaluaciones())
                .toList();
    }
}
