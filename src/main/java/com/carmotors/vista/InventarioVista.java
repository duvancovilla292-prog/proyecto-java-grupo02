/*
 * ============================================================
 * Archivo: InventarioVista.java
 * Propósito: menú y presentación del Módulo de Inventario: tipos,
 *            repuestos y lotes.
 * Conceptos clave: MVC - Vista, implementa VistaMenu, composición con
 *            VistaConsola.
 * Justificación: muestra stock físico, reservado y disponible por separado
 *            para que el jefe de taller vea el efecto de la regla "Reservado".
 *            mostrarListaLotes sirve de referencia al pedir un código de lote.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.vista;

import com.carmotors.modelo.Lote;
import com.carmotors.modelo.Repuesto;
import com.carmotors.modelo.TipoRepuesto;

import java.util.List;
import java.util.Map;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public class InventarioVista implements VistaMenu {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    // Visto en clase: List.of / List.copyOf -> S19Demo03ReglasOrdenMain List.of y S20Demo02 Orden List.copyOf (Sesiones 19 y 20)
    private static final List<String> OPCIONES = List.of(
            "Registrar repuesto",
            "Ingresar lote (entrada de mercancía)",
            "Listar inventario",
            "Buscar repuesto por nombre, marca o tipo",
            "Trazabilidad: lotes de un repuesto",
            "Actualizar precios y stock mínimo",
            "Corregir caducidad de un lote",
            "Anular lote ingresado por error",
            "Eliminar repuesto sin existencias",
            "Tipos de repuesto: listar",
            "Tipos de repuesto: registrar",
            "Tipos de repuesto: renombrar",
            "Tipos de repuesto: eliminar");

    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final VistaConsola consola;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public InventarioVista(VistaConsola consola) {
        this.consola = consola;
    }

    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    @Override
    public String titulo() {
        return "Gestión de inventario";
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

    public void mostrarTipos(List<TipoRepuesto> tipos) {
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (tipos.isEmpty()) {
            consola.mostrar("No hay tipos de repuesto registrados.");
        }
        // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
        for (TipoRepuesto t : tipos) {
            consola.mostrar("  " + t.getId() + ". " + t.getNombre());
        }
    }

    public void mostrarRepuestos(List<Repuesto> repuestos) {
        if (repuestos.isEmpty()) {
            consola.mostrar("No se encontraron repuestos.");
            return;
        }
        consola.mostrar(Formato.ajustar("Id", 4) + Formato.ajustar("Nombre", 30) + Formato.ajustar("Tipo", 16)
                + Formato.ajustar("Marca", 10) + Formato.derecha("Venta", 11) + Formato.derecha("Stock", 7)
                + Formato.derecha("Reserv", 7) + Formato.derecha("Disp", 6) + Formato.derecha("Mín", 5));
        for (Repuesto r : repuestos) {
            // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
            // Visto en clase: conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
            consola.mostrar(Formato.ajustar(String.valueOf(r.getId()), 4) + Formato.ajustar(r.getNombre(), 30)
                    + Formato.ajustar(r.getNombreTipo(), 16) + Formato.ajustar(r.getMarca(), 10)
                    + Formato.derecha(Formato.pesos(r.getPrecioVenta()), 11)
                    + Formato.derecha(String.valueOf(r.getCantidadStock()), 7)
                    + Formato.derecha(String.valueOf(r.getCantidadReservada()), 7)
                    + Formato.derecha(String.valueOf(r.getDisponible()), 6)
                    + Formato.derecha(String.valueOf(r.getStockMinimo()), 5)
                    + (r.estaBajoMinimo() ? "  <- BAJO MÍNIMO" : ""));
        }
    }

    /** Resumen de un repuesto antes de modificarlo. */
    public void mostrarFicha(Repuesto r) {
        consola.mostrar(r.getNombre() + " | compra " + Formato.pesos(r.getPrecioCompra()) + " | venta "
                + Formato.pesos(r.getPrecioVenta()) + " | stock " + r.getCantidadStock() + " | mínimo "
                + r.getStockMinimo());
    }

    public void mostrarLotes(Repuesto repuesto, List<Lote> lotes) {
        consola.mostrar("Lotes de " + repuesto.getNombre() + " (proveedor asociado #" + repuesto.getProveedorId() + "):");
        if (lotes.isEmpty()) {
            consola.mostrar("  (Sin lotes registrados)");
        }
        for (Lote l : lotes) {
            consola.mostrar("  " + Formato.ajustar(l.getCodigo(), 14) + " proveedor #" + l.getProveedorId()
                    + " | ingreso " + Formato.fecha(l.getFechaIngreso())
                    + " | caduca " + Formato.fecha(l.getFechaCaducidad()) + " | " + l.getCantidad() + " und.");
        }
    }

    /** Lotes de todos los repuestos: referencia al pedir un código de lote. */
    public void mostrarListaLotes(List<Lote> lotes) {
        if (lotes.isEmpty()) {
            consola.mostrar("  (Sin lotes registrados)");
            return;
        }
        consola.mostrar("  " + Formato.ajustar("Código", 15) + Formato.ajustar("Repuesto", 10)
                + Formato.ajustar("Ingreso", 12) + Formato.ajustar("Caduca", 12) + "Cantidad");
        for (Lote l : lotes) {
            consola.mostrar("  " + Formato.ajustar(l.getCodigo(), 15) + Formato.ajustar("#" + l.getRepuestoId(), 10)
                    + Formato.ajustar(Formato.fecha(l.getFechaIngreso()), 12)
                    + Formato.ajustar(Formato.fecha(l.getFechaCaducidad()), 12) + l.getCantidad() + " und.");
        }
    }

}
