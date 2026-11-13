/*
 * ============================================================
 * Archivo: ProductividadTecnico.java
 * Propósito: fila del reporte "Productividad por técnico".
 * Conceptos clave: objeto de valor inmutable (private final + getters,
 *            sesión 06), resultado calculado con Streams en la capa de servicio.
 * Justificación: el servicio CALCULA y devuelve datos; la vista decide cómo
 *            mostrarlos. Así la capa de servicio no contiene formato de
 *            presentación (SRP y separación MVC).
 * ============================================================
 */
package com.carmotors.modelo;

public class ProductividadTecnico {
    private final Tecnico tecnico;
    private final int terminadas;  // órdenes con trabajo terminado
    private final int enCurso;     // órdenes pendientes o en proceso
    private final int manoObra;    // mano de obra repartida entre los técnicos de cada orden

    public ProductividadTecnico(Tecnico tecnico, int terminadas, int enCurso, int manoObra) {
        this.tecnico = tecnico;
        this.terminadas = terminadas;
        this.enCurso = enCurso;
        this.manoObra = manoObra;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public int getTerminadas() {
        return terminadas;
    }

    public int getEnCurso() {
        return enCurso;
    }

    public int getManoObra() {
        return manoObra;
    }
}
