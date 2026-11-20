/*
 * ============================================================
 * Archivo: ClienteController.java
 * Propósito: controlador del Módulo de Gestión de Clientes.
 * Conceptos clave: MVC - Controlador, COMMAND con lambdas, la vista se
 *            recibe por constructor.
 * Justificación: solo traduce la opción elegida en llamadas al servicio y
 *            entrega los resultados a la vista; no valida reglas ni consulta
 *            la BD. Lee los datos a través de la vista con etiquetas, igual
 *            que UsuarioController de S18Demo01.
 *            Antes de pedir un id, placa, documento o código muestra la lista
 *            de opciones válidas con referencia(...) (heredado de
 *            ControladorBase), separada con título y línea, para que el
 *            usuario no tenga que saberla de memoria.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.modelo.Cliente;
import com.carmotors.modelo.Vehiculo;
import com.carmotors.servicio.ClienteServicio;
import com.carmotors.vista.ClienteVista;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;

// Visto en clase: herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
// Visto en clase: PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public class ClienteController extends ControladorBase {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final ClienteVista vista;
    private final ClienteServicio servicio;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    /** Recibe su vista (como UsuarioController de S18Demo01) y crea su servicio; se instancia solo cuando el usuario entra al módulo. */
    public ClienteController(VistaConsola consola, ClienteVista vista) {
        super(consola, vista);
        this.vista = vista;
        this.servicio = new ClienteServicio();
        // Visto en clase: expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
        registrar("1", () -> registrarCliente());
        registrar("2", () -> actualizarCliente());
        registrar("3", () -> vista.mostrarClientes(servicio.listarClientes()));
        registrar("4", () -> eliminarCliente());
        registrar("5", () -> registrarVehiculo());
        registrar("6", () -> actualizarVehiculo());
        registrar("7", () -> eliminarVehiculo());
        registrar("8", () -> verPerfil());
    }

    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    private void registrarCliente() throws SQLException {
        String nombre = consola.leerTextoObligatorio("Nombre completo: ");
        String documento = consola.leerTextoObligatorio("Documento de identidad: ");
        String telefono = consola.leerTexto("Teléfono: ");
        String email = consola.leerTextoObligatorio("Email: ");
        int id = servicio.registrarCliente(nombre, documento, telefono, email);
        consola.mostrarExito("Cliente registrado con id " + id + ".");
    }

    private void actualizarCliente() throws SQLException {
        referenciaClientes();
        String documento = consola.leerTextoObligatorio("Documento del cliente: ");
        Cliente actual = servicio.obtenerPorDocumento(documento);
        consola.mostrar("Deje vacío para conservar el valor actual.");
        String nombre = valorOActual(consola.leerTexto("Nombre [" + actual.getNombre() + "]: "), actual.getNombre());
        String telefono = valorOActual(consola.leerTexto("Teléfono [" + actual.getTelefono() + "]: "), actual.getTelefono());
        String email = valorOActual(consola.leerTexto("Email [" + actual.getEmail() + "]: "), actual.getEmail());
        servicio.actualizarCliente(documento, nombre, telefono, email);
        consola.mostrarExito("Datos actualizados.");
    }

    private void eliminarCliente() throws SQLException {
        referenciaClientes();
        String documento = consola.leerTextoObligatorio("Documento del cliente: ");
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (consola.confirmar("Se eliminará el cliente " + servicio.obtenerPorDocumento(documento).getNombre() + ".")) {
            servicio.eliminarCliente(documento);
            consola.mostrarExito("Cliente eliminado.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void registrarVehiculo() throws SQLException {
        referenciaClientes();
        String documento = consola.leerTextoObligatorio("Documento del dueño: ");
        String placa = consola.leerTextoObligatorio("Placa: ");
        String marca = consola.leerTexto("Marca: ");
        String modelo = consola.leerTexto("Modelo: ");
        int id = servicio.registrarVehiculo(documento, placa, marca, modelo);
        consola.mostrarExito("Vehículo registrado con id " + id + ".");
    }

    private void actualizarVehiculo() throws SQLException {
        referenciaVehiculos();
        Vehiculo actual = servicio.obtenerVehiculo(consola.leerTextoObligatorio("Placa: "));
        consola.mostrar("Deje vacío para conservar el valor actual.");
        referencia("Clientes registrados (posibles nuevos dueños)", () -> vista.mostrarClientes(servicio.listarClientes()));
        String documento = consola.leerTexto("Documento del nuevo dueño (vacío = mismo dueño): ");
        String marca = valorOActual(consola.leerTexto("Marca [" + actual.getMarca() + "]: "), actual.getMarca());
        String modelo = valorOActual(consola.leerTexto("Modelo [" + actual.getModelo() + "]: "), actual.getModelo());
        servicio.actualizarVehiculo(actual.getPlaca(), documento, marca, modelo);
        consola.mostrarExito("Vehículo actualizado.");
    }

    private void eliminarVehiculo() throws SQLException {
        referenciaVehiculos();
        String placa = consola.leerTextoObligatorio("Placa: ");
        if (consola.confirmar("Se eliminará el vehículo " + Vehiculo.normalizarPlaca(placa) + ".")) {
            servicio.eliminarVehiculo(placa);
            consola.mostrarExito("Vehículo eliminado.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void verPerfil() throws SQLException {
        referenciaClientes();
        String documento = consola.leerTextoObligatorio("Documento del cliente: ");
        vista.mostrarPerfil(servicio.obtenerPorDocumento(documento), servicio.vehiculosDe(documento),
                servicio.historialServicios(documento));
    }

    // ------------------------------------------------------------ Referencias antes de pedir un dato

    private void referenciaClientes() throws SQLException {
        referencia("Clientes registrados", () -> vista.mostrarClientes(servicio.listarClientes()));
    }

    private void referenciaVehiculos() throws SQLException {
        referencia("Vehículos registrados", () -> vista.mostrarVehiculos(servicio.listarVehiculos()));
    }

    private String valorOActual(String nuevo, String actual) {
        // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
        // Visto en clase: String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
        return nuevo.isBlank() ? actual : nuevo;
    }
}
