/*
 * ============================================================
 * Archivo: OrdenServicioController.java
 * Propósito: controlador del Módulo de Gestión de Servicios (órdenes,
 *            técnicos, repuestos y ciclo de vida).
 * Conceptos clave: MVC - Controlador, COMMAND, conversión de id a enum
 *            (TipoMantenimiento.desdeId / EstadoServicio.desdeId).
 * Justificación: cada opción es una acción pequeña que delega en
 *            OrdenServicioServicio; las reglas (State, reserva, deducción,
 *            liberación de reservas) no aparecen aquí.
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.modelo.EstadoServicio;
import com.carmotors.modelo.OrdenServicio;
import com.carmotors.modelo.Tecnico;
import com.carmotors.modelo.TipoMantenimiento;
import com.carmotors.servicio.OrdenServicioServicio;
import com.carmotors.vista.OrdenServicioVista;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;
import java.util.List;

public class OrdenServicioController extends ControladorBase {
    private final OrdenServicioVista vista;
    private final OrdenServicioServicio servicio;

    public OrdenServicioController(VistaConsola consola, OrdenServicioVista vista, OrdenServicioServicio servicio) {
        super(consola, vista);
        this.vista = vista;
        this.servicio = servicio;
        registrar("1", () -> crearOrden());
        registrar("2", () -> vista.mostrarOrdenes(servicio.listarOrdenes()));
        registrar("3", () -> listarPorEstado());
        registrar("4", () -> verDetalle());
        registrar("5", () -> actualizarOrden());
        registrar("6", () -> asignarTecnico());
        registrar("7", () -> reemplazarTecnico());
        registrar("8", () -> quitarTecnico());
        registrar("9", () -> asignarRepuesto());
        registrar("10", () -> quitarRepuesto());
        registrar("11", () -> iniciarOrden());
        registrar("12", () -> completar());
        registrar("13", () -> entregar());
        registrar("14", () -> cancelarOrden());
        registrar("15", () -> registrarTecnico());
        registrar("16", () -> vista.mostrarTecnicos(servicio.listarTecnicos()));
        registrar("17", () -> actualizarTecnico());
        registrar("18", () -> cambiarActividad());
        registrar("19", () -> eliminarTecnico());
    }

    private void crearOrden() throws SQLException {
        String placa = consola.leerTextoObligatorio("Placa del vehículo: ");
        vista.mostrarTiposMantenimiento();
        TipoMantenimiento tipo = TipoMantenimiento.desdeId(consola.leerEntero("Tipo de mantenimiento: "));
        String descripcion = consola.leerTextoObligatorio("Descripción del problema: ");
        int manoObra = consola.leerMonto("Costo estimado de mano de obra: ");
        int id = servicio.crearOrden(placa, tipo, descripcion, manoObra);
        consola.mostrarExito("Orden #" + id + " creada en estado Pendiente.");
    }

    private void listarPorEstado() throws SQLException {
        vista.mostrarEstados();
        vista.mostrarOrdenes(servicio.listarPorEstado(EstadoServicio.desdeId(consola.leerEntero("Estado: "))));
    }

    private void verDetalle() throws SQLException {
        OrdenServicio orden = servicio.obtenerOrden(consola.leerEntero("Número de orden: "));
        vista.mostrarDetalle(orden, servicio.vehiculoDe(orden), servicio.tecnicosDe(orden.getId()),
                servicio.repuestosDe(orden.getId()));
    }

    private void actualizarOrden() throws SQLException {
        OrdenServicio actual = servicio.obtenerOrden(consola.leerEntero("Número de orden: "));
        consola.mostrar("Deje vacío para conservar el valor actual.");
        vista.mostrarTiposMantenimiento();
        String tipoTexto = consola.leerTexto("Tipo [" + actual.getTipoMantenimiento().getId() + "]: ");
        TipoMantenimiento tipo = tipoTexto.isBlank() ? actual.getTipoMantenimiento()
                : TipoMantenimiento.desdeId(Integer.parseInt(tipoTexto));
        String descripcion = consola.leerTexto("Descripción [" + actual.getDescripcionProblema() + "]: ");
        String manoObra = consola.leerTexto("Mano de obra [" + actual.getCostoManoObra() + "]: ");
        servicio.actualizarOrden(actual.getId(), tipo,
                descripcion.isBlank() ? actual.getDescripcionProblema() : descripcion,
                manoObra.isBlank() ? actual.getCostoManoObra() : Integer.parseInt(manoObra));
        consola.mostrarExito("Orden actualizada.");
    }

    private void asignarTecnico() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        vista.mostrarTecnicos(servicio.listarTecnicos());
        servicio.asignarTecnico(orden, consola.leerEntero("Id del técnico: "));
        consola.mostrarExito("Técnico asignado.");
    }

    private void reemplazarTecnico() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        vista.mostrarTecnicos(servicio.tecnicosDe(orden));
        int anterior = consola.leerEntero("Id del técnico a reemplazar: ");
        int nuevo = consola.leerEntero("Id del técnico nuevo: ");
        servicio.reemplazarTecnico(orden, anterior, nuevo);
        consola.mostrarExito("Técnico reemplazado.");
    }

    private void quitarTecnico() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        List<Tecnico> asignados = servicio.tecnicosDe(orden);
        vista.mostrarTecnicos(asignados);
        servicio.quitarTecnico(orden, consola.leerEntero("Id del técnico a quitar: "));
        consola.mostrarExito("Técnico retirado de la orden.");
    }

    private void asignarRepuesto() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        int repuesto = consola.leerEntero("Id del repuesto: ");
        int cantidad = consola.leerEntero("Cantidad: ");
        servicio.asignarRepuesto(orden, repuesto, cantidad);
        consola.mostrarExito("Repuesto asignado y RESERVADO en inventario.");
    }

    private void quitarRepuesto() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        servicio.quitarRepuesto(orden, consola.leerEntero("Id del repuesto a quitar: "));
        consola.mostrarExito("Repuesto retirado; su reserva volvió al disponible.");
    }

    private void iniciarOrden() throws SQLException {
        servicio.iniciar(consola.leerEntero("Número de orden: "));
        consola.mostrarExito("Orden en proceso.");
    }

    private void completar() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        if (consola.confirmar("Se descontarán del inventario los repuestos reservados.")) {
            servicio.completar(orden);
            consola.mostrarExito("Orden completada; stock deducido. Ya puede facturarse.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void entregar() throws SQLException {
        servicio.entregar(consola.leerEntero("Número de orden: "));
        consola.mostrarExito("Vehículo entregado al cliente.");
    }

    private void cancelarOrden() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        if (consola.confirmar("Se eliminará la orden y se liberarán sus repuestos reservados.")) {
            servicio.cancelarOrden(orden);
            consola.mostrarExito("Orden cancelada.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void registrarTecnico() throws SQLException {
        String nombre = consola.leerTextoObligatorio("Nombre: ");
        String especialidad = consola.leerTexto("Especialidad: ");
        consola.mostrarExito("Técnico registrado con id " + servicio.registrarTecnico(nombre, especialidad) + ".");
    }

    private void actualizarTecnico() throws SQLException {
        int id = consola.leerEntero("Id del técnico: ");
        String nombre = consola.leerTextoObligatorio("Nombre: ");
        String especialidad = consola.leerTexto("Especialidad: ");
        servicio.actualizarTecnico(id, nombre, especialidad);
        consola.mostrarExito("Técnico actualizado.");
    }

    private void cambiarActividad() throws SQLException {
        int id = consola.leerEntero("Id del técnico: ");
        boolean activo = consola.leerEntero("1 = activar, 2 = desactivar: ") == 1;
        servicio.cambiarActividadTecnico(id, activo);
        consola.mostrarExito("Técnico " + (activo ? "activado." : "desactivado."));
    }

    private void eliminarTecnico() throws SQLException {
        servicio.eliminarTecnico(consola.leerEntero("Id del técnico: "));
        consola.mostrarExito("Técnico eliminado.");
    }
}
