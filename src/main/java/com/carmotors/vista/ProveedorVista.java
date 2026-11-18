/*
 * ============================================================
 * Archivo: ProveedorVista.java
 * Propósito: menú y presentación del Módulo de Proveedores: proveedores,
 *            catálogos y evaluaciones.
 * Conceptos clave: MVC - Vista, implementa VistaMenu, composición con
 *            VistaConsola.
 * Justificación: mantiene el formato de proveedores fuera del controlador
 *            y del servicio (SRP).
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
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 * ============================================================
 */
package com.carmotors.vista;

import com.carmotors.modelo.EvaluacionProveedor;
import com.carmotors.modelo.ProductoCatalogo;
import com.carmotors.modelo.Proveedor;

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
            "Eliminar evaluación");

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

}
