/*
 * ============================================================
 * Archivo: ProductoCatalogo.java
 * Propósito: fila de proveedor_catalogo: qué repuesto ofrece un
 *            proveedor y a qué costo.
 * Conceptos clave: objeto de valor inmutable; campos de solo lectura
 *            llenados por JOIN para mostrar nombres.
 * Justificación: permite comparar con Streams el costo del mismo
 *            repuesto entre varios proveedores.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class ProductoCatalogo {
    private final int proveedorId;
    private final String nombreProveedor; // Solo lectura (JOIN)
    private final int repuestoId;
    private final String nombreRepuesto;  // Solo lectura (JOIN)
    private final int costo;             // pesos

    public ProductoCatalogo(int proveedorId, String nombreProveedor, int repuestoId,
                            String nombreRepuesto, int costo) {
        if (proveedorId <= 0 || repuestoId <= 0 || costo <= 0) {
            throw new DatoInvalidoException("Proveedor, repuesto y costo positivo son obligatorios.");
        }
        this.proveedorId = proveedorId;
        this.nombreProveedor = nombreProveedor == null ? "" : nombreProveedor;
        this.repuestoId = repuestoId;
        this.nombreRepuesto = nombreRepuesto == null ? "" : nombreRepuesto;
        this.costo = costo;
    }

    public int getProveedorId() {
        return proveedorId;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public int getRepuestoId() {
        return repuestoId;
    }

    public String getNombreRepuesto() {
        return nombreRepuesto;
    }

    public int getCosto() {
        return costo;
    }
}
