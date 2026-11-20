/*
 * ============================================================
 * Archivo: Lote.java
 * Propósito: entidad de la tabla lotes (trazabilidad de ingresos).
 * Conceptos clave: fechas como String "AAAA-MM-DD" comparadas con compareTo
 *            (S19Demo03), valores opcionales representados con null.
 * Justificación: cada ingreso de mercancía queda identificado con un
 *            código único, su proveedor y su fecha, para poder rastrear
 *            el origen de cada pieza y alertar caducidades.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class Lote {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final int id;
    private final String codigo;            // único, ej: LOT-2026-001
    private final int repuestoId;
    private final int proveedorId;
    // Visto en clase: funciones de MySQL en el SQL (NOW, CURDATE, DATE_ADD, LAST_INSERT_ID) -> son SQL de MySQL, no Java; en Java solo se vio LocalDate.of (S12Demo02)
    private final String fechaIngreso;     // la asigna MySQL (CURDATE()) al insertar
    private final String fechaCaducidad;   // null si el repuesto no caduca
    private final int cantidad;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public Lote(int id, String codigo, int repuestoId, int proveedorId, String fechaIngreso,
                String fechaCaducidad, int cantidad) {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        // Visto en clase: String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
        if (codigo == null || codigo.isBlank() || codigo.strip().length() > 30) {
            // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
            throw new DatoInvalidoException("Código de lote obligatorio de hasta 30 caracteres.");
        }
        if (repuestoId <= 0 || proveedorId <= 0 || cantidad <= 0) {
            throw new DatoInvalidoException("Repuesto, proveedor y cantidad positiva son obligatorios.");
        }
        // Visto en clase: Predicate<T> / Comparator<T> / compareTo -> S19Demo03ReglasOrdenMain (Sesión 19)
        if (fechaCaducidad != null && fechaIngreso != null && fechaCaducidad.compareTo(fechaIngreso) < 0) {
            throw new DatoInvalidoException("La caducidad no puede ser anterior al ingreso.");
        }
        this.id = id;
        // Visto en clase: toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
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
