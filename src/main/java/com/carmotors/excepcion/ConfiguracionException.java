/*
 * ============================================================
 * Archivo: ConfiguracionException.java
 * Propósito: falta o es ilegible la configuración de conexión a la base de datos.
 * Conceptos clave: excepción propia (S10Demo03 DatoObligatorioException),
 *            herencia (sesión 07), @Serial serialVersionUID.
 * Justificación: el arranque de la aplicación la atrapa para explicar que deben completarse
 *            las constantes de conexión en lugar de mostrar un error técnico.
 * ============================================================
 */
package com.carmotors.excepcion;

import java.io.Serial;

public class ConfiguracionException extends CarMotorsException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ConfiguracionException(String mensaje) {
        super(mensaje);
    }

    public ConfiguracionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
