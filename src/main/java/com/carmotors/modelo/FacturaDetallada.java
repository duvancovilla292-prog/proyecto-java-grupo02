/*
 * ============================================================
 * Archivo: FacturaDetallada.java
 * Propósito: todos los datos necesarios para presentar una factura: factura, orden, cliente, vehículo, repuestos, técnicos e impuesto aplicado.
 * Conceptos clave: objeto de valor inmutable (private final + getters,
 *            sesión 06), resultado calculado con Streams en la capa de servicio.
 * Justificación: el servicio CALCULA y devuelve datos; la vista decide cómo
 *            mostrarlos. Así la capa de servicio no contiene formato de
 *            presentación (SRP y separación MVC).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - List.of / List.copyOf -> S19Demo03ReglasOrdenMain List.of y S20Demo02 Orden List.copyOf (Sesiones 19 y 20)
 * ============================================================
 */
package com.carmotors.modelo;

import java.util.List;

public class FacturaDetallada {
    private final Factura factura;
    private final OrdenServicio orden;
    private final Cliente cliente;
    private final Vehiculo vehiculo;
    private final List<DetalleRepuesto> repuestos;
    private final List<Tecnico> tecnicos;
    private final String descripcionImpuesto;  // ej. "IVA 19%"

    public FacturaDetallada(Factura factura, OrdenServicio orden, Cliente cliente, Vehiculo vehiculo,
                            List<DetalleRepuesto> repuestos, List<Tecnico> tecnicos, String descripcionImpuesto) {
        this.factura = factura;
        this.orden = orden;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.repuestos = List.copyOf(repuestos);  // copia defensiva (S20Demo02 Orden)
        this.tecnicos = List.copyOf(tecnicos);
        this.descripcionImpuesto = descripcionImpuesto;
    }

    /** Número legible de la factura: FAC-000007. */
    public String numero() {
        String id = String.valueOf(factura.getId());
        while (id.length() < 6) {
            id = "0" + id;
        }
        return "FAC-" + id;
    }

    public Factura getFactura() {
        return factura;
    }

    public OrdenServicio getOrden() {
        return orden;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public List<DetalleRepuesto> getRepuestos() {
        return repuestos;
    }

    public List<Tecnico> getTecnicos() {
        return tecnicos;
    }

    public String getDescripcionImpuesto() {
        return descripcionImpuesto;
    }
}
