/*
 * ============================================================
 * Archivo: AlertaStock.java
 * Propósito: fila del reporte "Alertas de inventario": un repuesto cuyo
 *            disponible (stock - reservado) está por debajo del mínimo.
 * Conceptos clave: objeto de valor inmutable (private final + getters,
 *            sesión 06), dato calculado (disponible) en un método.
 * Justificación: el reporte solo necesita estos cinco datos; no se carga la
 *            entidad Repuesto completa (tipo, proveedor, precios).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo.reporte;

public class AlertaStock {
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final int repuestoId;
    private final String nombre;
    private final int stock;
    private final int reservado;
    private final int stockMinimo;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public AlertaStock(int repuestoId, String nombre, int stock, int reservado, int stockMinimo) {
        this.repuestoId = repuestoId;
        this.nombre = nombre;
        this.stock = stock;
        this.reservado = reservado;
        this.stockMinimo = stockMinimo;
    }

    /** Unidades libres para nuevas órdenes. */
    public int disponible() {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        return stock - reservado;
    }

    /** Cuántas unidades faltan para llegar al mínimo (negativo = más crítico). */
    public int holgura() {
        return disponible() - stockMinimo;
    }

    public int getRepuestoId() {
        return repuestoId;
    }

    public String getNombre() {
        return nombre;
    }

    public int getStock() {
        return stock;
    }

    public int getReservado() {
        return reservado;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }
}
