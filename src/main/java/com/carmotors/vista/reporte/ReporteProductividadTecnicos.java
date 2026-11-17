/*
 * ============================================================
 * Archivo: ReporteProductividadTecnicos.java
 * Propósito: presentar "Productividad por técnico".
 * Conceptos clave: PATRÓN TEMPLATE METHOD (subclase de ReporteGerencial,
 *            S14Demo05), Stream.map + toList para convertir datos en líneas.
 * Justificación: solo da FORMATO; los cálculos llegan hechos desde la capa
 *            de servicio como objetos del modelo (SRP / separación MVC).
 * ============================================================
 */
package com.carmotors.vista.reporte;

import com.carmotors.modelo.ProductividadTecnico;
import com.carmotors.vista.Formato;

import java.util.List;

public class ReporteProductividadTecnicos extends ReporteGerencial {
    private final List<ProductividadTecnico> filas;

    public ReporteProductividadTecnicos(List<ProductividadTecnico> filas) {
        this.filas = List.copyOf(filas);
    }

    @Override
    protected String titulo() {
        return "Productividad por técnico";
    }

    @Override
    protected List<String> cuerpo() {
        return filas.stream()
                .map(p -> Formato.ajustar(p.getTecnico().getNombre() + (p.getTecnico().isActivo() ? "" : " (inactivo)"), 26)
                        + " terminadas: " + Formato.derecha(String.valueOf(p.getTerminadas()), 2)
                        + " | en curso: " + Formato.derecha(String.valueOf(p.getEnCurso()), 2)
                        + " | mano de obra: " + Formato.derecha(Formato.pesos(p.getManoObra()), 11))
                .toList();
    }
}
