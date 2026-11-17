/*
 * ============================================================
 * Archivo: ImpuestoExento.java
 * Propósito: estrategia alternativa sin impuesto (servicios exentos).
 * Conceptos clave: PATRÓN STRATEGY: segunda implementación intercambiable.
 * Justificación: demuestra que la facturación cambia de comportamiento
 *            con solo inyectar otra estrategia al armar la aplicación (Liskov:
 *            cualquier PoliticaImpuesto sirve donde se espera la interfaz).
 * ============================================================
 */
package com.carmotors.servicio.impuesto;

public class ImpuestoExento implements PoliticaImpuesto {
    @Override
    public int calcular(int baseGravable) {
        return 0;
    }

    @Override
    public String descripcion() {
        return "Exento";
    }
}
