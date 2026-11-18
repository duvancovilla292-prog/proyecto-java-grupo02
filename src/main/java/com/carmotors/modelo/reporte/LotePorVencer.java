/*
 * ============================================================
 * Archivo: LotePorVencer.java
 * Propósito: fila del reporte "Alertas de inventario": un lote que caduca
 *            dentro de los próximos N días (o ya caducó).
 * Conceptos clave: objeto de valor inmutable (private final + getters,
 *            sesión 06), fechas como String "AAAA-MM-DD".
 * Justificación: el nombre del repuesto llega resuelto por un JOIN en
 *            ReporteDAO, así la vista no tiene que buscarlo en el catálogo.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 * ============================================================
 */
package com.carmotors.modelo.reporte;

public class LotePorVencer {
    private final String codigo;
    private final String nombreRepuesto;
    private final String fechaCaducidad;
    private final int cantidad;

    public LotePorVencer(String codigo, String nombreRepuesto, String fechaCaducidad, int cantidad) {
        this.codigo = codigo;
        this.nombreRepuesto = nombreRepuesto;
        this.fechaCaducidad = fechaCaducidad;
        this.cantidad = cantidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombreRepuesto() {
        return nombreRepuesto;
    }

    public String getFechaCaducidad() {
        return fechaCaducidad;
    }

    public int getCantidad() {
        return cantidad;
    }
}
