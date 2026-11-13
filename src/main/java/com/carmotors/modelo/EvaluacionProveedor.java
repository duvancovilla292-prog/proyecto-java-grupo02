/*
 * ============================================================
 * Archivo: EvaluacionProveedor.java
 * Propósito: fila de evaluaciones_proveedor (calidad y puntualidad 1-5).
 * Conceptos clave: validación de rangos (sesión 02), fecha como String
 *            (la asigna MySQL con CURDATE() al insertar).
 * Justificación: guardar cada evaluación por separado (y no un promedio)
 *            conserva el historial; el promedio se calcula con Streams.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class EvaluacionProveedor {
    public static final int MINIMO = 1;
    public static final int MAXIMO = 5;

    private final int id;
    private final int proveedorId;
    private final int calidad;      // 1..5
    private final int puntualidad;  // 1..5
    private final String fecha;     // "AAAA-MM-DD"; null hasta que se guarda
    private final String comentario;

    public EvaluacionProveedor(int id, int proveedorId, int calidad, int puntualidad, String fecha,
                               String comentario) {
        if (proveedorId <= 0) {
            throw new DatoInvalidoException("El proveedor es obligatorio.");
        }
        if (calidad < MINIMO || calidad > MAXIMO || puntualidad < MINIMO || puntualidad > MAXIMO) {
            throw new DatoInvalidoException("Calidad y puntualidad deben estar entre 1 y 5.");
        }
        this.id = id;
        this.proveedorId = proveedorId;
        this.calidad = calidad;
        this.puntualidad = puntualidad;
        this.fecha = fecha;
        this.comentario = comentario == null ? "" : comentario.strip();
    }

    /** Puntaje combinado de la evaluación (suma de ambos criterios, máximo 10). */
    public int puntaje() {
        return calidad + puntualidad;
    }

    public int getId() {
        return id;
    }

    public int getProveedorId() {
        return proveedorId;
    }

    public int getCalidad() {
        return calidad;
    }

    public int getPuntualidad() {
        return puntualidad;
    }

    public String getFecha() {
        return fecha;
    }

    public String getComentario() {
        return comentario;
    }
}
