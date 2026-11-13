/*
 * ============================================================
 * Archivo: ConsumoRepuesto.java
 * Propósito: fila del reporte "Análisis de consumo de repuestos".
 * Conceptos clave: objeto de valor inmutable (private final + getters,
 *            sesión 06), resultado calculado con Streams en la capa de servicio.
 * Justificación: el servicio CALCULA y devuelve datos; la vista decide cómo
 *            mostrarlos. Así la capa de servicio no contiene formato de
 *            presentación (SRP y separación MVC).
 * ============================================================
 */
package com.carmotors.modelo;

public class ConsumoRepuesto {
    private final Repuesto repuesto;
    private final int usadas;      // unidades DEDUCIDAS (consumo real)
    private final int reservadas;  // unidades RESERVADAS (consumo futuro)
    private final int ingreso;    // venta de las unidades usadas
    private final int margen;     // ingreso - costo de compra

    public ConsumoRepuesto(Repuesto repuesto, int usadas, int reservadas, int ingreso, int margen) {
        this.repuesto = repuesto;
        this.usadas = usadas;
        this.reservadas = reservadas;
        this.ingreso = ingreso;
        this.margen = margen;
    }

    public Repuesto getRepuesto() {
        return repuesto;
    }

    public int getUsadas() {
        return usadas;
    }

    public int getReservadas() {
        return reservadas;
    }

    public int getIngreso() {
        return ingreso;
    }

    public int getMargen() {
        return margen;
    }
}
