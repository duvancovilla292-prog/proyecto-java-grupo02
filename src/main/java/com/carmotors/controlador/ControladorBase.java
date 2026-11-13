/*
 * ============================================================
 * Archivo: ControladorBase.java
 * Propósito: comportamiento común de todos los controladores: pedir a la
 *            vista que muestre su menú, leer la opción y ejecutar la acción
 *            asociada manejando los errores.
 * Conceptos clave: MVC - Controlador, PATRÓN TEMPLATE METHOD (iniciar() es
 *            final, con hooks alIniciar/alSalir), PATRÓN COMMAND
 *            (Map<String, AccionMenu>), interfaz VistaMenu (los TEXTOS del
 *            menú los da la vista), multi-catch y excepciones propias (sesión 10).
 * Justificación: el ciclo "mostrar menú -> leer opción -> ejecutar -> manejar
 *            error" es idéntico en los 6 controladores; escribirlo una vez
 *            elimina duplicación (DRY). El controlador NO conoce los textos del
 *            menú: solo asocia número -> acción, así la vista concentra toda la
 *            interacción con el usuario.
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.excepcion.CarMotorsException;
import com.carmotors.vista.VistaConsola;
import com.carmotors.vista.VistaMenu;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class ControladorBase {
    // SQLState de MySQL para violaciones de integridad (clave duplicada o registros asociados).
    private static final String VIOLACION_INTEGRIDAD = "23000";

    protected final VistaConsola consola;
    private final VistaMenu menu;                                // vista que dibuja el menú
    private final Map<String, AccionMenu> acciones = new HashMap<>(); // opción -> comando

    protected ControladorBase(VistaConsola consola, VistaMenu menu) {
        this.consola = consola;
        this.menu = menu;
    }

    /** Asocia el número de una opción del menú (definida en la vista) con su comando. */
    protected final void registrar(String opcion, AccionMenu accion) {
        acciones.put(opcion, accion);
    }

    /** Hook opcional: se ejecuta una vez antes de mostrar el menú. */
    protected void alIniciar() {
    }

    /** Hook opcional: se ejecuta una vez cuando el usuario sale con 0. */
    protected void alSalir() {
    }

    /** Método plantilla: alIniciar -> ciclo del menú hasta elegir 0 -> alSalir. */
    public final void iniciar() {
        alIniciar();
        while (true) {
            String opcion = consola.pedirOpcion(menu.titulo(), menu.opciones(), menu.textoSalida());
            if (opcion.equals("0")) {
                alSalir();
                return;
            }
            AccionMenu accion = acciones.get(opcion);
            if (accion == null || !menu.opciones().containsKey(opcion)) {
                consola.mostrarError("Opción no válida.");
            } else {
                ejecutarSeguro(accion);
            }
        }
    }

    // Un error nunca cierra la aplicación: se informa y se vuelve al menú.
    private void ejecutarSeguro(AccionMenu accion) {
        try {
            accion.ejecutar();
        } catch (SQLException error) {
            if (VIOLACION_INTEGRIDAD.equals(error.getSQLState())) {
                consola.mostrarError("La base de datos rechazó la operación: el dato ya existe o tiene "
                        + "registros asociados.");
            } else {
                consola.mostrarError("Operación de base de datos no completada (SQLState " + error.getSQLState()
                        + "): " + error.getMessage());
            }
        } catch (IOException error) {
            consola.mostrarError("No se pudo escribir el archivo: " + error.getMessage());
        } catch (CarMotorsException error) {
            consola.mostrarError(error.getMessage());
        }
    }
}
