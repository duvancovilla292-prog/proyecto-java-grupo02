/*
 * ============================================================
 * Archivo: ReglaNegocioException.java
 * Propósito: operación rechazada por una regla del taller (stock insuficiente, transición de estado inválida, duplicados).
 * Conceptos clave: excepción propia (S10Demo03 DatoObligatorioException),
 *            herencia (sesión 07), @Serial serialVersionUID.
 * Justificación: los datos son válidos pero la operación no está permitida en el
 *            estado actual; la lanzan los servicios y el patrón State.
 * ============================================================
 */
package com.carmotors.excepcion;

import java.io.Serial;

public class ReglaNegocioException extends CarMotorsException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
