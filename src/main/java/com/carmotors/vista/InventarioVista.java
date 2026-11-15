
package com.carmotors.vista;

import com.carmotors.modelo.Lote;
import com.carmotors.modelo.Repuesto;
import com.carmotors.modelo.TipoRepuesto;
import com.carmotors.vista.reporte.ReporteAlertasInventario;

import java.util.List;
import java.util.Map;

public class InventarioVista implements VistaMenu {
    private static final List<String> OPCIONES = List.of(
            "Registrar repuesto",
            "Ingresar lote (entrada de mercancía)",
            "Listar inventario",
            "Buscar repuesto por nombre, marca o tipo",
            "Trazabilidad: lotes de un repuesto",
            "Actualizar precios y stock mínimo",
            "Reporte de alertas (stock mínimo y caducidad)",
            "Corregir caducidad de un lote",
            "Anular lote ingresado por error",
            "Eliminar repuesto sin existencias",
            "Tipos de repuesto: listar",
            "Tipos de repuesto: registrar",
            "Tipos de repuesto: renombrar",
            "Tipos de repuesto: eliminar");

    private final VistaConsola consola;

    public InventarioVista(VistaConsola consola) {
        this.consola = consola;
    }

    @Override
    public String titulo() {
        return "Gestión de inventario";
    }

    @Override
    public Map<String, String> opciones() {
        return VistaConsola.numerar(OPCIONES);
    }

    @Override
    public String textoSalida() {
        return "Volver al menú principal";
    }

    public void mostrarTipos(List<TipoRepuesto> tipos) {
        if (tipos.isEmpty()) {
            consola.mostrar("No hay tipos de repuesto registrados.");
        }
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

    public void mostrarAlertas(List<Repuesto> bajoMinimo, List<Lote> porVencer, List<Repuesto> catalogo, int dias) {
        consola.mostrarLineas(new ReporteAlertasInventario(bajoMinimo, porVencer, catalogo, dias).generar());
    }
}
