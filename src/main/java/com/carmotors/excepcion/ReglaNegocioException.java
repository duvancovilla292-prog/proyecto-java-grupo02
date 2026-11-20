/*
 * ============================================================
 * Archivo: ReglaNegocioException.java
 * Propósito: operación rechazada por una regla del taller (stock insuficiente, transición de estado inválida, duplicados).
 * Conceptos clave: excepción propia (S10Demo03 DatoObligatorioException),
 *            herencia (sesión 07), @Serial serialVersionUID.
 * Justificación: los datos son válidos pero la operación no está permitida en el
 *            estado actual; la lanzan los servicios y el patrón State.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.excepcion;

import java.io.Serial;

// Visto en clase: herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
// Visto en clase: excepción propia + @Serial serialVersionUID -> S10Demo03 DatoObligatorioException (Sesión 10)
public class ReglaNegocioException extends CarMotorsException {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    @Serial
    private static final long serialVersionUID = 1L;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
