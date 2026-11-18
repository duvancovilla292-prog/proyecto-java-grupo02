/*
 * ============================================================
 * Archivo: RecursoNoEncontradoException.java
 * Propósito: el registro buscado no existe (cliente, repuesto, orden, técnico...).
 * Conceptos clave: excepción propia (S10Demo03 DatoObligatorioException),
 *            herencia (sesión 07), @Serial serialVersionUID.
 * Justificación: separarla de DatoInvalido deja claro en el mensaje y en el código
 *            que el dato era correcto pero no hay coincidencias en la base de datos
 *            (equivale al "Sin coincidencias para ese id" de S18Demo00).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
 *   - excepción propia + @Serial serialVersionUID -> S10Demo03 DatoObligatorioException (Sesión 10)
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
