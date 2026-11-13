/*
 * ============================================================
 * Archivo: AsignacionTecnico.java
 * Propósito: fila de la tabla de unión servicios_tecnicos.
 * Conceptos clave: objeto de valor inmutable.
 * Justificación: representar la relación muchos-a-muchos como objeto
 *            permite procesarla con Streams en el reporte de productividad.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class AsignacionTecnico {
    private final int servicioId;
    private final int tecnicoId;

    public AsignacionTecnico(int servicioId, int tecnicoId) {
        if (servicioId <= 0 || tecnicoId <= 0) {
            throw new DatoInvalidoException("Servicio y técnico son obligatorios.");
        }
        this.servicioId = servicioId;
        this.tecnicoId = tecnicoId;
    }

    public int getServicioId() {
        return servicioId;
    }

    public int getTecnicoId() {
        return tecnicoId;
    }
}
