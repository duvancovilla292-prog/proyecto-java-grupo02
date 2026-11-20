/*
 * ============================================================
 * Archivo: ImpuestoExento.java
 * Propósito: estrategia alternativa sin impuesto (servicios exentos).
 * Conceptos clave: PATRÓN STRATEGY: segunda implementación intercambiable.
 * Justificación: demuestra que la facturación cambia de comportamiento
 *            con solo inyectar otra estrategia al armar la aplicación (Liskov:
 *            cualquier PoliticaImpuesto sirve donde se espera la interfaz).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.servicio.impuesto;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN STRATEGY -> S14Demo00 CalculadorEnvio (Sesión 14)
public class ImpuestoExento implements PoliticaImpuesto {
    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    @Override
    public int calcular(int baseGravable) {
        return 0;
    }

    @Override
    public String descripcion() {
        return "Exento";
    }
}
