/*
 * ============================================================
 * Archivo: MenuPrincipal.java
 * Propósito: vista del menú raíz del sistema: pantalla de bienvenida con
 *            los módulos disponibles, mensajes de arranque y despedida.
 * Conceptos clave: MVC - Vista, implementa VistaMenu (interfaz), composición
 *            (usa VistaConsola para imprimir), List.of y for-each (sesión 08).
 * Justificación: el texto de presentación de la aplicación es
 *            responsabilidad de la vista, no del arranque ni del controlador.
 *            El controlador del menú principal decide CUÁNDO mostrarlo (hooks
 *            alIniciar/alSalir) y esta clase decide CÓMO se ve (SRP).
 * ============================================================
 */
package com.carmotors.vista;

import java.util.List;
import java.util.Map;

public class MenuPrincipal implements VistaMenu {
    // Descripción breve de cada módulo, en el mismo orden del menú.
    private static final List<String> MODULOS = List.of(
            "Inventario: repuestos, lotes, alertas de stock mínimo y caducidad",
            "Servicios: órdenes, técnicos, repuestos reservados y ciclo de vida",
            "Clientes: perfiles, vehículos, historial y recordatorios preventivos",
            "Facturación: factura en texto plano y reportes gerenciales",
            "Proveedores: catálogo de precios y evaluación de desempeño");

    // Texto de las opciones del menú raíz (mismo orden que MODULOS).
    private static final List<String> OPCIONES = List.of(
            "Inventario de repuestos",
            "Órdenes de servicio y técnicos",
            "Clientes y vehículos",
            "Facturación y reportes gerenciales",
            "Proveedores");

    private final VistaConsola consola;

    public MenuPrincipal(VistaConsola consola) {
        this.consola = consola;
    }

    @Override
    public String titulo() {
        return "CarMotors - Sistema de Gestión de Taller";
    }

    @Override
    public Map<String, String> opciones() {
        return VistaConsola.numerar(OPCIONES);
    }

    @Override
    public String textoSalida() {
        return "Salir";
    }

    /** Mensaje mientras se comprueba la base de datos. */
    public void mostrarInicio() {
        consola.mostrar("Iniciando CarMotors SGT...");
    }

    /** Pantalla de bienvenida con el resumen de los módulos. */
    public void mostrarBienvenida() {
        consola.mostrar("");
        consola.mostrar("##############################################################");
        consola.mostrar("#        CARMOTORS - SISTEMA DE GESTIÓN DE TALLER (SGT)       #");
        consola.mostrar("##############################################################");
        int numero = 1;
        for (String modulo : MODULOS) {
            consola.mostrar("  " + numero + ") " + modulo);
            numero++;
        }
    }

    /** Ayuda cuando no se pudo conectar a MySQL. */
    public void mostrarAyudaConexion() {
        consola.mostrar("Verifique que MySQL esté activo, que existan las tablas (database/schema.sql)");
        consola.mostrar("y que URL, DB_USER y DB_PASSWORD de config/ConexionFactory sean correctos.");
    }

    public void mostrarDespedida() {
        consola.mostrar("Hasta pronto. Gracias por usar CarMotors SGT.");
    }
}
