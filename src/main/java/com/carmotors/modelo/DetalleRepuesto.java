/*
 * ============================================================
 * Archivo: DetalleRepuesto.java
 * Propósito: fila de la tabla de unión servicios_repuestos
 *            (repuesto usado en una orden, cantidad, precio y estado).
 * Conceptos clave: objeto de valor, enum EstadoReserva, cálculo derivado.
 * Justificación: se guarda el precio de venta del MOMENTO de la
 *            asignación para que la factura no cambie si luego se
 *            actualiza el precio del repuesto.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class DetalleRepuesto {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final int servicioId;
    private final int repuestoId;
    private final String nombreRepuesto;  // Solo lectura, llenado por JOIN
    private final int cantidad;
    private final int precioUnitario;    // precio_unitario_venta
    private final EstadoReserva estado;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public DetalleRepuesto(int servicioId, int repuestoId, String nombreRepuesto, int cantidad,
                           int precioUnitario, EstadoReserva estado) {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (servicioId <= 0 || repuestoId <= 0 || cantidad <= 0 || precioUnitario < 0 || estado == null) {
            // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
            throw new DatoInvalidoException("Detalle de repuesto inválido.");
        }
        this.servicioId = servicioId;
        this.repuestoId = repuestoId;
        // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
        this.nombreRepuesto = nombreRepuesto == null ? "" : nombreRepuesto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.estado = estado;
    }

    /** cantidad x precio unitario. */
    public int subtotal() {
        return cantidad * precioUnitario;
    }

    public int getServicioId() {
        return servicioId;
    }

    public int getRepuestoId() {
        return repuestoId;
    }

    public String getNombreRepuesto() {
        return nombreRepuesto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getPrecioUnitario() {
        return precioUnitario;
    }

    public EstadoReserva getEstado() {
        return estado;
    }
}
