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
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.controlador;

import java.io.IOException;
import java.sql.SQLException;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN COMMAND -> S14Demo03 GestorComandos (Sesión 14)
// Visto en clase: interfaz funcional @FunctionalInterface -> S19Demo00 ReglaMonto (Sesión 19)
@FunctionalInterface
public interface AccionMenu {
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    void ejecutar() throws SQLException, IOException;
}
