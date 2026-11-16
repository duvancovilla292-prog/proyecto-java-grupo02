/*
 * ============================================================
 * Archivo: ReporteRecordatorios.java
 * Propósito: presentar los clientes a notificar por su próximo mantenimiento preventivo.
 * Conceptos clave: PATRÓN TEMPLATE METHOD (subclase de ReporteGerencial,
 *            S14Demo05), Stream.map + toList para convertir datos en líneas.
 * Justificación: solo da FORMATO; los cálculos llegan hechos desde la capa
 *            de servicio como objetos del modelo (SRP / separación MVC).
 * ============================================================
 */
package com.carmotors.vista.reporte;

import com.carmotors.modelo.RecordatorioMantenimiento;
import com.carmotors.vista.Formato;

import java.util.List;

public class ReporteRecordatorios extends ReporteGerencial {
    private final List<RecordatorioMantenimiento> recordatorios;
    private final int meses;

    public ReporteRecordatorios(List<RecordatorioMantenimiento> recordatorios, int meses) {
        this.recordatorios = List.copyOf(recordatorios);
        this.meses = meses;
    }

    @Override
    protected String titulo() {
        return "Recordatorios de mantenimiento preventivo (cada " + meses + " meses)";
    }

    @Override
    protected List<String> cuerpo() {
        return recordatorios.stream()
                .map(r -> (r.isVencido() ? "[VENCIDO] " : "[PRÓXIMO] ")
                        + Formato.ajustar(r.getCliente().getNombre(), 20)
                        + " | " + r.getCliente().getTelefono() + " | " + r.getCliente().getEmail()
                        + " | " + r.getVehiculo().getPlaca()
                        + " | último " + Formato.fecha(r.getUltimoPreventivo())
                        + " | sugerido " + Formato.fecha(r.getProximoPreventivo()))
                .toList();
    }

    @Override
    protected String mensajeSinDatos() {
        return "No hay clientes por notificar en este periodo.";
    }
}
