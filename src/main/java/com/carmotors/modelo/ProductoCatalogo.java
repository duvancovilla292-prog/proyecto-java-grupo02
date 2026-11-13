/*
 * ============================================================
 * Archivo: ProductoCatalogo.java
 * Propósito: fila de proveedor_catalogo: qué repuesto ofrece un
 *            proveedor y a qué costo.
 * Conceptos clave: objeto de valor inmutable; campos de solo lectura
 *            llenados por JOIN para mostrar nombres.
 * Justificación: permite comparar con Streams el costo del mismo
 *            repuesto entre varios proveedores.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class ProductoCatalogo {
    private final int proveedorId;
    private final String nombreProveedor; // Solo lectura (JOIN)
    private final int repuestoId;
    private final String nombreRepuesto;  // Solo lectura (JOIN)
    private final int costo;             // pesos

    public ProductoCatalogo(int proveedorId, String nombreProveedor, int repuestoId,
                            String nombreRepuesto, int costo) {
        if (proveedorId <= 0 || repuestoId <= 0 || costo <= 0) {
            throw new DatoInvalidoException("Proveedor, repuesto y costo positivo son obligatorios.");
        }
        this.proveedorId = proveedorId;
        this.nombreProveedor = nombreProveedor == null ? "" : nombreProveedor;
        this.repuestoId = repuestoId;
        this.nombreRepuesto = nombreRepuesto == null ? "" : nombreRepuesto;
        this.costo = costo;
    }

    public int getProveedorId() {
        return proveedorId;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public int getRepuestoId() {
        return repuestoId;
    }

    public String getNombreRepuesto() {
        return nombreRepuesto;
    }

    public int getCosto() {
        return costo;
    }
}
