/*
 * ============================================================
 * Archivo: PoliticaImpuesto.java
 * Propósito: contrato para calcular el impuesto de una factura.
 * Conceptos clave: PATRÓN STRATEGY (S14Demo00 CalculadorEnvio), interfaz
 *            funcional de un solo método de cálculo (sesión 19).
 * Justificación: si cambia la tarifa (IVA 19 %, exento, otra tasa) se crea
 *            otra estrategia sin modificar la facturación (OCP) y el
 *            servicio depende de esta abstracción (DIP).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.servicio.impuesto;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN STRATEGY -> S14Demo00 CalculadorEnvio (Sesión 14)
public interface PoliticaImpuesto {
    /** Impuesto en pesos para la base gravable indicada. */
    int calcular(int baseGravable);

    /** Texto que aparece en la factura, ej: "IVA 19%". */
    String descripcion();
}
