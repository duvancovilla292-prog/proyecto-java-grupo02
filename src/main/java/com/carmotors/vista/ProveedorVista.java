/*
 * ============================================================
 * Archivo: ProveedorVista.java
 * Propósito: menú y presentación del Módulo de Proveedores: proveedores,
 *            catálogos, evaluaciones y ranking.
 * Conceptos clave: MVC - Vista, implementa VistaMenu, composición con
 *            VistaConsola, TEMPLATE METHOD (ReporteDesempenoProveedores).
 * Justificación: mantiene el formato de proveedores fuera del controlador
 *            y del servicio (SRP).
 * ============================================================
 */
package com.carmotors.vista;

import com.carmotors.modelo.DesempenoProveedor;
import com.carmotors.modelo.EvaluacionProveedor;
import com.carmotors.modelo.ProductoCatalogo;
import com.carmotors.modelo.Proveedor;
import com.carmotors.vista.reporte.ReporteDesempenoProveedores;

import java.util.List;
import java.util.Map;

public class ProveedorVista implements VistaMenu {
    private static final List<String> OPCIONES = List.of(
            "Registrar proveedor",
            "Listar proveedores",
            "Actualizar datos de contacto",
            "Eliminar proveedor",
            "Agregar o actualizar producto en catálogo",
            "Quitar producto del catálogo",
            "Ver catálogo de un proveedor",
            "Comparar precios de un repuesto entre proveedores",
            "Evaluar proveedor (calidad y puntualidad)",
            "Ver evaluaciones de un proveedor",
            "Corregir evaluación",
            "Eliminar evaluación",
            "Ranking de desempeño");

    private final VistaConsola consola;

    public ProveedorVista(VistaConsola consola) {
        this.consola = consola;
    }

    @Override
    public String titulo() {
        return "Gestión de proveedores";
    }

    @Override
    public Map<String, String> opciones() {
        return VistaConsola.numerar(OPCIONES);
    }

    @Override
    public String textoSalida() {
        return "Volver al menú principal";
    }

    public void mostrarProveedores(List<Proveedor> proveedores) {
        if (proveedores.isEmpty()) {
            consola.mostrar("No hay proveedores registrados.");
            return;
        }
        for (Proveedor p : proveedores) {
            consola.mostrar("  #" + Formato.ajustar(String.valueOf(p.getId()), 4) + Formato.ajustar(p.getNombre(), 26)
                    + Formato.ajustar(p.getContacto(), 18) + Formato.ajustar(p.getTelefono(), 12) + p.getEmail());
        }
    }

    /** Catálogo; "porProveedor" decide si la columna relevante es el repuesto o el proveedor. */
    public void mostrarCatalogo(String encabezado, List<ProductoCatalogo> productos, boolean porProveedor) {
        consola.mostrar(encabezado);
        if (productos.isEmpty()) {
            consola.mostrar("  (Sin productos en el catálogo)");
            return;
        }
        for (ProductoCatalogo p : productos) {
            String nombre = porProveedor
                    ? "#" + p.getRepuestoId() + " " + p.getNombreRepuesto()
                    : "#" + p.getProveedorId() + " " + p.getNombreProveedor();
            consola.mostrar("  " + Formato.ajustar(nombre, 36) + Formato.derecha(Formato.pesos(p.getCosto()), 12));
        }
    }

    public void mostrarEvaluaciones(List<EvaluacionProveedor> evaluaciones) {
        if (evaluaciones.isEmpty()) {
            consola.mostrar("  (Sin evaluaciones)");
        }
        for (EvaluacionProveedor e : evaluaciones) {
            consola.mostrar("  #" + Formato.ajustar(String.valueOf(e.getId()), 4) + Formato.fecha(e.getFecha())
                    + " | calidad " + e.getCalidad() + " | puntualidad " + e.getPuntualidad() + " | " + e.getComentario());
        }
    }

    public void mostrarRanking(List<DesempenoProveedor> ranking) {
        consola.mostrarLineas(new ReporteDesempenoProveedores(ranking).generar());
    }
}
