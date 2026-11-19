/*
 * ============================================================
 * Archivo: DesempenoProveedor.java
 * Propósito: resumen calculado de las evaluaciones de un proveedor.
 * Conceptos clave: objeto de valor; promedios calculados con división
 *            decimal (cast a double, sesión 02 y S08Demo00).
 * Justificación: el ranking de proveedores ordena estos objetos con un
 *            Comparator (sesión 19) sin tocar la entidad Proveedor.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo.reporte;

import com.carmotors.modelo.Proveedor;

public class DesempenoProveedor {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final Proveedor proveedor;
    private final int evaluaciones;
    private final double promedioCalidad;
    private final double promedioPuntualidad;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
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
