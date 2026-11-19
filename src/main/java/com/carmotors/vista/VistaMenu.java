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
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.vista;

import java.util.Map;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
public interface VistaMenu {
    /** Título que se muestra sobre el menú. */
    String titulo();

    // Visto en clase: Map / HashMap / LinkedHashMap / Map.Entry -> S10Demo01MapMain (Sesión 10)
    /** Opciones en orden: número -> texto. */
    Map<String, String> opciones();

    /** Texto de la opción 0 ("Volver" o "Salir"). */
    String textoSalida();
}
