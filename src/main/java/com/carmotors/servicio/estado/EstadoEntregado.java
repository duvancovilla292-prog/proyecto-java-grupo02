/*
 * ============================================================
 * Archivo: EstadoEntregado.java
 * Propósito: estado concreto "Entregado (estado final)" de una orden de servicio.
 * Conceptos clave: PATRÓN STATE (S14Demo04: Creado/Pagado/Enviado/Entregado),
 *            implementación de interfaz con @Override (sesión 07).
 * Justificación: cada estado conoce SOLO su sucesor y si admite cambios;
 *            así no hay un switch gigante de transiciones en el servicio
 *            y agregar un estado nuevo no modifica los existentes (OCP).
 * ============================================================
 */
package com.carmotors.servicio.estado;

import com.carmotors.excepcion.ReglaNegocioException;
import com.carmotors.modelo.EstadoServicio;

public class EstadoEntregado implements EstadoOrden {
    @Override
    public EstadoServicio getCodigo() {
        return EstadoServicio.ENTREGADO;
    }

    @Override
    public EstadoOrden avanzar() {
        throw new ReglaNegocioException("La orden ya fue entregada; no tiene más estados.");
    }

    @Override
    public boolean permiteAsignaciones() {
        return false;
    }
}
