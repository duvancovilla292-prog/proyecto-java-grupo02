/*
 * ============================================================
 * Archivo: Listado.java
 * Propósito: representar una lista de referencia que se muestra antes de
 *            pedir un dato (id, placa, documento, código de lote...).
 * Conceptos clave: INTERFAZ FUNCIONAL con @FunctionalInterface (S19Demo00
 *            ReglaMonto) para pasar el listado con una lambda sin
 *            parámetros, ej. () -> vista.mostrarTipos(servicio.listarTipos()).
 * Justificación: a diferencia de Runnable, permite lanzar SQLException,
 *            porque cada listado consulta la base de datos. La usa
 *            ControladorBase.referencia(...).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.controlador;

import java.sql.SQLException;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: interfaz funcional @FunctionalInterface -> S19Demo00 ReglaMonto (Sesión 19)
@FunctionalInterface
public interface Listado {
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    void mostrar() throws SQLException;
}
