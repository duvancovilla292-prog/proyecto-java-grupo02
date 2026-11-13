/*
 * ============================================================
 * Archivo: RecursoNoEncontradoException.java
 * Propósito: el registro buscado no existe (cliente, repuesto, orden, técnico...).
 * Conceptos clave: excepción propia (S10Demo03 DatoObligatorioException),
 *            herencia (sesión 07), @Serial serialVersionUID.
 * Justificación: separarla de DatoInvalido deja claro en el mensaje y en el código
 *            que el dato era correcto pero no hay coincidencias en la base de datos
 *            (equivale al "Sin coincidencias para ese id" de S18Demo00).
 * ============================================================
 */
package com.carmotors.excepcion;

import java.io.Serial;

public class RecursoNoEncontradoException extends CarMotorsException {
    @Serial
    private static final long serialVersionUID = 1L;

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
