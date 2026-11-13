/*
 * ============================================================
 * Archivo: EstadoReserva.java
 * Propósito: estado de un repuesto asignado a una orden
 *            (columna servicios_repuestos.estado).
 * Conceptos clave: enum simple; name()/valueOf() (S10Demo02).
 * Justificación: modela la regla de negocio crítica: al asignarse el
 *            repuesto queda RESERVADO y al completarse la orden pasa a
 *            DEDUCIDO (sale definitivamente del inventario).
 * ============================================================
 */
package com.carmotors.modelo;

public enum EstadoReserva {
    RESERVADO,
    DEDUCIDO
}
