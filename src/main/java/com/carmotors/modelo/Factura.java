/*
 * ============================================================
 * Archivo: Factura.java
 * Propósito: entidad de la tabla facturas.
 * Conceptos clave: fecha de emisión como String (la asigna MySQL con NOW()),
 *            validación de coherencia de totales (encapsulamiento, S06).
 * Justificación: el constructor comprueba que total = repuestos + mano
 *            de obra + impuestos; así una factura incoherente no puede
 *            existir en memoria.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class Factura {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final int id;
    private final int servicioId;
    private final String fechaEmision;  // null hasta que se guarda
    private final int totalRepuestos;
    private final int totalManoObra;
    private final int impuestos;
    private final int totalFinal;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public Factura(int id, int servicioId, String fechaEmision, int totalRepuestos,
                   int totalManoObra, int impuestos, int totalFinal) {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (servicioId <= 0) {
            // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
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
