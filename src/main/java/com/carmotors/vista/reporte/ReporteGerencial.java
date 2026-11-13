/*
 * ============================================================
 * Archivo: ReporteGerencial.java
 * Propósito: plantilla común de presentación de todos los reportes
 *            (encabezado, cuerpo, pie).
 * Conceptos clave: PATRÓN TEMPLATE METHOD (S14Demo05): generar() es final y
 *            fija el algoritmo; las subclases implementan titulo() y cuerpo().
 *            Clase abstracta (S07Demo03 Poliza).
 * Justificación: los seis reportes comparten formato; el esqueleto se escribe
 *            una vez y cada reporte nuevo se agrega con una subclase (OCP). Vive
 *            en la capa vista porque solo transforma datos ya calculados por
 *            los servicios en líneas de texto para la consola.
 * ============================================================
 */
package com.carmotors.vista.reporte;

import java.util.ArrayList;
import java.util.List;

public abstract class ReporteGerencial {
    protected static final String SEPARADOR = "==============================================================";

    /** Método plantilla: el orden de los pasos no se puede alterar en las subclases. */
    public final List<String> generar() {
        List<String> lineas = new ArrayList<>();
        lineas.add(SEPARADOR);
        lineas.add(titulo().toUpperCase());
        lineas.add(SEPARADOR);
        List<String> cuerpo = cuerpo();
        if (cuerpo.isEmpty()) {
            lineas.add(mensajeSinDatos());
        } else {
            lineas.addAll(cuerpo);
        }
        lineas.add(SEPARADOR);
        return lineas;
    }

    /** Paso obligatorio: título del reporte. */
    protected abstract String titulo();

    /** Paso obligatorio: líneas con los datos. */
    protected abstract List<String> cuerpo();

    /** Paso opcional (hook): mensaje cuando no hay datos. */
    protected String mensajeSinDatos() {
        return "No hay datos para este reporte.";
    }
}
