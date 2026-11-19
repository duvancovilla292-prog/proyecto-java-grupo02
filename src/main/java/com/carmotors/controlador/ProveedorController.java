/*
 * ============================================================
 * Archivo: ProveedorController.java
 * Propósito: controlador del Módulo de Gestión de Proveedores.
 * Conceptos clave: MVC - Controlador, COMMAND, lambdas para las acciones
 *            de una sola línea.
 * Justificación: igual que los demás controladores: recibe la opción,
 *            delega en el servicio y entrega el resultado a la vista.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.modelo.Proveedor;
import com.carmotors.servicio.ProveedorServicio;
import com.carmotors.vista.ProveedorVista;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;

public class ProveedorController extends ControladorBase {
    private final ProveedorVista vista;
    private final ProveedorServicio servicio;

    /** Recibe su vista (como UsuarioController de S18Demo01) y crea su servicio; se instancia solo cuando el usuario entra al módulo. */
    public ProveedorController(VistaConsola consola, ProveedorVista vista) {
        super(consola, vista);
        this.vista = vista;
        this.servicio = new ProveedorServicio();
        registrar("1", () -> registrarProveedor());
        registrar("2", () -> vista.mostrarProveedores(servicio.listarProveedores()));
        registrar("3", () -> actualizar());
        registrar("4", () -> eliminar());
        registrar("5", () -> registrarEnCatalogo());
        registrar("6", () -> quitarDelCatalogo());
        registrar("7", () -> verCatalogo());
        registrar("8", () -> compararPrecios());
        registrar("9", () -> evaluar());
        registrar("10", () -> vista.mostrarEvaluaciones(servicio.evaluacionesDe(consola.leerEntero("Id del proveedor: "))));
        registrar("11", () -> corregirEvaluacion());
        registrar("12", () -> eliminarEvaluacion());
    }

    private void registrarProveedor() throws SQLException {
        String nombre = consola.leerTextoObligatorio("Nombre o razón social: ");
        String contacto = consola.leerTexto("Persona de contacto: ");
        String telefono = consola.leerTexto("Teléfono: ");
        String email = consola.leerTexto("Email: ");
        consola.mostrarExito("Proveedor registrado con id " + servicio.registrarProveedor(nombre, contacto, telefono, email) + ".");
    }

    private void actualizar() throws SQLException {
        int id = consola.leerEntero("Id del proveedor: ");
        Proveedor actual = servicio.obtenerProveedor(id);
        consola.mostrar("Deje vacío para conservar el valor actual.");
        String nombre = valorOActual(consola.leerTexto("Nombre [" + actual.getNombre() + "]: "), actual.getNombre());
        String contacto = valorOActual(consola.leerTexto("Contacto [" + actual.getContacto() + "]: "), actual.getContacto());
        String telefono = valorOActual(consola.leerTexto("Teléfono [" + actual.getTelefono() + "]: "), actual.getTelefono());
        String email = valorOActual(consola.leerTexto("Email [" + actual.getEmail() + "]: "), actual.getEmail());
        servicio.actualizarProveedor(id, nombre, contacto, telefono, email);
        consola.mostrarExito("Proveedor actualizado.");
    }

    private void eliminar() throws SQLException {
        int id = consola.leerEntero("Id del proveedor: ");
        if (consola.confirmar("Se eliminará " + servicio.obtenerProveedor(id).getNombre()
                + " junto con su catálogo y evaluaciones.")) {
            servicio.eliminarProveedor(id);
            consola.mostrarExito("Proveedor eliminado.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void registrarEnCatalogo() throws SQLException {
        int proveedor = consola.leerEntero("Id del proveedor: ");
        int repuesto = consola.leerEntero("Id del repuesto: ");
        int costo = consola.leerMonto("Costo ofrecido: ");
        boolean actualizado = servicio.registrarEnCatalogo(proveedor, repuesto, costo);
        consola.mostrarExito(actualizado ? "Costo actualizado en el catálogo." : "Producto agregado al catálogo.");
    }

    private void quitarDelCatalogo() throws SQLException {
        int proveedor = consola.leerEntero("Id del proveedor: ");
        servicio.quitarDelCatalogo(proveedor, consola.leerEntero("Id del repuesto: "));
        consola.mostrarExito("Producto retirado del catálogo.");
    }

    private void verCatalogo() throws SQLException {
        int proveedor = consola.leerEntero("Id del proveedor: ");
        vista.mostrarCatalogo("Catálogo de " + servicio.obtenerProveedor(proveedor).getNombre() + ":",
                servicio.catalogoDe(proveedor), true);
    }

    private void compararPrecios() throws SQLException {
        int repuesto = consola.leerEntero("Id del repuesto: ");
        vista.mostrarCatalogo("Ofertas de la más barata a la más cara:", servicio.compararPrecios(repuesto), false);
    }

    private void evaluar() throws SQLException {
        int proveedor = consola.leerEntero("Id del proveedor: ");
        int calidad = consola.leerEntero("Calidad de los productos (1-5): ");
        int puntualidad = consola.leerEntero("Puntualidad de entregas (1-5): ");
        String comentario = consola.leerTexto("Comentario: ");
        servicio.evaluar(proveedor, calidad, puntualidad, comentario);
        consola.mostrarExito("Evaluación registrada.");
    }

    private void corregirEvaluacion() throws SQLException {
        int id = consola.leerEntero("Id de la evaluación: ");
        int calidad = consola.leerEntero("Calidad (1-5): ");
        int puntualidad = consola.leerEntero("Puntualidad (1-5): ");
        servicio.corregirEvaluacion(id, calidad, puntualidad, consola.leerTexto("Comentario: "));
        consola.mostrarExito("Evaluación corregida.");
    }

    private void eliminarEvaluacion() throws SQLException {
        servicio.eliminarEvaluacion(consola.leerEntero("Id de la evaluación: "));
        consola.mostrarExito("Evaluación eliminada.");
    }

    private String valorOActual(String nuevo, String actual) {
        return nuevo.isBlank() ? actual : nuevo;
    }
}
