/*
 * ============================================================
 * Archivo: Repuesto.java
 * Propósito: entidad de la tabla repuestos (ficha del inventario).
 * Conceptos clave: encapsulamiento, validación de invariantes, montos en
 *            pesos enteros con int (como Billetera de S06Demo00 y Envio
 *            de S07Demo01), métodos de consulta derivados.
 * Justificación: el objeto concentra las reglas que dependen SOLO de sus
 *            datos (disponible = stock - reservado; ¿está bajo mínimo?).
 *            Las reglas que coordinan varias entidades (reservar, deducir)
 *            están en la capa de servicio (SRP).
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class Repuesto {
    private final int id;
    private final String nombre;
    private final int tipoId;            // FK -> repuesto_tipos.id
    private final String nombreTipo;     // Solo lectura: lo llena el DAO con un JOIN
    private final String marca;
    private final int proveedorId;       // FK -> proveedores.id (proveedor asociado)
    private final int precioCompra;     // pesos
    private final int precioVenta;      // pesos
    private final int cantidadStock;     // unidades físicas en el almacén
    private final int stockMinimo;       // umbral de alerta
    private final int cantidadReservada; // unidades comprometidas con órdenes abiertas

    public Repuesto(int id, String nombre, int tipoId, String nombreTipo, String marca, int proveedorId,
                    int precioCompra, int precioVenta, int cantidadStock, int stockMinimo,
                    int cantidadReservada) {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("Nombre de repuesto obligatorio.");
        }
        if (tipoId <= 0 || proveedorId <= 0) {
            throw new DatoInvalidoException("Tipo y proveedor son obligatorios.");
        }
        if (precioCompra < 0 || precioVenta < precioCompra) {
            throw new DatoInvalidoException("Precios inválidos: la venta no puede ser menor que la compra.");
        }
        if (cantidadStock < 0 || stockMinimo < 0 || cantidadReservada < 0 || cantidadReservada > cantidadStock) {
            throw new DatoInvalidoException("Cantidades inválidas: la reserva no puede superar el stock.");
        }
        this.id = id;
        this.nombre = nombre.strip();
        this.tipoId = tipoId;
        this.nombreTipo = nombreTipo == null ? "" : nombreTipo;
        this.marca = marca == null ? "" : marca.strip();
        this.proveedorId = proveedorId;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.cantidadStock = cantidadStock;
        this.stockMinimo = stockMinimo;
        this.cantidadReservada = cantidadReservada;
    }

    /** Unidades que todavía se pueden asignar a nuevas órdenes. */
    public int getDisponible() {
        return cantidadStock - cantidadReservada;
    }

    /** Regla de alerta: lo disponible quedó por debajo del stock mínimo. */
    public boolean estaBajoMinimo() {
        return getDisponible() < stockMinimo;
    }

    /** Ganancia unitaria (precio de venta - costo). */
    public int margenUnitario() {
        return precioVenta - precioCompra;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTipoId() {
        return tipoId;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public String getMarca() {
        return marca;
    }

    public int getProveedorId() {
        return proveedorId;
    }

    public int getPrecioCompra() {
        return precioCompra;
    }

    public int getPrecioVenta() {
        return precioVenta;
    }

    public int getCantidadStock() {
        return cantidadStock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public int getCantidadReservada() {
        return cantidadReservada;
    }
}
