/*
 * ============================================================
 * Archivo: RecordatorioMantenimiento.java
 * Propósito: resultado calculado (no es tabla) que indica a qué cliente
 *            notificar sobre el próximo mantenimiento preventivo de un vehículo.
 * Conceptos clave: objeto de valor inmutable, fechas como String "AAAA-MM-DD".
 * Justificación: separa el CÁLCULO (capa de servicio) de la PRESENTACIÓN
 *            (capa de vista), cumpliendo SRP.
 * ============================================================
 */
package com.carmotors.modelo;

public class RecordatorioMantenimiento {
    private final Cliente cliente;
    private final Vehiculo vehiculo;
    private final String ultimoPreventivo;
    private final String proximoPreventivo;
    private final boolean vencido;  // true: la fecha sugerida ya pasó

    public RecordatorioMantenimiento(Cliente cliente, Vehiculo vehiculo, String ultimoPreventivo,
                                     String proximoPreventivo, boolean vencido) {
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.ultimoPreventivo = ultimoPreventivo;
        this.proximoPreventivo = proximoPreventivo;
        this.vencido = vencido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public String getUltimoPreventivo() {
        return ultimoPreventivo;
    }

    public String getProximoPreventivo() {
        return proximoPreventivo;
    }

    public boolean isVencido() {
        return vencido;
    }
}
