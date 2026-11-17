/*
 * ============================================================
 * Archivo: ReporteConsumoRepuestos.java
 * Propósito: presentar el "Análisis de consumo de repuestos" con totales.
 * Conceptos clave: PATRÓN TEMPLATE METHOD (subclase de ReporteGerencial,
 *            S14Demo05), Stream.map + toList para convertir datos en líneas.
 * Justificación: solo da FORMATO; los cálculos llegan hechos desde la capa
 *            de servicio como objetos del modelo (SRP / separación MVC).
 * ============================================================
 */
package com.carmotors.vista.reporte;

import com.carmotors.modelo.ConsumoRepuesto;
import com.carmotors.vista.Formato;

import java.util.ArrayList;
import java.util.List;

public class ReporteConsumoRepuestos extends ReporteGerencial {
    private final List<ConsumoRepuesto> filas;

    public ReporteConsumoRepuestos(List<ConsumoRepuesto> filas) {
        this.filas = List.copyOf(filas);
    }

    @Override
    protected String titulo() {
        return "Análisis de consumo de repuestos";
    }

    @Override
    protected List<String> cuerpo() {
        List<String> lineas = new ArrayList<>();
        if (filas.isEmpty()) {
            return lineas;
        }
        lineas.add(Formato.ajustar("Repuesto", 30) + Formato.derecha("Usadas", 7) + Formato.derecha("Reserv.", 8)
                + Formato.derecha("Ingreso", 13) + Formato.derecha("Margen", 12));
        lineas.addAll(filas.stream()
                .map(c -> Formato.ajustar(c.getRepuesto().getNombre(), 30)
                        + Formato.derecha(String.valueOf(c.getUsadas()), 7)
                        + Formato.derecha(String.valueOf(c.getReservadas()), 8)
                        + Formato.derecha(Formato.pesos(c.getIngreso()), 13)
                        + Formato.derecha(Formato.pesos(c.getMargen()), 12))
                .toList());
        lineas.add("");
        lineas.add("Ingreso total por repuestos: "
                + Formato.pesos(filas.stream().map(ConsumoRepuesto::getIngreso).reduce(0, Integer::sum))
                + " | Margen total: " + Formato.pesos(filas.stream().map(ConsumoRepuesto::getMargen).reduce(0, Integer::sum)));
        return lineas;
    }
}
