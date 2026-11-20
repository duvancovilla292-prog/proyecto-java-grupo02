/*
 * ============================================================
 * Archivo: EstadoOrden.java
 * Propósito: contrato común de los estados del ciclo de vida de una orden:
 *            Pendiente -> En Proceso -> Completado -> Facturado -> Entregado.
 * Conceptos clave: PATRÓN STATE (S14Demo04 EstadoPedido), interfaz (sesión 07),
 *            polimorfismo.
 * Justificación: el servicio pregunta al estado actual "¿cuál es tu siguiente?"
 *            y "¿aceptas técnicos/repuestos?" sin conocer cada caso; una
 *            transición inválida (ej. entregar sin facturar) se rechaza aquí.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.servicio.estado;

import com.carmotors.excepcion.ReglaNegocioException;
import com.carmotors.modelo.EstadoServicio;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN STATE -> S14Demo04 EstadoPedido (Sesión 14)
public interface EstadoOrden {
    /** Estado persistible que representa este objeto. */
    EstadoServicio getCodigo();

    /** Devuelve el estado siguiente; lanza ReglaNegocioException si no existe. */
    EstadoOrden avanzar();

    /** true si todavía se pueden asignar técnicos y repuestos. */
    boolean permiteAsignaciones();
}
