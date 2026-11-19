/*
 * ============================================================
 * Archivo: EstadoReserva.java
 * Propósito: estado de un repuesto asignado a una orden
 *            (columna servicios_repuestos.estado).
 * Conceptos clave: enum simple; name()/valueOf() (S10Demo02).
 * Justificación: modela la regla de negocio crítica: al asignarse el
 *            repuesto queda RESERVADO y al completarse la orden pasa a
 *            DEDUCIDO (sale definitivamente del inventario).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo;

// Visto en clase: enum con atributos, values() y == -> S10Demo02 DiaSemana y S10Demo02EnumMain (Sesión 10)
public enum EstadoReserva {
    RESERVADO,
    DEDUCIDO
}
