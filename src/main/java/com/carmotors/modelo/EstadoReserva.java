/*
 * ============================================================
 * Archivo: EstadoReserva.java
 * Propósito: estado de un repuesto asignado a una orden
 *            (columna servicios_repuestos.estado).
 * Conceptos clave: enum simple; name()/valueOf() (S10Demo02).
 * Justificación: modela la regla de negocio crítica: al asignarse el
 *            repuesto queda RESERVADO y al completarse la orden pasa a
 *            DEDUCIDO (sale definitivamente del inventario).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - enum con atributos, values() y == -> S10Demo02 DiaSemana y S10Demo02EnumMain (Sesión 10)
 * ============================================================
 */
package com.carmotors.modelo;

public enum EstadoReserva {
    RESERVADO,
    DEDUCIDO
}
