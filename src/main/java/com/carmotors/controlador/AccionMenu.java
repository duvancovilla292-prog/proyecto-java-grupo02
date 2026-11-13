/*
 * ============================================================
 * Archivo: AccionMenu.java
 * Propósito: representar una opción de menú ejecutable.
 * Conceptos clave: PATRÓN COMMAND (S14Demo03: comando que encapsula una
 *            acción), INTERFAZ FUNCIONAL con @FunctionalInterface (S19Demo00
 *            ReglaMonto) para registrar cada comando con una lambda sin
 *            parámetros, ej. () -> registrarCliente() (como el Supplier de S19Demo01).
 * Justificación: a diferencia de Runnable, permite lanzar SQLException e
 *            IOException; así cada acción delega el manejo de errores en
 *            un único lugar (ControladorBase) en vez de repetir try/catch.
 * ============================================================
 */
package com.carmotors.controlador;

import java.io.IOException;
import java.sql.SQLException;

@FunctionalInterface
public interface AccionMenu {
    void ejecutar() throws SQLException, IOException;
}
