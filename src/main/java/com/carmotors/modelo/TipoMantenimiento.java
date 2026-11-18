/*
 * ============================================================
 * Archivo: TipoMantenimiento.java
 * Propósito: representar la tabla de consulta mantenimiento_tipos.
 * Conceptos clave: enum con atributos y constructor (S10Demo02 DiaSemana),
 *            values() para buscar por id.
 * Justificación: los tipos son fijos (Preventivo/Correctivo); un enum
 *            impide valores inválidos en tiempo de compilación y su id
 *            coincide con la fila de la base de datos (data.sql).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - métodos y miembros static -> S05Demo02ConversorTemperatura (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - enum con atributos, values() y == -> S10Demo02 DiaSemana y S10Demo02EnumMain (Sesión 10)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
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
