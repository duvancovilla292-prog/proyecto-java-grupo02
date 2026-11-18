/*
 * ============================================================
 * Archivo: ImpuestoExento.java
 * Propósito: estrategia alternativa sin impuesto (servicios exentos).
 * Conceptos clave: PATRÓN STRATEGY: segunda implementación intercambiable.
 * Justificación: demuestra que la facturación cambia de comportamiento
 *            con solo inyectar otra estrategia al armar la aplicación (Liskov:
 *            cualquier PoliticaImpuesto sirve donde se espera la interfaz).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
 *   - interfaces e implements -> S07Demo04 Notificable (Sesión 07)
 *   - PATRÓN STRATEGY -> S14Demo00 CalculadorEnvio (Sesión 14)
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
