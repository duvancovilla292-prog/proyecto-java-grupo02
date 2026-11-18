/*
 * ============================================================
 * Archivo: ResumenTipoServicio.java
 * Propósito: resumen del reporte "Servicios más solicitados" para un tipo de mantenimiento.
 * Conceptos clave: objeto de valor inmutable (private final + getters,
 *            sesión 06), resultado calculado con Streams en la capa de servicio.
 * Justificación: el servicio CALCULA y devuelve datos; la vista decide cómo
 *            mostrarlos. Así la capa de servicio no contiene formato de
 *            presentación (SRP y separación MVC).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 * ============================================================
 */
package com.carmotors.modelo;

public class ResumenTipoServicio {
    private final TipoMantenimiento tipo;
    private final int solicitudes;     // órdenes registradas de este tipo
    private final int manoObra;        // mano de obra de las órdenes terminadas
    private final int ventaRepuestos;  // repuestos deducidos en esas órdenes

    public ResumenTipoServicio(TipoMantenimiento tipo, int solicitudes, int manoObra, int ventaRepuestos) {
        this.tipo = tipo;
        this.solicitudes = solicitudes;
        this.manoObra = manoObra;
        this.ventaRepuestos = ventaRepuestos;
    }

    /** Ingreso total generado por el tipo de servicio (rentabilidad). */
    public int ingresoTotal() {
        return manoObra + ventaRepuestos;
    }

    public TipoMantenimiento getTipo() {
        return tipo;
    }

    public int getSolicitudes() {
        return solicitudes;
    }

    public int getManoObra() {
        return manoObra;
    }

    public int getVentaRepuestos() {
        return ventaRepuestos;
    }
}
