
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
