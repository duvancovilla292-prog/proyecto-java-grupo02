/*
 * ============================================================
 * Archivo: Tecnico.java
 * Propósito: entidad de la tabla tecnicos.
 * Conceptos clave: encapsulamiento, boolean de estado.
 * Justificación: el enunciado exige asignar técnicos y medir su
 *            productividad; "activo" permite conservar el historial de
 *            un técnico retirado sin poder asignarle nuevas órdenes.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class Tecnico {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final int id;
    private final String nombre;
    private final String especialidad;
    private final boolean activo;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public Tecnico(int id, String nombre, String especialidad, boolean activo) {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        // Visto en clase: String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
        if (nombre == null || nombre.isBlank()) {
            // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
            throw new DatoInvalidoException("Nombre del técnico obligatorio.");
        }
        this.id = id;
        this.nombre = nombre.strip();
        // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
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
