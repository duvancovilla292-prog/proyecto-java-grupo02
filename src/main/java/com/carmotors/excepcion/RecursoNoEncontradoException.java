/*
 * ============================================================
 * Archivo: RecursoNoEncontradoException.java
 * Propósito: el registro buscado no existe (cliente, repuesto, orden, técnico...).
 * Conceptos clave: excepción propia (S10Demo03 DatoObligatorioException),
 *            herencia (sesión 07), @Serial serialVersionUID.
 * Justificación: separarla de DatoInvalido deja claro en el mensaje y en el código
 *            que el dato era correcto pero no hay coincidencias en la base de datos
 *            (equivale al "Sin coincidencias para ese id" de S18Demo00).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.excepcion;

import java.io.Serial;

// Visto en clase: herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
// Visto en clase: excepción propia + @Serial serialVersionUID -> S10Demo03 DatoObligatorioException (Sesión 10)
public class RecursoNoEncontradoException extends CarMotorsException {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    @Serial
    private static final long serialVersionUID = 1L;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
