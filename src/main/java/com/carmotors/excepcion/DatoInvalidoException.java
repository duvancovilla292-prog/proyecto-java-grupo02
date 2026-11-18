/*
 * ============================================================
 * Archivo: DatoInvalidoException.java
 * Propósito: dato con formato o valor inválido (vacío, negativo, fuera de rango, texto que no es número).
 * Conceptos clave: excepción propia (S10Demo03 DatoObligatorioException),
 *            herencia (sesión 07), @Serial serialVersionUID.
 * Justificación: la lanzan el modelo (validación en constructores), el Builder y la
 *            vista al convertir texto; indica que el usuario debe corregir lo que escribió.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
 *   - excepción propia + @Serial serialVersionUID -> S10Demo03 DatoObligatorioException (Sesión 10)
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
