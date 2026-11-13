/*
 * ============================================================
 * Archivo: DesempenoProveedor.java
 * Propósito: resumen calculado de las evaluaciones de un proveedor.
 * Conceptos clave: objeto de valor; promedios calculados con división
 *            decimal (cast a double, sesión 02 y S08Demo00).
 * Justificación: el ranking de proveedores ordena estos objetos con un
 *            Comparator (sesión 19) sin tocar la entidad Proveedor.
 * ============================================================
 */
package com.carmotors.modelo;

public class DesempenoProveedor {
    private final Proveedor proveedor;
    private final int evaluaciones;
    private final double promedioCalidad;
    private final double promedioPuntualidad;

    public DesempenoProveedor(Proveedor proveedor, int evaluaciones, double promedioCalidad,
                              double promedioPuntualidad) {
        this.proveedor = proveedor;
        this.evaluaciones = evaluaciones;
        this.promedioCalidad = promedioCalidad;
        this.promedioPuntualidad = promedioPuntualidad;
    }

    /** Puntaje final sobre 5: promedio simple de ambos criterios. */
    public double puntajeGeneral() {
        return (promedioCalidad + promedioPuntualidad) / 2.0;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public int getEvaluaciones() {
        return evaluaciones;
    }

    public double getPromedioCalidad() {
        return promedioCalidad;
    }

    public double getPromedioPuntualidad() {
        return promedioPuntualidad;
    }
}
