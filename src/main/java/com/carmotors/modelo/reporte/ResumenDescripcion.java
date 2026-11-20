/*
 * ============================================================
 * Archivo: ResumenDescripcion.java
 * Propósito: cuántas veces se solicitó un trabajo con la misma descripción.
 * Conceptos clave: objeto de valor inmutable (private final + getters,
 *            sesión 06), agregado por MySQL en ReporteDAO y ordenado en ReporteServicio.
 * Justificación: el servicio CALCULA y devuelve datos; la vista decide cómo
 *            mostrarlos. Así la capa de servicio no contiene formato de
 *            presentación (SRP y separación MVC).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo.reporte;

public class ResumenDescripcion {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final String descripcion;  // normalizada en minúsculas
    private final int cantidad;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
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
