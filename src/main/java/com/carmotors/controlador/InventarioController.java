/*
 * ============================================================
 * Archivo: InventarioController.java
 * Propósito: controlador del Módulo de Gestión de Inventario.
 * Conceptos clave: MVC - Controlador, COMMAND, inyección de dependencias.
 * Justificación: pide datos a la vista, delega en InventarioServicio y
 *            entrega resultados a la vista; las alertas automáticas de stock
 *            bajo llegan solas por el patrón Observer configurado al arrancar.
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.modelo.Repuesto;
import com.carmotors.servicio.InventarioServicio;
import com.carmotors.vista.InventarioVista;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;

public class InventarioController extends ControladorBase {
    private final InventarioVista vista;
    private final InventarioServicio servicio;

    public InventarioController(VistaConsola consola, InventarioVista vista, InventarioServicio servicio) {
        super(consola, vista);
        this.vista = vista;
        this.servicio = servicio;
        registrar("1", () -> registrarRepuesto());
        registrar("2", () -> registrarLote());
        registrar("3", () -> vista.mostrarRepuestos(servicio.listarRepuestos()));
        registrar("4", () -> vista.mostrarRepuestos(servicio.buscar(consola.leerTexto("Texto a buscar: "))));
        registrar("5", () -> verLotes());
        registrar("6", () -> actualizarPrecios());
        registrar("7", () -> alertas());
        registrar("8", () -> corregirCaducidad());
        registrar("9", () -> anularLote());
        registrar("10", () -> eliminarRepuesto());
        registrar("11", () -> vista.mostrarTipos(servicio.listarTipos()));
        registrar("12", () -> registrarTipo());
        registrar("13", () -> renombrarTipo());
        registrar("14", () -> eliminarTipo());
    }

    private void registrarRepuesto() throws SQLException {
        String nombre = consola.leerTextoObligatorio("Nombre: ");
        vista.mostrarTipos(servicio.listarTipos());
        int tipo = consola.leerEntero("Id del tipo: ");
        String marca = consola.leerTexto("Marca: ");
        int proveedor = consola.leerEntero("Id del proveedor asociado: ");
        int compra = consola.leerMonto("Precio de compra: ");
        int venta = consola.leerMonto("Precio de venta: ");
        int minimo = consola.leerEntero("Stock mínimo: ");
        int id = servicio.registrarRepuesto(nombre, tipo, marca, proveedor, compra, venta, minimo);
        consola.mostrarExito("Repuesto #" + id + " registrado con stock 0. Ingrese un lote para sumar existencias.");
    }

    private void registrarLote() throws SQLException {
        String codigo = consola.leerTextoObligatorio("Código único del lote (ej. LOT-2026-100): ");
        int repuesto = consola.leerEntero("Id del repuesto: ");
        int proveedor = consola.leerEntero("Id del proveedor que entrega: ");
        int cantidad = consola.leerEntero("Cantidad recibida: ");
        String caducidad = consola.leerFechaOpcional("Fecha de caducidad AAAA-MM-DD (Enter si no aplica): ");
        servicio.registrarLote(codigo, repuesto, proveedor, cantidad, caducidad);
        Repuesto actualizado = servicio.obtenerRepuesto(repuesto);
        consola.mostrarExito("Lote registrado. Stock actual de " + actualizado.getNombre() + ": "
                + actualizado.getCantidadStock() + ".");
    }

    private void verLotes() throws SQLException {
        int id = consola.leerEntero("Id del repuesto: ");
        vista.mostrarLotes(servicio.obtenerRepuesto(id), servicio.listarLotes(id));
    }

    private void actualizarPrecios() throws SQLException {
        int id = consola.leerEntero("Id del repuesto: ");
        vista.mostrarFicha(servicio.obtenerRepuesto(id));
        int compra = consola.leerMonto("Nuevo precio de compra: ");
        int venta = consola.leerMonto("Nuevo precio de venta: ");
        int minimo = consola.leerEntero("Nuevo stock mínimo: ");
        servicio.actualizarPrecios(id, compra, venta, minimo);
        consola.mostrarExito("Ficha actualizada.");
    }

    private void alertas() throws SQLException {
        int dias = consola.leerEnteroConDefecto("Días de anticipación para caducidad", 30);
        vista.mostrarAlertas(servicio.repuestosBajoMinimo(), servicio.lotesPorVencer(dias), servicio.listarRepuestos(), dias);
    }

    private void corregirCaducidad() throws SQLException {
        String codigo = consola.leerTextoObligatorio("Código del lote: ");
        String caducidad = consola.leerFechaOpcional("Nueva caducidad AAAA-MM-DD (Enter = no caduca): ");
        servicio.actualizarCaducidadLote(codigo, caducidad);
        consola.mostrarExito("Caducidad actualizada.");
    }

    private void anularLote() throws SQLException {
        String codigo = consola.leerTextoObligatorio("Código del lote: ");
        if (consola.confirmar("Se restará la cantidad del lote al stock.")) {
            servicio.eliminarLote(codigo);
            consola.mostrarExito("Lote anulado y stock corregido.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void eliminarRepuesto() throws SQLException {
        int id = consola.leerEntero("Id del repuesto: ");
        if (consola.confirmar("Se eliminará la ficha de " + servicio.obtenerRepuesto(id).getNombre() + ".")) {
            servicio.eliminarRepuesto(id);
            consola.mostrarExito("Repuesto eliminado.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void registrarTipo() throws SQLException {
        int id = servicio.registrarTipo(consola.leerTextoObligatorio("Nombre del tipo: "));
        consola.mostrarExito("Tipo registrado con id " + id + ".");
    }

    private void renombrarTipo() throws SQLException {
        vista.mostrarTipos(servicio.listarTipos());
        int id = consola.leerEntero("Id del tipo: ");
        servicio.renombrarTipo(id, consola.leerTextoObligatorio("Nuevo nombre: "));
        consola.mostrarExito("Tipo actualizado.");
    }

    private void eliminarTipo() throws SQLException {
        vista.mostrarTipos(servicio.listarTipos());
        servicio.eliminarTipo(consola.leerEntero("Id del tipo: "));
        consola.mostrarExito("Tipo eliminado.");
    }
}
