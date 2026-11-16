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
        registrar("9", () -> recordatorios());
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

    private void recordatorios() throws SQLException {
        int meses = consola.leerEnteroConDefecto("Frecuencia del preventivo en meses", 6);
        int dias = consola.leerEnteroConDefecto("Incluir los que vencen en los próximos días", 30);
        vista.mostrarRecordatorios(servicio.recordatoriosMantenimiento(meses, dias), meses);
    }

    private String valorOActual(String nuevo, String actual) {
        return nuevo.isBlank() ? actual : nuevo;
    }
}
