/*
 * ============================================================
 * Archivo: EstadoPendiente.java
 * Propósito: estado concreto "Pendiente (recibida, sin iniciar)" de una orden de servicio.
 * Conceptos clave: PATRÓN STATE (S14Demo04: Creado/Pagado/Enviado/Entregado),
 *            implementación de interfaz con @Override (sesión 07).
 * Justificación: cada estado conoce SOLO su sucesor y si admite cambios;
 *            así no hay un switch gigante de transiciones en el servicio
 *            y agregar un estado nuevo no modifica los existentes (OCP).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.servicio.estado;

import com.carmotors.modelo.EstadoServicio;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN STATE -> S14Demo04 EstadoPedido (Sesión 14)
public class EstadoPendiente implements EstadoOrden {
    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
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
