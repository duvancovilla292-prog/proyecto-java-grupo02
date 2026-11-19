/*
 * ============================================================
 * Archivo: ProveedorController.java
 * Propósito: controlador del Módulo de Gestión de Proveedores.
 * Conceptos clave: MVC - Controlador, COMMAND, lambdas para las acciones
 *            de una sola línea.
 * Justificación: igual que los demás controladores: recibe la opción,
 *            delega en el servicio y entrega el resultado a la vista.
 *            Antes de pedir un id, placa, documento o código muestra la lista
 *            de opciones válidas con referencia(...) (heredado de
 *            ControladorBase), separada con título y línea, para que el
 *            usuario no tenga que saberla de memoria.
 *            Reutiliza InventarioVista para mostrar los repuestos.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.modelo.Proveedor;
import com.carmotors.servicio.ProveedorServicio;
import com.carmotors.vista.InventarioVista;
import com.carmotors.vista.ProveedorVista;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;

// Visto en clase: herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
// Visto en clase: PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public class ProveedorController extends ControladorBase {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final ProveedorVista vista;
    private final ProveedorServicio servicio;
    private final InventarioVista inventario;  // reutiliza la tabla de repuestos como referencia

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    /** Recibe su vista (como UsuarioController de S18Demo01) y crea su servicio; se instancia solo cuando el usuario entra al módulo. */
    public ProveedorController(VistaConsola consola, ProveedorVista vista) {
        super(consola, vista);
        this.vista = vista;
        this.servicio = new ProveedorServicio();
        this.inventario = new InventarioVista(consola);
        // Visto en clase: expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
        registrar("1", () -> registrarProveedor());
        registrar("2", () -> vista.mostrarProveedores(servicio.listarProveedores()));
        registrar("3", () -> actualizar());
        registrar("4", () -> eliminar());
        registrar("5", () -> registrarEnCatalogo());
        registrar("6", () -> quitarDelCatalogo());
        registrar("7", () -> verCatalogo());
        registrar("8", () -> compararPrecios());
        registrar("9", () -> evaluar());
        registrar("10", () -> verEvaluaciones());
        registrar("11", () -> corregirEvaluacion());
        registrar("12", () -> eliminarEvaluacion());
    }

    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    private void registrarProveedor() throws SQLException {
        String nombre = consola.leerTextoObligatorio("Nombre o razón social: ");
        String contacto = consola.leerTexto("Persona de contacto: ");
        String telefono = consola.leerTexto("Teléfono: ");
        String email = consola.leerTexto("Email: ");
        consola.mostrarExito("Proveedor registrado con id " + servicio.registrarProveedor(nombre, contacto, telefono, email) + ".");
    }

    private void actualizar() throws SQLException {
        referenciaProveedores();
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
        referenciaProveedores();
        int id = consola.leerEntero("Id del proveedor: ");
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (consola.confirmar("Se eliminará " + servicio.obtenerProveedor(id).getNombre()
                + " junto con su catálogo y evaluaciones.")) {
            servicio.eliminarProveedor(id);
            consola.mostrarExito("Proveedor eliminado.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void registrarEnCatalogo() throws SQLException {
        referenciaProveedores();
        int proveedor = consola.leerEntero("Id del proveedor: ");
        referenciaRepuestos();
        int repuesto = consola.leerEntero("Id del repuesto: ");
        int costo = consola.leerMonto("Costo ofrecido: ");
        boolean actualizado = servicio.registrarEnCatalogo(proveedor, repuesto, costo);
        // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
        consola.mostrarExito(actualizado ? "Costo actualizado en el catálogo." : "Producto agregado al catálogo.");
    }

    private void quitarDelCatalogo() throws SQLException {
        referenciaProveedores();
        int proveedor = consola.leerEntero("Id del proveedor: ");
        referencia("Catálogo del proveedor #" + proveedor,
                () -> vista.mostrarCatalogo("", servicio.catalogoDe(proveedor), true));
        servicio.quitarDelCatalogo(proveedor, consola.leerEntero("Id del repuesto: "));
        consola.mostrarExito("Producto retirado del catálogo.");
    }

    private void verCatalogo() throws SQLException {
        referenciaProveedores();
        int proveedor = consola.leerEntero("Id del proveedor: ");
        vista.mostrarCatalogo("Catálogo de " + servicio.obtenerProveedor(proveedor).getNombre() + ":",
                servicio.catalogoDe(proveedor), true);
    }

    private void compararPrecios() throws SQLException {
        referenciaRepuestos();
        int repuesto = consola.leerEntero("Id del repuesto: ");
        vista.mostrarCatalogo("Ofertas de la más barata a la más cara:", servicio.compararPrecios(repuesto), false);
    }

    private void evaluar() throws SQLException {
        referenciaProveedores();
        int proveedor = consola.leerEntero("Id del proveedor: ");
        int calidad = consola.leerEntero("Calidad de los productos (1-5): ");
        int puntualidad = consola.leerEntero("Puntualidad de entregas (1-5): ");
        String comentario = consola.leerTexto("Comentario: ");
        servicio.evaluar(proveedor, calidad, puntualidad, comentario);
        consola.mostrarExito("Evaluación registrada.");
    }

    private void corregirEvaluacion() throws SQLException {
        referenciaEvaluaciones();
        int id = consola.leerEntero("Id de la evaluación: ");
        int calidad = consola.leerEntero("Calidad (1-5): ");
        int puntualidad = consola.leerEntero("Puntualidad (1-5): ");
        servicio.corregirEvaluacion(id, calidad, puntualidad, consola.leerTexto("Comentario: "));
        consola.mostrarExito("Evaluación corregida.");
    }

    private void eliminarEvaluacion() throws SQLException {
        referenciaEvaluaciones();
        servicio.eliminarEvaluacion(consola.leerEntero("Id de la evaluación: "));
        consola.mostrarExito("Evaluación eliminada.");
    }

    private void verEvaluaciones() throws SQLException {
        referenciaProveedores();
        vista.mostrarEvaluaciones(servicio.evaluacionesDe(consola.leerEntero("Id del proveedor: ")));
    }

    // ------------------------------------------------------------ Referencias antes de pedir un dato

    private void referenciaProveedores() throws SQLException {
        referencia("Proveedores registrados", () -> vista.mostrarProveedores(servicio.listarProveedores()));
    }

    private void referenciaRepuestos() throws SQLException {
        referencia("Repuestos del inventario", () -> inventario.mostrarRepuestos(servicio.listarRepuestos()));
    }

    private void referenciaEvaluaciones() throws SQLException {
        referencia("Evaluaciones registradas", () -> vista.mostrarEvaluaciones(servicio.listarEvaluaciones()));
    }

    private String valorOActual(String nuevo, String actual) {
        // Visto en clase: String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
        return nuevo.isBlank() ? actual : nuevo;
    }
}
