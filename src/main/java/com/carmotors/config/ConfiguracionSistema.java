/*
 * ============================================================
 * Archivo: ConfiguracionSistema.java
 * Propósito: guardar en un solo lugar la configuración del negocio:
 *            porcentaje de IVA y carpeta donde se guardan las facturas.
 * Conceptos clave: constantes public static final (se usan sin crear
 *            objetos: ConfiguracionSistema.IVA_PORCENTAJE).
 * Justificación: antes estos valores vivían en ConexionFactory, que así
 *            tenía dos responsabilidades (conexiones a MySQL + reglas del
 *            negocio). Se separan para cumplir el Principio de
 *            Responsabilidad Única (SRP): cambiar el IVA o la carpeta de
 *            facturas ya no toca la clase de la base de datos.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.config;

import java.nio.file.Path;

public final class ConfiguracionSistema {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    /** Porcentaje de IVA que aplica la facturación (IVA Colombia). */
    public static final int IVA_PORCENTAJE = 19;

    // Visto en clase: Path.of -> S17Demo00RutasMain (Sesión 17)
    /** Carpeta donde se guardan las facturas en texto plano (relativa a la raíz del proyecto). */
    public static final Path CARPETA_FACTURAS = Path.of("facturas");
}
