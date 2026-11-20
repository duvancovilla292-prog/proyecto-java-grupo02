/*
 * ============================================================
 * Archivo: TipoMantenimiento.java
 * Propósito: representar la tabla de consulta mantenimiento_tipos.
 * Conceptos clave: enum con atributos y constructor (S10Demo02 DiaSemana),
 *            values() para buscar por id.
 * Justificación: los tipos son fijos (Preventivo/Correctivo); un enum
 *            impide valores inválidos en tiempo de compilación y su id
 *            coincide con la fila de la base de datos (data.sql).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

// Visto en clase: enum con atributos, values() y == -> S10Demo02 DiaSemana y S10Demo02EnumMain (Sesión 10)
public enum TipoMantenimiento {
    PREVENTIVO(1, "Preventivo"),
    CORRECTIVO(2, "Correctivo");

    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final int id;              // Id en mantenimiento_tipos
    private final String descripcion;  // Texto para mostrar

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
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

    // Visto en clase: métodos y miembros static -> S05Demo02ConversorTemperatura (Sesión 05)
    /** Convierte el id leído de la BD en el enum; lanza DatoInvalidoException si no existe. */
    public static TipoMantenimiento desdeId(int id) {
        // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
        for (TipoMantenimiento tipo : values()) {
            // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
            if (tipo.id == id) {
                return tipo;
            }
        }
        // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
        throw new DatoInvalidoException("Tipo de mantenimiento desconocido: " + id);
    }
}
