/*
 * ============================================================
 * Archivo: EstadoCompletado.java
 * Propósito: estado concreto "Completado (trabajo terminado, stock deducido)" de una orden de servicio.
 * Conceptos clave: PATRÓN STATE (S14Demo04: Creado/Pagado/Enviado/Entregado),
 *            implementación de interfaz con @Override (sesión 07).
 * Justificación: cada estado conoce SOLO su sucesor y si admite cambios;
 *            así no hay un switch gigante de transiciones en el servicio
 *            y agregar un estado nuevo no modifica los existentes (OCP).
 * ============================================================
 */
package com.carmotors.servicio.estado;

import com.carmotors.modelo.EstadoServicio;

public class EstadoCompletado implements EstadoOrden {
    @Override
    public EstadoServicio getCodigo() {
        return EstadoServicio.COMPLETADO;
    }

    @Override
    public EstadoOrden avanzar() {
        return new EstadoFacturado();
    }

    @Override
    public boolean permiteAsignaciones() {
        return false;
    }
}
