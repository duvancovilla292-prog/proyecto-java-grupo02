/*
 * ============================================================
 * Archivo: Factura.java
 * Propósito: entidad de la tabla facturas.
 * Conceptos clave: fecha de emisión como String (la asigna MySQL con NOW()),
 *            validación de coherencia de totales (encapsulamiento, S06).
 * Justificación: el constructor comprueba que total = repuestos + mano
 *            de obra + impuestos; así una factura incoherente no puede
 *            existir en memoria.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class Factura {
    private final int id;
    private final int servicioId;
    private final String fechaEmision;  // null hasta que se guarda
    private final int totalRepuestos;
    private final int totalManoObra;
    private final int impuestos;
    private final int totalFinal;

    public Factura(int id, int servicioId, String fechaEmision, int totalRepuestos,
                   int totalManoObra, int impuestos, int totalFinal) {
        if (servicioId <= 0) {
            throw new DatoInvalidoException("El servicio es obligatorio.");
        }
        if (totalRepuestos < 0 || totalManoObra < 0 || impuestos < 0
                || totalFinal != totalRepuestos + totalManoObra + impuestos) {
            throw new DatoInvalidoException("Totales de factura incoherentes.");
        }
        this.id = id;
        this.servicioId = servicioId;
        this.fechaEmision = fechaEmision;
        this.totalRepuestos = totalRepuestos;
        this.totalManoObra = totalManoObra;
        this.impuestos = impuestos;
        this.totalFinal = totalFinal;
    }

    public int getId() {
        return id;
    }

    public int getServicioId() {
        return servicioId;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public int getTotalRepuestos() {
        return totalRepuestos;
    }

    public int getTotalManoObra() {
        return totalManoObra;
    }

    public int getImpuestos() {
        return impuestos;
    }

    public int getTotalFinal() {
        return totalFinal;
    }
}
