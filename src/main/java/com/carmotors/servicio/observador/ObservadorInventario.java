/*
 * ============================================================
 * Archivo: ObservadorInventario.java
 * Propósito: contrato de quien quiere enterarse cuando un repuesto queda
 *            por debajo de su stock mínimo.
 * Conceptos clave: PATRÓN OBSERVER (S14Demo01 PublicadorPedidos), interfaz.
 * Justificación: el inventario NO sabe quién reacciona a una alerta
 *            (consola hoy, correo mañana); solo notifica (bajo acoplamiento).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.servicio.observador;

import com.carmotors.modelo.Repuesto;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN OBSERVER -> S14Demo01 PublicadorPedidos (Sesión 14)
public interface ObservadorInventario {
    void stockBajo(Repuesto repuesto);
}
