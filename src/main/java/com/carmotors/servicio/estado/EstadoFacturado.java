/*
 * ============================================================
 * Archivo: EstadoFacturado.java
 * Propósito: estado concreto "Facturado (factura emitida)" de una orden de servicio.
 * Conceptos clave: PATRÓN STATE (S14Demo04: Creado/Pagado/Enviado/Entregado),
 *            implementación de interfaz con @Override (sesión 07).
 * Justificación: cada estado conoce SOLO su sucesor y si admite cambios;
 *            así no hay un switch gigante de transiciones en el servicio
 *            y agregar un estado nuevo no modifica los existentes (OCP).
 * ============================================================
 */
package com.carmotors.servicio.estado;

import com.carmotors.modelo.EstadoServicio;

public class EstadoFacturado implements EstadoOrden {
    @Override
    public EstadoServicio getCodigo() {
        return EstadoServicio.FACTURADO;
    }

    @Override
    public EstadoOrden avanzar() {
        return new EstadoEntregado();
    }

    @Override
    public boolean permiteAsignaciones() {
        return false;
    }
}
