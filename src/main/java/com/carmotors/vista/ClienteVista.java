/*
 * ============================================================
 * Archivo: ClienteVista.java
 * Propósito: menú y presentación del Módulo de Clientes: clientes,
 *            vehículos, perfil con historial y recordatorios.
 * Conceptos clave: MVC - Vista, implementa VistaMenu (interfaz, sesión 07),
 *            composición con VistaConsola, List.of + for-each,
 *            TEMPLATE METHOD (ReporteRecordatorios).
 * Justificación: el texto del menú y el formato de los datos son
 *            responsabilidad de la vista; el controlador solo decide qué
 *            acción ejecutar. La vista recibe objetos del modelo ya calculados.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
 *   - interfaces e implements -> S07Demo04 Notificable (Sesión 07)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - List.of / List.copyOf -> S19Demo03ReglasOrdenMain List.of y S20Demo02 Orden List.copyOf (Sesiones 19 y 20)
 *   - Map / HashMap / LinkedHashMap / Map.Entry -> S10Demo01MapMain (Sesión 10)
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 *   - referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
 *   - Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
 * ============================================================
 */
package com.carmotors.vista;

import com.carmotors.modelo.Cliente;
import com.carmotors.modelo.OrdenServicio;
import com.carmotors.modelo.RecordatorioMantenimiento;
import com.carmotors.modelo.Vehiculo;
import com.carmotors.vista.reporte.ReporteRecordatorios;

import java.util.List;
import java.util.Map;

public class ClienteVista implements VistaMenu {
    private static final List<String> OPCIONES = List.of(
            "Registrar cliente",
            "Actualizar datos de contacto",
            "Listar clientes",
            "Eliminar cliente sin vehículos",
            "Registrar vehículo de un cliente",
            "Actualizar / transferir vehículo",
            "Eliminar vehículo sin historial",
            "Ver perfil e historial de un cliente",
            "Recordatorios de mantenimiento preventivo");

    private final VistaConsola consola;

    public ClienteVista(VistaConsola consola) {
        this.consola = consola;
    }

    @Override
    public String titulo() {
        return "Gestión de clientes";
    }

    @Override
    public Map<String, String> opciones() {
        return VistaConsola.numerar(OPCIONES);
    }

    @Override
    public String textoSalida() {
        return "Volver al menú principal";
    }

    public void mostrarClientes(List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            consola.mostrar("No hay clientes registrados.");
            return;
        }
        consola.mostrar(Formato.ajustar("Documento", 13) + Formato.ajustar("Nombre", 22)
                + Formato.ajustar("Teléfono", 13) + "Email");
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
            List<String> placa = vehiculos.stream()
                    .filter(v -> v.getId() == o.getVehiculoId())
                    .map(Vehiculo::getPlaca)
                    .toList();
            consola.mostrar("  #" + o.getId() + " " + Formato.fechaHora(o.getFechaRecepcion()) + " "
                    + (placa.isEmpty() ? "-" : placa.get(0)) + " "
                    + Formato.ajustar(o.getTipoMantenimiento().getDescripcion(), 11) + " "
                    + Formato.ajustar(o.getEstado().getDescripcion(), 11) + " " + o.getDescripcionProblema());
        }
    }

    public void mostrarRecordatorios(List<RecordatorioMantenimiento> recordatorios, int meses) {
        consola.mostrarLineas(new ReporteRecordatorios(recordatorios, meses).generar());
    }
}
