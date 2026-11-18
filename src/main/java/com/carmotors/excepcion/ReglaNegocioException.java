/*
 * ============================================================
 * Archivo: ReglaNegocioException.java
 * Propósito: operación rechazada por una regla del taller (stock insuficiente, transición de estado inválida, duplicados).
 * Conceptos clave: excepción propia (S10Demo03 DatoObligatorioException),
 *            herencia (sesión 07), @Serial serialVersionUID.
 * Justificación: los datos son válidos pero la operación no está permitida en el
 *            estado actual; la lanzan los servicios y el patrón State.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
 *   - excepción propia + @Serial serialVersionUID -> S10Demo03 DatoObligatorioException (Sesión 10)
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
