/*
 * ============================================================
 * Archivo: ConfiguracionException.java
 * Propósito: falta o es ilegible la configuración de conexión a la base de datos.
 * Conceptos clave: excepción propia (S10Demo03 DatoObligatorioException),
 *            herencia (sesión 07), @Serial serialVersionUID.
 * Justificación: el arranque de la aplicación la atrapa para explicar que deben completarse
 *            las constantes de conexión en lugar de mostrar un error técnico.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.excepcion;

import java.io.Serial;

// Visto en clase: herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
// Visto en clase: excepción propia + @Serial serialVersionUID -> S10Demo03 DatoObligatorioException (Sesión 10)
public class ConfiguracionException extends CarMotorsException {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    @Serial
    private static final long serialVersionUID = 1L;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public ConfiguracionException(String mensaje) {
        super(mensaje);
    }

    public ConfiguracionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
