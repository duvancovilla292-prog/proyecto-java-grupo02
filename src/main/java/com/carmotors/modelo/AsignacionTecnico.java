/*
 * ============================================================
 * Archivo: AsignacionTecnico.java
 * Propósito: fila de la tabla de unión servicios_tecnicos.
 * Conceptos clave: objeto de valor inmutable.
 * Justificación: representar la relación muchos-a-muchos como objeto
 *            permite procesarla con Streams en el reporte de productividad.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class AsignacionTecnico {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final int servicioId;
    private final int tecnicoId;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public AsignacionTecnico(int servicioId, int tecnicoId) {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (servicioId <= 0 || tecnicoId <= 0) {
            // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
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
