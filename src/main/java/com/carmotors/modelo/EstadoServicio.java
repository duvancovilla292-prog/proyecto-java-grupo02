/*
 * ============================================================
 * Archivo: EstadoServicio.java
 * Propósito: representar la tabla de consulta servicio_estados
 *            (Pendiente, En Proceso, Completado, Facturado, Entregado).
 * Conceptos clave: enum con atributos (S10Demo02), búsqueda con values().
 * Justificación: el enum solo IDENTIFICA el estado y lo traduce a/desde la BD.
 *            Las reglas de transición viven en el patrón State
 *            (paquete servicio.estado) para respetar SRP y OCP.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

// Visto en clase: enum con atributos, values() y == -> S10Demo02 DiaSemana y S10Demo02EnumMain (Sesión 10)
public enum EstadoServicio {
    PENDIENTE(1, "Pendiente"),
    EN_PROCESO(2, "En Proceso"),
    COMPLETADO(3, "Completado"),
    FACTURADO(4, "Facturado"),
    ENTREGADO(5, "Entregado");

    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final int id;
    private final String descripcion;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    EstadoServicio(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** true si el trabajo técnico ya terminó (cuenta para reportes y recordatorios). */
    public boolean trabajoTerminado() {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        return this == COMPLETADO || this == FACTURADO || this == ENTREGADO;
    }

    // Visto en clase: métodos y miembros static -> S05Demo02ConversorTemperatura (Sesión 05)
    public static EstadoServicio desdeId(int id) {
        // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
        for (EstadoServicio estado : values()) {
            // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
            if (estado.id == id) {
                return estado;
            }
        }
        // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
        throw new DatoInvalidoException("Estado de servicio desconocido: " + id);
    }
}
