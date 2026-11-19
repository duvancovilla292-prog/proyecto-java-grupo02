/*
 * ============================================================
 * Archivo: EvaluacionProveedor.java
 * Propósito: fila de evaluaciones_proveedor (calidad y puntualidad 1-5).
 * Conceptos clave: validación de rangos (sesión 02), fecha como String
 *            (la asigna MySQL con CURDATE() al insertar).
 * Justificación: guardar cada evaluación por separado (y no un promedio)
 *            conserva el historial; el promedio se calcula con Streams.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class EvaluacionProveedor {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    public static final int MINIMO = 1;
    public static final int MAXIMO = 5;

    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final int id;
    private final int proveedorId;
    private final int calidad;      // 1..5
    private final int puntualidad;  // 1..5
    private final String fecha;     // "AAAA-MM-DD"; null hasta que se guarda
    private final String comentario;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public EvaluacionProveedor(int id, int proveedorId, int calidad, int puntualidad, String fecha,
                               String comentario) {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (proveedorId <= 0) {
            // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
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
        // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
        // Visto en clase: String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
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
