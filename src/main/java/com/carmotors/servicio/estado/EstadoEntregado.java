/*
 * ============================================================
 * Archivo: EstadoEntregado.java
 * Propósito: estado concreto "Entregado (estado final)" de una orden de servicio.
 * Conceptos clave: PATRÓN STATE (S14Demo04: Creado/Pagado/Enviado/Entregado),
 *            implementación de interfaz con @Override (sesión 07).
 * Justificación: cada estado conoce SOLO su sucesor y si admite cambios;
 *            así no hay un switch gigante de transiciones en el servicio
 *            y agregar un estado nuevo no modifica los existentes (OCP).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
 *   - interfaces e implements -> S07Demo04 Notificable (Sesión 07)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN STATE -> S14Demo04 EstadoPedido (Sesión 14)
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
