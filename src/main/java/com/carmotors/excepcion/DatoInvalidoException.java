/*
 * ============================================================
 * Archivo: DatoInvalidoException.java
 * Propósito: dato con formato o valor inválido (vacío, negativo, fuera de rango, texto que no es número).
 * Conceptos clave: excepción propia (S10Demo03 DatoObligatorioException),
 *            herencia (sesión 07), @Serial serialVersionUID.
 * Justificación: la lanzan el modelo (validación en constructores), el Builder y la
 *            vista al convertir texto; indica que el usuario debe corregir lo que escribió.
 * ============================================================
 */
package com.carmotors.excepcion;

import java.io.Serial;

public class DatoInvalidoException extends CarMotorsException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
