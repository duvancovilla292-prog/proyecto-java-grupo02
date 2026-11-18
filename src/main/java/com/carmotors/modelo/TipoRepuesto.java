/*
 * ============================================================
 * Archivo: TipoRepuesto.java
 * Propósito: entidad de la tabla de consulta repuesto_tipos
 *            (Mecánico, Eléctrico, Frenos...).
 * Conceptos clave: clase inmutable simple.
 * Justificación: a diferencia de estados y tipos de mantenimiento, el
 *            taller puede agregar nuevos tipos de repuesto en la BD sin
 *            recompilar; por eso es una clase leída por DAO y no un enum (OCP).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
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
