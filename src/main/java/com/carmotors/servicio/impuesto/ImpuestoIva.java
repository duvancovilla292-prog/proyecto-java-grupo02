/*
 * ============================================================
 * Archivo: ImpuestoIva.java
 * Propósito: estrategia de IVA con porcentaje configurable (19 % en Colombia).
 * Conceptos clave: PATRÓN STRATEGY, aritmética entera con redondeo
 *            (operadores de la sesión 02), validación en constructor.
 * Justificación: se trabaja con pesos enteros como en los demos;
 *            sumar 50 antes de dividir entre 100 redondea al peso más cercano.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
 *   - interfaces e implements -> S07Demo04 Notificable (Sesión 07)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN STRATEGY -> S14Demo00 CalculadorEnvio (Sesión 14)
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
