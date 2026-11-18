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
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.vista.MenuPrincipal;
import com.carmotors.vista.VistaConsola;

public class MenuPrincipalController extends ControladorBase {
    private final MenuPrincipal vista;

    public MenuPrincipalController(VistaConsola consola, MenuPrincipal vista, ControladorBase inventario,
                                   ControladorBase servicios, ControladorBase clientes,
                                   ControladorBase facturacion, ControladorBase proveedores) {
        super(consola, vista);
        this.vista = vista;
        registrar("1", () -> inventario.iniciar());
        registrar("2", () -> servicios.iniciar());
        registrar("3", () -> clientes.iniciar());
        registrar("4", () -> facturacion.iniciar());
        registrar("5", () -> proveedores.iniciar());
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
