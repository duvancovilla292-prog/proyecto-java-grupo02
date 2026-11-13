/*
 * ============================================================
 * Archivo: TipoMantenimiento.java
 * Propósito: representar la tabla de consulta mantenimiento_tipos.
 * Conceptos clave: enum con atributos y constructor (S10Demo02 DiaSemana),
 *            values() para buscar por id.
 * Justificación: los tipos son fijos (Preventivo/Correctivo); un enum
 *            impide valores inválidos en tiempo de compilación y su id
 *            coincide con la fila de la base de datos (data.sql).
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public enum TipoMantenimiento {
    PREVENTIVO(1, "Preventivo"),
    CORRECTIVO(2, "Correctivo");

    private final int id;              // Id en mantenimiento_tipos
    private final String descripcion;  // Texto para mostrar

    TipoMantenimiento(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** Convierte el id leído de la BD en el enum; lanza DatoInvalidoException si no existe. */
    public static TipoMantenimiento desdeId(int id) {
        for (TipoMantenimiento tipo : values()) {
            if (tipo.id == id) {
                return tipo;
            }
        }
        throw new DatoInvalidoException("Tipo de mantenimiento desconocido: " + id);
    }
}
