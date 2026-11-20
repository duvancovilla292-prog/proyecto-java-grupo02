/*
 * ============================================================
 * Archivo: Main.java
 * Propósito: punto de entrada (main): crea lo que necesita la vista (la
 *            consola y el menú principal), comprueba la conexión y arranca
 *            el menú.
 * Conceptos clave: try-with-resources sobre Scanner, MVC (Main solo crea la
 *            vista raíz y su controlador).
 * Justificación: Main no conoce DAO, servicios ni constantes de
 *            configuración. Las credenciales viven en el Singleton
 *            ConexionFactory y el IVA y la carpeta de facturas en las
 *            constantes de ConfiguracionSistema; el menú
 *            principal crea la vista de cada módulo y se la pasa a su
 *            controlador, que crea su servicio (y cada servicio sus DAO), en
 *            el momento en que el usuario entra al módulo.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors;

import com.carmotors.controlador.MenuPrincipalController;
import com.carmotors.vista.MenuPrincipal;
import com.carmotors.vista.VistaConsola;

import java.util.Scanner;

// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public class Main {
    public static void main(String[] args) {
        // Visto en clase: Scanner y hasNextLine -> S03Scanner (Sesión 03) y S18Demo01 VistaConsola (Sesión 18)
        // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
        try (Scanner entrada = new Scanner(System.in)) {
            VistaConsola consola = new VistaConsola(entrada);
            MenuPrincipalController menu = new MenuPrincipalController(consola, new MenuPrincipal(consola));
            // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
            if (menu.conectar()) {
                menu.iniciar();  // bienvenida -> menú -> despedida (Template Method con hooks)
            }
        }
    }
}
