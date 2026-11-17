/*
 * ============================================================
 * Archivo: EstadoPendiente.java
 * Propósito: estado concreto "Pendiente (recibida, sin iniciar)" de una orden de servicio.
 * Conceptos clave: PATRÓN STATE (S14Demo04: Creado/Pagado/Enviado/Entregado),
 *            implementación de interfaz con @Override (sesión 07).
 * Justificación: cada estado conoce SOLO su sucesor y si admite cambios;
 *            así no hay un switch gigante de transiciones en el servicio
 *            y agregar un estado nuevo no modifica los existentes (OCP).
 * ============================================================
 */
package com.carmotors.servicio.estado;

import com.carmotors.modelo.EstadoServicio;

public class EstadoPendiente implements EstadoOrden {
    @Override
    public EstadoServicio getCodigo() {
        return EstadoServicio.PENDIENTE;
    }

    @Override
    public EstadoOrden avanzar() {
        return new EstadoEnProceso();
    }

    @Override
    public boolean permiteAsignaciones() {
        return true;
    }
}
