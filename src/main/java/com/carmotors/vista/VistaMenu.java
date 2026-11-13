/*
 * ============================================================
 * Archivo: VistaMenu.java
 * Propósito: contrato de toda vista que muestra un menú: título, opciones
 *            numeradas y texto de la opción 0.
 * Conceptos clave: interfaz (S07Demo04 Notificable), Map ordenado
 *            (LinkedHashMap, S10Demo01), polimorfismo.
 * Justificación: los TEXTOS de los menús pertenecen a la vista. El
 *            controlador solo asocia cada número con una acción (Command) y
 *            ControladorBase pide el menú a esta interfaz, sin conocer qué
 *            vista concreta lo dibuja (Inversión de Dependencias).
 * ============================================================
 */
package com.carmotors.vista;

import java.util.Map;

public interface VistaMenu {
    /** Título que se muestra sobre el menú. */
    String titulo();

    /** Opciones en orden: número -> texto. */
    Map<String, String> opciones();

    /** Texto de la opción 0 ("Volver" o "Salir"). */
    String textoSalida();
}
