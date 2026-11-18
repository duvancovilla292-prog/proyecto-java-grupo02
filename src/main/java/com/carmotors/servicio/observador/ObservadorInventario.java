/*
 * ============================================================
 * Archivo: ObservadorInventario.java
 * Propósito: contrato de quien quiere enterarse cuando un repuesto queda
 *            por debajo de su stock mínimo.
 * Conceptos clave: PATRÓN OBSERVER (S14Demo01 PublicadorPedidos), interfaz.
 * Justificación: el inventario NO sabe quién reacciona a una alerta
 *            (consola hoy, correo mañana); solo notifica (bajo acoplamiento).
 * Visto en clase (demos del curso):
 *   - interfaces e implements -> S07Demo04 Notificable (Sesión 07)
 *   - PATRÓN OBSERVER -> S14Demo01 PublicadorPedidos (Sesión 14)
 * ============================================================
 */
package com.carmotors.servicio.observador;

import com.carmotors.modelo.Repuesto;

public interface ObservadorInventario {
    void stockBajo(Repuesto repuesto);
}
