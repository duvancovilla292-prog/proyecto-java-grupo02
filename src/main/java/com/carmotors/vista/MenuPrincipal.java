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
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.vista;

import java.util.List;
import java.util.Map;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public class MenuPrincipal implements VistaMenu {
    // Descripción breve de cada módulo, en el mismo orden del menú.
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    // Visto en clase: List.of / List.copyOf -> S19Demo03ReglasOrdenMain List.of y S20Demo02 Orden List.copyOf (Sesiones 19 y 20)
    private static final List<String> MODULOS = List.of(
            "Inventario: repuestos, lotes y alertas de stock en tiempo real",
            "Servicios: órdenes, técnicos, repuestos reservados y ciclo de vida",
            "Clientes: perfiles, vehículos e historial de servicios",
            "Facturación: factura en texto plano",
            "Proveedores: catálogo de precios y evaluación de desempeño",
            "Reportes: servicios, técnicos, repuestos, alertas, recordatorios y proveedores");

    // Texto de las opciones del menú raíz (mismo orden que MODULOS).
    private static final List<String> OPCIONES = List.of(
            "Inventario de repuestos",
            "Órdenes de servicio y técnicos",
            "Clientes y vehículos",
            "Facturación",
            "Proveedores",
            "Reportes gerenciales");

    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final VistaConsola consola;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public MenuPrincipal(VistaConsola consola) {
        this.consola = consola;
    }

    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    @Override
    public String titulo() {
        return "CarMotors - Sistema de Gestión de Taller";
    }

    // Visto en clase: Map / HashMap / LinkedHashMap / Map.Entry -> S10Demo01MapMain (Sesión 10)
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
        // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
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
