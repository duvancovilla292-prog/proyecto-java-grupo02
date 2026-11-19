/*
 * ============================================================
 * Archivo: Main.java
 * Propósito: punto de entrada (main): crea lo que necesita la vista (la
 *            consola y el menú principal), comprueba la conexión y arranca
 *            el menú.
 * Conceptos clave: try-with-resources sobre Scanner, MVC (Main solo crea la
 *            vista raíz y su controlador).
 * Justificación: Main no conoce DAO, servicios ni constantes de
 *            configuración. La configuración (credenciales, IVA, carpeta de
 *            facturas) vive en el Singleton ConexionFactory; el menú
 *            principal crea la vista de cada módulo y se la pasa a su
 *            controlador, que crea su servicio (y cada servicio sus DAO), en
 *            el momento en que el usuario entra al módulo.
 * Visto en clase (demos del curso):
 *   - Scanner y hasNextLine -> S03Scanner (Sesión 03) y S18Demo01 VistaConsola (Sesión 18)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 * ============================================================
 */
package com.carmotors;

import com.carmotors.controlador.MenuPrincipalController;
import com.carmotors.vista.MenuPrincipal;
import com.carmotors.vista.VistaConsola;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            VistaConsola consola = new VistaConsola(entrada);
            MenuPrincipalController menu = new MenuPrincipalController(consola, new MenuPrincipal(consola));
            if (menu.conectar()) {
                menu.iniciar();  // bienvenida -> menú -> despedida (Template Method con hooks)
            }
        }
    }
}
