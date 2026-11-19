/*
 * ============================================================
 * Archivo: ClienteVista.java
 * Propósito: menú y presentación del Módulo de Clientes: clientes,
 *            vehículos y perfil con historial.
 * Conceptos clave: MVC - Vista, implementa VistaMenu (interfaz, sesión 07),
 *            composición con VistaConsola, List.of + for-each.
 * Justificación: el texto del menú y el formato de los datos son
 *            responsabilidad de la vista; el controlador solo decide qué
 *            acción ejecutar. La vista recibe objetos del modelo ya calculados.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.vista;

import com.carmotors.modelo.Cliente;
import com.carmotors.modelo.OrdenServicio;
import com.carmotors.modelo.Vehiculo;

import java.util.List;
import java.util.Map;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public class ClienteVista implements VistaMenu {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    // Visto en clase: List.of / List.copyOf -> S19Demo03ReglasOrdenMain List.of y S20Demo02 Orden List.copyOf (Sesiones 19 y 20)
    private static final List<String> OPCIONES = List.of(
            "Registrar cliente",
            "Actualizar datos de contacto",
            "Listar clientes",
            "Eliminar cliente sin vehículos",
            "Registrar vehículo de un cliente",
            "Actualizar / transferir vehículo",
            "Eliminar vehículo sin historial",
            "Ver perfil e historial de un cliente");

    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final VistaConsola consola;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public ClienteVista(VistaConsola consola) {
        this.consola = consola;
    }

    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    @Override
    public String titulo() {
        return "Gestión de clientes";
    }

    // Visto en clase: Map / HashMap / LinkedHashMap / Map.Entry -> S10Demo01MapMain (Sesión 10)
    @Override
    public Map<String, String> opciones() {
        return VistaConsola.numerar(OPCIONES);
    }

    @Override
    public String textoSalida() {
        return "Volver al menú principal";
    }

    public void mostrarClientes(List<Cliente> clientes) {
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (clientes.isEmpty()) {
            consola.mostrar("No hay clientes registrados.");
            return;
        }
        consola.mostrar(Formato.ajustar("Documento", 13) + Formato.ajustar("Nombre", 22)
                + Formato.ajustar("Teléfono", 13) + "Email");
        // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
        for (Cliente c : clientes) {
            consola.mostrar(Formato.ajustar(c.getDocumento(), 13) + Formato.ajustar(c.getNombre(), 22)
                    + Formato.ajustar(c.getTelefono(), 13) + c.getEmail());
        }
    }

    public void mostrarVehiculos(List<Vehiculo> vehiculos) {
        if (vehiculos.isEmpty()) {
            consola.mostrar("  (Sin vehículos registrados)");
            return;
        }
        for (Vehiculo v : vehiculos) {
            consola.mostrar("  " + v.descripcion());
        }
    }

    /** Perfil completo: datos de contacto, vehículos y servicios realizados. */
    public void mostrarPerfil(Cliente cliente, List<Vehiculo> vehiculos, List<OrdenServicio> historial) {
        consola.mostrar("Cliente : " + cliente.getNombre() + " (doc. " + cliente.getDocumento() + ")");
        consola.mostrar("Contacto: " + cliente.getTelefono() + " | " + cliente.getEmail());
        consola.mostrar("Vehículos:");
        mostrarVehiculos(vehiculos);
        consola.mostrar("Historial de servicios (" + historial.size() + "):");
        if (historial.isEmpty()) {
            consola.mostrar("  (Sin servicios)");
        }
        for (OrdenServicio o : historial) {
            // Visto en clase: expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
            // Visto en clase: referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
            // Visto en clase: Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
            List<String> placa = vehiculos.stream()
                    .filter(v -> v.getId() == o.getVehiculoId())
                    .map(Vehiculo::getPlaca)
                    .toList();
            // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
            consola.mostrar("  #" + o.getId() + " " + Formato.fechaHora(o.getFechaRecepcion()) + " "
                    + (placa.isEmpty() ? "-" : placa.get(0)) + " "
                    + Formato.ajustar(o.getTipoMantenimiento().getDescripcion(), 11) + " "
                    + Formato.ajustar(o.getEstado().getDescripcion(), 11) + " " + o.getDescripcionProblema());
        }
    }

}
