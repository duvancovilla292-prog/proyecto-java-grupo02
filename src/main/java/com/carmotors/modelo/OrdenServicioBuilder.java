/*
 * ============================================================
 * Archivo: OrdenServicioBuilder.java
 * Propósito: construir objetos OrdenServicio paso a paso.
 * Conceptos clave: PATRÓN BUILDER (S12Demo02 SolicitudBuilder): métodos
 *            encadenados que devuelven "this" y validación en build().
 * Justificación: se usa en dos lugares con necesidades distintas:
 *            - El servicio crea órdenes NUEVAS (sin id, estado Pendiente,
 *              la fecha de recepción la asigna MySQL con NOW()).
 *            - El DAO RECONSTRUYE órdenes leídas de la BD (con id, estado
 *              y fecha de entrega).
 *            Un único Builder cubre ambos casos sin multiplicar constructores.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

// Visto en clase: PATRÓN BUILDER -> S12Demo02 SolicitudBuilder (Sesión 12)
public class OrdenServicioBuilder {
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private int id;                                          // 0 = nueva
    private int vehiculoId;
    private TipoMantenimiento tipoMantenimiento;
    private String fechaRecepcion;                            // null en órdenes nuevas (la pone MySQL)
    private String fechaEntrega;                              // opcional
    private String descripcionProblema = "";
    private int costoManoObra;
    private EstadoServicio estado = EstadoServicio.PENDIENTE;  // toda orden nace Pendiente

    public OrdenServicioBuilder id(int valor) {
        id = valor;
        return this;
    }

    public OrdenServicioBuilder vehiculoId(int valor) {
        vehiculoId = valor;
        return this;
    }

    public OrdenServicioBuilder tipoMantenimiento(TipoMantenimiento valor) {
        tipoMantenimiento = valor;
        return this;
    }

    public OrdenServicioBuilder fechaRecepcion(String valor) {
        fechaRecepcion = valor;
        return this;
    }

    public OrdenServicioBuilder fechaEntrega(String valor) {
        fechaEntrega = valor;
        return this;
    }

    public OrdenServicioBuilder descripcionProblema(String valor) {
        // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
        // Visto en clase: String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
        descripcionProblema = valor == null ? "" : valor.strip();
        return this;
    }

    public OrdenServicioBuilder costoManoObra(int valor) {
        costoManoObra = valor;
        return this;
    }

    public OrdenServicioBuilder estado(EstadoServicio valor) {
        estado = valor;
        return this;
    }

    /** Valida los datos acumulados y crea la orden inmutable. */
    public OrdenServicio build() {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (id < 0 || vehiculoId <= 0) {
            // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
            throw new DatoInvalidoException("La orden requiere un vehículo válido.");
        }
        if (tipoMantenimiento == null || estado == null) {
            throw new DatoInvalidoException("Tipo de mantenimiento y estado son obligatorios.");
        }
        if (descripcionProblema.isBlank()) {
            throw new DatoInvalidoException("La descripción del problema es obligatoria.");
        }
        if (costoManoObra < 0) {
            throw new DatoInvalidoException("El costo de mano de obra no puede ser negativo.");
        }
        // Las fechas en formato AAAA-MM-DD HH:MM:SS se comparan con compareTo (orden alfabético = cronológico).
        // Visto en clase: Predicate<T> / Comparator<T> / compareTo -> S19Demo03ReglasOrdenMain (Sesión 19)
        if (fechaEntrega != null && fechaRecepcion != null && fechaEntrega.compareTo(fechaRecepcion) < 0) {
            throw new DatoInvalidoException("La entrega no puede ser anterior a la recepción.");
        }
        return new OrdenServicio(id, vehiculoId, tipoMantenimiento, fechaRecepcion, fechaEntrega,
                descripcionProblema, costoManoObra, estado);
    }
}
