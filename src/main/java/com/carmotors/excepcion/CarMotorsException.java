/*
 * ============================================================
 * Archivo: CarMotorsException.java
 * Propósito: excepción base de todos los errores controlados del sistema.
 * Conceptos clave: excepción propia (S10Demo03 DatoObligatorioException),
 *            herencia (sesión 07), @Serial serialVersionUID.
 * Justificación: una jerarquía propia permite que los controladores atrapen TODOS los
 *            errores de negocio con un solo catch y los muestre como mensaje,
 *            sin confundirlos con fallos inesperados. Es no verificada
 *            (RuntimeException) para poder lanzarse también desde
 *            constructores del modelo y desde lambdas de los Streams.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.excepcion;

import java.io.Serial;

// Visto en clase: herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
// Visto en clase: excepción propia + @Serial serialVersionUID -> S10Demo03 DatoObligatorioException (Sesión 10)
// Visto en clase: excepción propia que extiende RuntimeException -> S14Demo01 FalloNotificacionException (Sesión 14)
public class CarMotorsException extends RuntimeException {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    @Serial
    private static final long serialVersionUID = 1L;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public CarMotorsException(String mensaje) {
        super(mensaje);
    }

    /** Conserva la causa original (por ejemplo, una IOException al leer la configuración). */
    public CarMotorsException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
