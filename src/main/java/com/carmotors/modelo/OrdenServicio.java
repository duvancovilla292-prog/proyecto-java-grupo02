/*
 * ============================================================
 * Archivo: OrdenServicio.java
 * Propósito: entidad de la tabla servicios (orden de servicio de un vehículo).
 * Conceptos clave: objeto inmutable construido SOLO por su Builder
 *            (constructor package-private, igual que Solicitud de S12Demo02).
 * Justificación: la orden tiene muchos campos, varios opcionales (id,
 *            fecha de entrega, estado inicial). El patrón Builder evita un
 *            constructor con 8 parámetros difícil de leer y centraliza la
 *            validación en build().
 * ============================================================
 */
package com.carmotors.modelo;

public final class OrdenServicio {
    private final int id;
    private final int vehiculoId;
    private final TipoMantenimiento tipoMantenimiento;
    private final String fechaRecepcion;  // "AAAA-MM-DD HH:MM:SS"; la asigna MySQL (NOW()) al insertar
    private final String fechaEntrega;    // null hasta que se entrega
    private final String descripcionProblema;
    private final int costoManoObra;          // pesos
    private final EstadoServicio estado;

    // Package-private: solo OrdenServicioBuilder (mismo paquete) puede crearla.
    OrdenServicio(int id, int vehiculoId, TipoMantenimiento tipoMantenimiento, String fechaRecepcion,
                  String fechaEntrega, String descripcionProblema, int costoManoObra,
                  EstadoServicio estado) {
        this.id = id;
        this.vehiculoId = vehiculoId;
        this.tipoMantenimiento = tipoMantenimiento;
        this.fechaRecepcion = fechaRecepcion;
        this.fechaEntrega = fechaEntrega;
        this.descripcionProblema = descripcionProblema;
        this.costoManoObra = costoManoObra;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public int getVehiculoId() {
        return vehiculoId;
    }

    public TipoMantenimiento getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public String getFechaRecepcion() {
        return fechaRecepcion;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public String getDescripcionProblema() {
        return descripcionProblema;
    }

    public int getCostoManoObra() {
        return costoManoObra;
    }

    public EstadoServicio getEstado() {
        return estado;
    }
}
