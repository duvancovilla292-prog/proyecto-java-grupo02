/*
 * ============================================================
 * Archivo: MenuPrincipalController.java
 * Propósito: menú raíz que enruta a cada módulo del sistema.
 * Conceptos clave: MVC - Controlador, COMMAND (cada opción es el método
 *            iniciar() de un submódulo: referencia a método de otro objeto),
 *            hooks del TEMPLATE METHOD (alIniciar/alSalir), polimorfismo.
 * Justificación: no conoce la lógica de ningún módulo ni los textos del menú
 *            (los da la vista MenuPrincipal); agregar un módulo nuevo es
 *            registrar una línea más (OCP).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
 *   - @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
 *   - modificador protected -> S07Demo00 Usuario.describirIdentidad (Sesión 07)
 *   - PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.vista.MenuPrincipal;
import com.carmotors.vista.VistaConsola;

public class MenuPrincipalController extends ControladorBase {
    private final MenuPrincipal vista;

    public MenuPrincipalController(VistaConsola consola, MenuPrincipal vista, ControladorBase inventario,
                                   ControladorBase servicios, ControladorBase clientes,
                                   ControladorBase facturacion, ControladorBase proveedores,
                                   ControladorBase reportes) {
        super(consola, vista);
        this.vista = vista;
        registrar("1", () -> inventario.iniciar());
        registrar("2", () -> servicios.iniciar());
        registrar("3", () -> clientes.iniciar());
        registrar("4", () -> facturacion.iniciar());
        registrar("5", () -> proveedores.iniciar());
        registrar("6", () -> reportes.iniciar());
    }

    @Override
    protected void alIniciar() {
        vista.mostrarBienvenida();
    }

    @Override
    protected void alSalir() {
        vista.mostrarDespedida();
    }
}
