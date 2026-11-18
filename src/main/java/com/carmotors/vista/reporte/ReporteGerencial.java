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
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - clase abstracta y métodos abstractos -> S07Demo03 Poliza (Sesión 07)
 *   - modificador protected -> S07Demo00 Usuario.describirIdentidad (Sesión 07)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
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
