/*
 * ============================================================
 * Archivo: FacturaDetallada.java
 * Propósito: todos los datos necesarios para presentar una factura: factura, orden, cliente, vehículo, repuestos, técnicos e impuesto aplicado.
 * Conceptos clave: objeto de valor inmutable (private final + getters,
 *            sesión 06), resultado calculado con Streams en la capa de servicio.
 * Justificación: el servicio CALCULA y devuelve datos; la vista decide cómo
 *            mostrarlos. Así la capa de servicio no contiene formato de
 *            presentación (SRP y separación MVC).
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
