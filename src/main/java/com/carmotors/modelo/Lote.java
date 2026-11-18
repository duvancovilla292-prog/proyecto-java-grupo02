/*
 * ============================================================
 * Archivo: Lote.java
 * Propósito: entidad de la tabla lotes (trazabilidad de ingresos).
 * Conceptos clave: fechas como String "AAAA-MM-DD" comparadas con compareTo
 *            (S19Demo03), valores opcionales representados con null.
 * Justificación: cada ingreso de mercancía queda identificado con un
 *            código único, su proveedor y su fecha, para poder rastrear
 *            el origen de cada pieza y alertar caducidades.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - Predicate<T> / Comparator<T> / compareTo -> S19Demo03ReglasOrdenMain (Sesión 19)
 *   - funciones de MySQL en el SQL (NOW, CURDATE, DATE_ADD, LAST_INSERT_ID) -> son SQL de MySQL, no Java; en Java solo se vio LocalDate.of (S12Demo02)
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class Lote {
    private final int id;
    private final String codigo;            // único, ej: LOT-2026-001
    private final int repuestoId;
    private final int proveedorId;
    private final String fechaIngreso;     // la asigna MySQL (CURDATE()) al insertar
    private final String fechaCaducidad;   // null si el repuesto no caduca
    private final int cantidad;

    public Lote(int id, String codigo, int repuestoId, int proveedorId, String fechaIngreso,
                String fechaCaducidad, int cantidad) {
        if (codigo == null || codigo.isBlank() || codigo.strip().length() > 30) {
            throw new DatoInvalidoException("Código de lote obligatorio de hasta 30 caracteres.");
        }
        if (repuestoId <= 0 || proveedorId <= 0 || cantidad <= 0) {
            throw new DatoInvalidoException("Repuesto, proveedor y cantidad positiva son obligatorios.");
        }
        if (fechaCaducidad != null && fechaIngreso != null && fechaCaducidad.compareTo(fechaIngreso) < 0) {
            throw new DatoInvalidoException("La caducidad no puede ser anterior al ingreso.");
        }
        this.id = id;
        this.codigo = codigo.strip().toUpperCase();
        this.repuestoId = repuestoId;
        this.proveedorId = proveedorId;
        this.fechaIngreso = fechaIngreso;
        this.fechaCaducidad = fechaCaducidad;
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getRepuestoId() {
        return repuestoId;
    }

    public int getProveedorId() {
        return proveedorId;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public String getFechaCaducidad() {
        return fechaCaducidad;
    }

    public int getCantidad() {
        return cantidad;
    }
}
