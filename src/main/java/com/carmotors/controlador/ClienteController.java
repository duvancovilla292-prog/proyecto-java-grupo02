/*
 * ============================================================
 * Archivo: ClienteController.java
 * Propósito: controlador del Módulo de Gestión de Clientes.
 * Conceptos clave: MVC - Controlador, COMMAND con referencias a método,
 *            inyección de dependencias por constructor.
 * Justificación: solo traduce la opción elegida en llamadas al servicio y
 *            entrega los resultados a la vista; no valida reglas ni consulta
 *            la BD. Lee los datos a través de la vista con etiquetas, igual
 *            que UsuarioController de S18Demo01.
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

import com.carmotors.modelo.Cliente;
import com.carmotors.modelo.Vehiculo;
import com.carmotors.servicio.ClienteServicio;
import com.carmotors.vista.ClienteVista;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;

public class ClienteController extends ControladorBase {
    private final ClienteVista vista;
    private final ClienteServicio servicio;

    /** Crea su vista y su servicio: el módulo se instancia solo cuando el usuario entra a él. */
    public ClienteController(VistaConsola consola) {
        this(consola, new ClienteVista(consola), new ClienteServicio());
    }

    public ClienteController(VistaConsola consola, ClienteVista vista, ClienteServicio servicio) {
        super(consola, vista);
        this.vista = vista;
        this.servicio = servicio;
        registrar("1", () -> registrarCliente());
        registrar("2", () -> actualizarCliente());
        registrar("3", () -> vista.mostrarClientes(servicio.listarClientes()));
        registrar("4", () -> eliminarCliente());
        registrar("5", () -> registrarVehiculo());
        registrar("6", () -> actualizarVehiculo());
        registrar("7", () -> eliminarVehiculo());
        registrar("8", () -> verPerfil());
    }

    private void registrarCliente() throws SQLException {
        String nombre = consola.leerTextoObligatorio("Nombre completo: ");
        String documento = consola.leerTextoObligatorio("Documento de identidad: ");
        String telefono = consola.leerTexto("Teléfono: ");
        String email = consola.leerTextoObligatorio("Email: ");
        int id = servicio.registrarCliente(nombre, documento, telefono, email);
        consola.mostrarExito("Cliente registrado con id " + id + ".");
    }

    private void actualizarCliente() throws SQLException {
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
        String documento = consola.leerTextoObligatorio("Documento del cliente: ");
        if (consola.confirmar("Se eliminará el cliente " + servicio.obtenerPorDocumento(documento).getNombre() + ".")) {
            servicio.eliminarCliente(documento);
            consola.mostrarExito("Cliente eliminado.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void registrarVehiculo() throws SQLException {
        String documento = consola.leerTextoObligatorio("Documento del dueño: ");
        String placa = consola.leerTextoObligatorio("Placa: ");
        String marca = consola.leerTexto("Marca: ");
        String modelo = consola.leerTexto("Modelo: ");
        int id = servicio.registrarVehiculo(documento, placa, marca, modelo);
        consola.mostrarExito("Vehículo registrado con id " + id + ".");
    }

    private void actualizarVehiculo() throws SQLException {
        Vehiculo actual = servicio.obtenerVehiculo(consola.leerTextoObligatorio("Placa: "));
        consola.mostrar("Deje vacío para conservar el valor actual.");
        String documento = consola.leerTexto("Documento del nuevo dueño (vacío = mismo dueño): ");
        String marca = valorOActual(consola.leerTexto("Marca [" + actual.getMarca() + "]: "), actual.getMarca());
        String modelo = valorOActual(consola.leerTexto("Modelo [" + actual.getModelo() + "]: "), actual.getModelo());
        servicio.actualizarVehiculo(actual.getPlaca(), documento, marca, modelo);
        consola.mostrarExito("Vehículo actualizado.");
    }

    private void eliminarVehiculo() throws SQLException {
        String placa = consola.leerTextoObligatorio("Placa: ");
        if (consola.confirmar("Se eliminará el vehículo " + Vehiculo.normalizarPlaca(placa) + ".")) {
            servicio.eliminarVehiculo(placa);
            consola.mostrarExito("Vehículo eliminado.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void verPerfil() throws SQLException {
        String documento = consola.leerTextoObligatorio("Documento del cliente: ");
        vista.mostrarPerfil(servicio.obtenerPorDocumento(documento), servicio.vehiculosDe(documento),
                servicio.historialServicios(documento));
    }

    private String valorOActual(String nuevo, String actual) {
        return nuevo.isBlank() ? actual : nuevo;
    }
}
