/*
 * ============================================================
 * Archivo: ConsumoRepuesto.java
 * Propósito: fila del reporte "Análisis de consumo de repuestos".
 * Conceptos clave: objeto de valor inmutable (private final + getters,
 *            sesión 06), agregado por MySQL (SUM + GROUP BY) en ReporteDAO.
 * Justificación: el servicio CALCULA y devuelve datos; la vista decide cómo
 *            mostrarlos. Así la capa de servicio no contiene formato de
 *            presentación (SRP y separación MVC).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 * ============================================================
 */
package com.carmotors.modelo.reporte;

public class ConsumoRepuesto {
    private final int repuestoId;
    private final String nombreRepuesto;
    private final int costoUnitario;  // precio de compra del repuesto
    private final int usadas;         // unidades DEDUCIDAS (consumo real)
    private final int reservadas;     // unidades RESERVADAS (consumo futuro)
    private final int ingreso;        // venta de las unidades usadas

    public ConsumoRepuesto(int repuestoId, String nombreRepuesto, int costoUnitario, int usadas, int reservadas,
                           int ingreso) {
        this.repuestoId = repuestoId;
        this.nombreRepuesto = nombreRepuesto;
        this.costoUnitario = costoUnitario;
        this.usadas = usadas;
        this.reservadas = reservadas;
        this.ingreso = ingreso;
    }

    /** Margen = ingreso por las unidades usadas - lo que costó comprarlas. */
    public int margen() {
        return ingreso - usadas * costoUnitario;
    }

    /** true si el repuesto tuvo algún movimiento (usado o reservado). */
    public boolean tuvoMovimiento() {
        return usadas > 0 || reservadas > 0;
    }

    public int getRepuestoId() {
        return repuestoId;
    }

    public String getNombreRepuesto() {
        return nombreRepuesto;
    }

    public int getCostoUnitario() {
        return costoUnitario;
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
}
