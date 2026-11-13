/*
 * ============================================================
 * Archivo: Tecnico.java
 * Propósito: entidad de la tabla tecnicos.
 * Conceptos clave: encapsulamiento, boolean de estado.
 * Justificación: el enunciado exige asignar técnicos y medir su
 *            productividad; "activo" permite conservar el historial de
 *            un técnico retirado sin poder asignarle nuevas órdenes.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class Tecnico {
    private final int id;
    private final String nombre;
    private final String especialidad;
    private final boolean activo;

    public Tecnico(int id, String nombre, String especialidad, boolean activo) {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("Nombre del técnico obligatorio.");
        }
        this.id = id;
        this.nombre = nombre.strip();
        this.especialidad = especialidad == null ? "" : especialidad.strip();
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public boolean isActivo() {
        return activo;
    }
}
