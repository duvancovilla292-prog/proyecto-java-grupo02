/*
 * ============================================================
 * Archivo: TipoRepuesto.java
 * Propósito: entidad de la tabla de consulta repuesto_tipos
 *            (Mecánico, Eléctrico, Frenos...).
 * Conceptos clave: clase inmutable simple.
 * Justificación: a diferencia de estados y tipos de mantenimiento, el
 *            taller puede agregar nuevos tipos de repuesto en la BD sin
 *            recompilar; por eso es una clase leída por DAO y no un enum (OCP).
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class TipoRepuesto {
    private final int id;
    private final String nombre;

    public TipoRepuesto(int id, String nombre) {
        if (nombre == null || nombre.isBlank()) {
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
