/*
 * ============================================================
 * Archivo: ControladorBase.java
 * Propósito: comportamiento común de todos los controladores: pedir a la
 *            vista que muestre su menú, leer la opción, ejecutar la acción
 *            asociada manejando los errores y mostrar listas de referencia
 *            antes de pedir un dato (referencia + interfaz Listado).
 * Conceptos clave: MVC - Controlador, PATRÓN TEMPLATE METHOD (iniciar() es
 *            final, con hooks alIniciar/alSalir), PATRÓN COMMAND
 *            (Map<String, AccionMenu>), interfaz VistaMenu (los TEXTOS del
 *            menú los da la vista), multi-catch y excepciones propias (sesión 10).
 * Justificación: el ciclo "mostrar menú -> leer opción -> ejecutar -> manejar
 *            error" es idéntico en los 6 controladores; escribirlo una vez
 *            elimina duplicación (DRY). El controlador NO conoce los textos del
 *            menú: solo asocia número -> acción, así la vista concentra toda la
 *            interacción con el usuario.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
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

// Visto en clase: clase abstracta y métodos abstractos -> S07Demo03 Poliza (Sesión 07)
// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public abstract class ControladorBase {
    // SQLState de MySQL para violaciones de integridad (clave duplicada o registros asociados).
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private static final String VIOLACION_INTEGRIDAD = "23000";

    // Visto en clase: modificador protected -> S07Demo00 Usuario.describirIdentidad (Sesión 07)
    protected final VistaConsola consola;
    // Visto en clase: Map / HashMap / LinkedHashMap / Map.Entry -> S10Demo01MapMain (Sesión 10)
    // Visto en clase: PATRÓN COMMAND -> S14Demo03 GestorComandos (Sesión 14)
    private final VistaMenu menu;                                // vista que dibuja el menú
    private final Map<String, AccionMenu> acciones = new HashMap<>(); // opción -> comando

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    protected ControladorBase(VistaConsola consola, VistaMenu menu) {
        this.consola = consola;
        this.menu = menu;
    }

    /** Asocia el número de una opción del menú (definida en la vista) con su comando. */
    protected final void registrar(String opcion, AccionMenu accion) {
        acciones.put(opcion, accion);
    }

    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    /**
     * Antes de pedir un id, placa, documento o código que el usuario no sabe
     * de memoria, muestra las opciones válidas en un bloque separado:
     * título -> listado -> línea separadora -> (luego el controlador pregunta).
     */
    protected final void referencia(String titulo, Listado listado) throws SQLException {
        consola.mostrarSeccion(titulo);
        listado.mostrar();
        consola.mostrarSeparador();
    }

    // Visto en clase: PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
    /** Hook opcional: se ejecuta una vez antes de mostrar el menú. */
    protected void alIniciar() {
    }

    /** Hook opcional: se ejecuta una vez cuando el usuario sale con 0. */
    protected void alSalir() {
    }

    /** Método plantilla: alIniciar -> ciclo del menú hasta elegir 0 -> alSalir. */
    public final void iniciar() {
        alIniciar();
        // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
        while (true) {
            String opcion = consola.pedirOpcion(menu.titulo(), menu.opciones(), menu.textoSalida());
            // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
            if (opcion.equals("0")) {
                alSalir();
                return;
            }
            AccionMenu accion = acciones.get(opcion);
            // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
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
