/*
 * ============================================================
 * Archivo: TipoRepuesto.java
 * Propósito: entidad de la tabla de consulta repuesto_tipos
 *            (Mecánico, Eléctrico, Frenos...).
 * Conceptos clave: clase inmutable simple.
 * Justificación: a diferencia de estados y tipos de mantenimiento, el
 *            taller puede agregar nuevos tipos de repuesto en la BD sin
 *            recompilar; por eso es una clase leída por DAO y no un enum (OCP).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class TipoRepuesto {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final int id;
    private final String nombre;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public TipoRepuesto(int id, String nombre) {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        // Visto en clase: String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
        if (nombre == null || nombre.isBlank()) {
            // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
            throw new DatoInvalidoException("Nombre de tipo obligatorio.");
        }
        this.id = id;
        this.nombre = nombre.strip();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
