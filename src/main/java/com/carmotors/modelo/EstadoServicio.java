/*
 * ============================================================
 * Archivo: EstadoServicio.java
 * Propósito: representar la tabla de consulta servicio_estados
 *            (Pendiente, En Proceso, Completado, Facturado, Entregado).
 * Conceptos clave: enum con atributos (S10Demo02), búsqueda con values().
 * Justificación: el enum solo IDENTIFICA el estado y lo traduce a/desde la BD.
 *            Las reglas de transición viven en el patrón State
 *            (paquete servicio.estado) para respetar SRP y OCP.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public enum EstadoServicio {
    PENDIENTE(1, "Pendiente"),
    EN_PROCESO(2, "En Proceso"),
    COMPLETADO(3, "Completado"),
    FACTURADO(4, "Facturado"),
    ENTREGADO(5, "Entregado");

    private final int id;
    private final String descripcion;

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
        return this == COMPLETADO || this == FACTURADO || this == ENTREGADO;
    }

    public static EstadoServicio desdeId(int id) {
        for (EstadoServicio estado : values()) {
            if (estado.id == id) {
                return estado;
            }
        }
        throw new DatoInvalidoException("Estado de servicio desconocido: " + id);
    }
}
