/*
 * ============================================================
 * Archivo: ResumenDescripcion.java
 * Propósito: cuántas veces se solicitó un trabajo con la misma descripción.
 * Conceptos clave: objeto de valor inmutable (private final + getters,
 *            sesión 06), resultado calculado con Streams en la capa de servicio.
 * Justificación: el servicio CALCULA y devuelve datos; la vista decide cómo
 *            mostrarlos. Así la capa de servicio no contiene formato de
 *            presentación (SRP y separación MVC).
 * ============================================================
 */
package com.carmotors.modelo;

public class ResumenDescripcion {
    private final String descripcion;  // normalizada en minúsculas
    private final int cantidad;

    public ResumenDescripcion(String descripcion, int cantidad) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }
}
