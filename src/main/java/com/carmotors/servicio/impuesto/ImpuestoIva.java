/*
 * ============================================================
 * Archivo: ImpuestoIva.java
 * Propósito: estrategia de IVA con porcentaje configurable (19 % en Colombia).
 * Conceptos clave: PATRÓN STRATEGY, aritmética entera con redondeo
 *            (operadores de la sesión 02), validación en constructor.
 * Justificación: se trabaja con pesos enteros como en los demos;
 *            sumar 50 antes de dividir entre 100 redondea al peso más cercano.
 * ============================================================
 */
package com.carmotors.servicio.impuesto;

import com.carmotors.excepcion.DatoInvalidoException;

public class ImpuestoIva implements PoliticaImpuesto {
    private final int porcentaje;

    public ImpuestoIva(int porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new DatoInvalidoException("Porcentaje de IVA entre 0 y 100.");
        }
        this.porcentaje = porcentaje;
    }

    @Override
    public int calcular(int baseGravable) {
        return (baseGravable * porcentaje + 50) / 100;
    }

    @Override
    public String descripcion() {
        return "IVA " + porcentaje + "%";
    }
}
