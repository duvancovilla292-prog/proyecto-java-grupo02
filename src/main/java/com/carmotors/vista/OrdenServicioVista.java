/*
 * ============================================================
 * Archivo: OrdenServicioVista.java
 * Propósito: menú y presentación del Módulo de Servicios: órdenes, su
 *            detalle, estados y técnicos.
 * Conceptos clave: MVC - Vista, implementa VistaMenu, composición con
 *            VistaConsola, enum.values() para listar estados.
 * Justificación: el detalle muestra en un solo lugar vehículo, estado,
 *            técnicos y repuestos (RESERVADO/DEDUCIDO), lo que resuelve la
 *            "planificación ineficiente": se ve qué está en espera, en
 *            proceso o listo para entregar.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
 *   - interfaces e implements -> S07Demo04 Notificable (Sesión 07)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - List.of / List.copyOf -> S19Demo03ReglasOrdenMain List.of y S20Demo02 Orden List.copyOf (Sesiones 19 y 20)
 *   - Map / HashMap / LinkedHashMap / Map.Entry -> S10Demo01MapMain (Sesión 10)
 *   - enum con atributos, values() y == -> S10Demo02 DiaSemana y S10Demo02EnumMain (Sesión 10)
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 * ============================================================
 */
package com.carmotors.vista;

import com.carmotors.modelo.DetalleRepuesto;
import com.carmotors.modelo.EstadoServicio;
import com.carmotors.modelo.OrdenServicio;
import com.carmotors.modelo.Tecnico;
import com.carmotors.modelo.TipoMantenimiento;
import com.carmotors.modelo.Vehiculo;

import java.util.List;
import java.util.Map;

public class OrdenServicioVista implements VistaMenu {
    private static final List<String> OPCIONES = List.of(
            "Crear orden de servicio",
            "Listar todas las órdenes",
            "Seguimiento: órdenes por estado",
            "Ver detalle de una orden",
            "Actualizar datos de una orden",
            "Asignar técnico a una orden",
            "Reemplazar técnico de una orden",
            "Quitar técnico de una orden",
            "Asignar repuesto a una orden (queda Reservado)",
            "Quitar repuesto de una orden (libera la reserva)",
            "Iniciar orden (Pendiente -> En Proceso)",
            "Completar orden (deduce stock)",
            "Entregar vehículo (Facturado -> Entregado)",
            "Cancelar orden pendiente",
            "Registrar técnico",
            "Listar técnicos",
            "Actualizar datos de un técnico",
            "Activar / desactivar técnico",
            "Eliminar técnico sin historial",
            "Recepción rápida: cliente + vehículo + orden");

    private final VistaConsola consola;

    public OrdenServicioVista(VistaConsola consola) {
        this.consola = consola;
    }

    @Override
    public String titulo() {
        return "Gestión de servicios";
    }

    @Override
    public Map<String, String> opciones() {
        return VistaConsola.numerar(OPCIONES);
    }

    @Override
    public String textoSalida() {
        return "Volver al menú principal";
    }

    public void mostrarTiposMantenimiento() {
        for (TipoMantenimiento tipo : TipoMantenimiento.values()) {
            consola.mostrar("  " + tipo.getId() + ". " + tipo.getDescripcion());
        }
    }

    public void mostrarEstados() {
        for (EstadoServicio estado : EstadoServicio.values()) {
            consola.mostrar("  " + estado.getId() + ". " + estado.getDescripcion());
        }
    }

    public void mostrarOrdenes(List<OrdenServicio> ordenes) {
        if (ordenes.isEmpty()) {
            consola.mostrar("No hay órdenes para mostrar.");
            return;
        }
        consola.mostrar(Formato.ajustar("#", 5) + Formato.ajustar("Recepción", 18) + Formato.ajustar("Vehíc.", 7)
                + Formato.ajustar("Tipo", 12) + Formato.ajustar("Estado", 12) + "Descripción");
        for (OrdenServicio o : ordenes) {
            consola.mostrar(Formato.ajustar(String.valueOf(o.getId()), 5)
                    + Formato.ajustar(Formato.fechaHora(o.getFechaRecepcion()), 18)
                    + Formato.ajustar("#" + o.getVehiculoId(), 7)
                    + Formato.ajustar(o.getTipoMantenimiento().getDescripcion(), 12)
                    + Formato.ajustar(o.getEstado().getDescripcion(), 12) + o.getDescripcionProblema());
        }
    }

    public void mostrarDetalle(OrdenServicio orden, Vehiculo vehiculo, List<Tecnico> tecnicos,
                               List<DetalleRepuesto> repuestos) {
        consola.mostrar("Orden #" + orden.getId() + " | Estado: " + orden.getEstado().getDescripcion());
        consola.mostrar("Vehículo : " + (vehiculo == null ? "-" : vehiculo.descripcion()));
        consola.mostrar("Tipo     : " + orden.getTipoMantenimiento().getDescripcion());
        consola.mostrar("Recepción: " + Formato.fechaHora(orden.getFechaRecepcion())
                + " | Entrega: " + Formato.fechaHora(orden.getFechaEntrega()));
        consola.mostrar("Problema : " + orden.getDescripcionProblema());
        consola.mostrar("Mano obra: " + Formato.pesos(orden.getCostoManoObra()));
        consola.mostrar("Técnicos :");
        if (tecnicos.isEmpty()) {
            consola.mostrar("  (Sin técnicos asignados)");
        }
        for (Tecnico t : tecnicos) {
            consola.mostrar("  #" + t.getId() + " " + t.getNombre() + " - " + t.getEspecialidad());
        }
        consola.mostrar("Repuestos:");
        if (repuestos.isEmpty()) {
            consola.mostrar("  (Sin repuestos asignados)");
        }
        for (DetalleRepuesto d : repuestos) {
            consola.mostrar("  #" + d.getRepuestoId() + " " + Formato.ajustar(d.getNombreRepuesto(), 30)
                    + " x" + d.getCantidad() + " " + Formato.derecha(Formato.pesos(d.subtotal()), 11)
                    + "  [" + d.getEstado() + "]");
        }
    }

    public void mostrarTecnicos(List<Tecnico> tecnicos) {
        if (tecnicos.isEmpty()) {
            consola.mostrar("No hay técnicos registrados.");
            return;
        }
        for (Tecnico t : tecnicos) {
            consola.mostrar("  #" + Formato.ajustar(String.valueOf(t.getId()), 4) + Formato.ajustar(t.getNombre(), 20)
                    + Formato.ajustar(t.getEspecialidad(), 26) + (t.isActivo() ? "Activo" : "Inactivo"));
        }
    }
}
