/*
 * ============================================================
 * Archivo: InventarioController.java
 * Propósito: controlador del Módulo de Gestión de Inventario.
 * Conceptos clave: MVC - Controlador, COMMAND, lambdas para registrar
 *            cada opción.
 * Justificación: recibe su vista por constructor y crea su servicio; pide
 *            datos a la vista, delega en InventarioServicio y entrega
 *            resultados a la vista; las alertas automáticas de stock bajo
 *            llegan solas por el patrón Observer configurado al arrancar.
 *            Antes de pedir un id, placa, documento o código muestra la lista
 *            de opciones válidas con referencia(...) (heredado de
 *            ControladorBase), separada con título y línea, para que el
 *            usuario no tenga que saberla de memoria.
 *            Reutiliza ProveedorVista para mostrar los proveedores.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.modelo.Repuesto;
import com.carmotors.servicio.InventarioServicio;
import com.carmotors.vista.InventarioVista;
import com.carmotors.vista.ProveedorVista;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;

// Visto en clase: herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
// Visto en clase: PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public class InventarioController extends ControladorBase {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final InventarioVista vista;
    private final InventarioServicio servicio;
    private final ProveedorVista proveedores;  // reutiliza la tabla de proveedores como referencia

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    /** Recibe su vista (como UsuarioController de S18Demo01) y crea su servicio; se instancia solo cuando el usuario entra al módulo. */
    public InventarioController(VistaConsola consola, InventarioVista vista) {
        super(consola, vista);
        this.vista = vista;
        this.servicio = new InventarioServicio();
        this.proveedores = new ProveedorVista(consola);
        // Visto en clase: expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
        registrar("1", () -> registrarRepuesto());
        registrar("2", () -> registrarLote());
        registrar("3", () -> vista.mostrarRepuestos(servicio.listarRepuestos()));
        registrar("4", () -> vista.mostrarRepuestos(servicio.buscar(consola.leerTexto("Texto a buscar: "))));
        registrar("5", () -> verLotes());
        registrar("6", () -> actualizarPrecios());
        registrar("7", () -> corregirCaducidad());
        registrar("8", () -> anularLote());
        registrar("9", () -> eliminarRepuesto());
        registrar("10", () -> vista.mostrarTipos(servicio.listarTipos()));
        registrar("11", () -> registrarTipo());
        registrar("12", () -> renombrarTipo());
        registrar("13", () -> eliminarTipo());
    }

    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    private void registrarRepuesto() throws SQLException {
        String nombre = consola.leerTextoObligatorio("Nombre: ");
        referencia("Tipos de repuesto", () -> vista.mostrarTipos(servicio.listarTipos()));
        int tipo = consola.leerEntero("Id del tipo: ");
        String marca = consola.leerTexto("Marca: ");
        referencia("Proveedores disponibles", () -> proveedores.mostrarProveedores(servicio.listarProveedores()));
        int proveedor = consola.leerEntero("Id del proveedor asociado: ");
        int compra = consola.leerMonto("Precio de compra: ");
        int venta = consola.leerMonto("Precio de venta: ");
        int minimo = consola.leerEntero("Stock mínimo: ");
        int id = servicio.registrarRepuesto(nombre, tipo, marca, proveedor, compra, venta, minimo);
        consola.mostrarExito("Repuesto #" + id + " registrado con stock 0. Ingrese un lote para sumar existencias.");
    }

    private void registrarLote() throws SQLException {
        String codigo = consola.leerTextoObligatorio("Código único del lote (ej. LOT-2026-100): ");
        referenciaRepuestos();
        int repuesto = consola.leerEntero("Id del repuesto: ");
        referencia("Proveedores disponibles", () -> proveedores.mostrarProveedores(servicio.listarProveedores()));
        int proveedor = consola.leerEntero("Id del proveedor que entrega: ");
        int cantidad = consola.leerEntero("Cantidad recibida: ");
        String caducidad = consola.leerFechaOpcional("Fecha de caducidad AAAA-MM-DD (Enter si no aplica): ");
        servicio.registrarLote(codigo, repuesto, proveedor, cantidad, caducidad);
        Repuesto actualizado = servicio.obtenerRepuesto(repuesto);
        consola.mostrarExito("Lote registrado. Stock actual de " + actualizado.getNombre() + ": "
                + actualizado.getCantidadStock() + ".");
    }

    private void verLotes() throws SQLException {
        referenciaRepuestos();
        int id = consola.leerEntero("Id del repuesto: ");
        vista.mostrarLotes(servicio.obtenerRepuesto(id), servicio.listarLotes(id));
    }

    private void actualizarPrecios() throws SQLException {
        referenciaRepuestos();
        int id = consola.leerEntero("Id del repuesto: ");
        vista.mostrarFicha(servicio.obtenerRepuesto(id));
        int compra = consola.leerMonto("Nuevo precio de compra: ");
        int venta = consola.leerMonto("Nuevo precio de venta: ");
        int minimo = consola.leerEntero("Nuevo stock mínimo: ");
        servicio.actualizarPrecios(id, compra, venta, minimo);
        consola.mostrarExito("Ficha actualizada.");
    }

    private void corregirCaducidad() throws SQLException {
        referenciaLotes();
        String codigo = consola.leerTextoObligatorio("Código del lote: ");
        String caducidad = consola.leerFechaOpcional("Nueva caducidad AAAA-MM-DD (Enter = no caduca): ");
        servicio.actualizarCaducidadLote(codigo, caducidad);
        consola.mostrarExito("Caducidad actualizada.");
    }

    private void anularLote() throws SQLException {
        referenciaLotes();
        String codigo = consola.leerTextoObligatorio("Código del lote: ");
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (consola.confirmar("Se restará la cantidad del lote al stock.")) {
            servicio.eliminarLote(codigo);
            consola.mostrarExito("Lote anulado y stock corregido.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void eliminarRepuesto() throws SQLException {
        referenciaRepuestos();
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
        referencia("Tipos de repuesto", () -> vista.mostrarTipos(servicio.listarTipos()));
        int id = consola.leerEntero("Id del tipo: ");
        servicio.renombrarTipo(id, consola.leerTextoObligatorio("Nuevo nombre: "));
        consola.mostrarExito("Tipo actualizado.");
    }

    private void eliminarTipo() throws SQLException {
        referencia("Tipos de repuesto", () -> vista.mostrarTipos(servicio.listarTipos()));
        servicio.eliminarTipo(consola.leerEntero("Id del tipo: "));
        consola.mostrarExito("Tipo eliminado.");
    }

    // ------------------------------------------------------------ Referencias antes de pedir un dato

    private void referenciaRepuestos() throws SQLException {
        referencia("Repuestos del inventario", () -> vista.mostrarRepuestos(servicio.listarRepuestos()));
    }

    private void referenciaLotes() throws SQLException {
        referencia("Lotes registrados", () -> vista.mostrarListaLotes(servicio.listarTodosLotes()));
    }
}
