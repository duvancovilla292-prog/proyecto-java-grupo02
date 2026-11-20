/*
 * ============================================================
 * Archivo: RecordatorioMantenimiento.java
 * Propósito: resultado calculado (no es tabla) que indica a qué cliente
 *            notificar sobre el próximo mantenimiento preventivo de un vehículo.
 * Conceptos clave: objeto de valor inmutable, fechas como String "AAAA-MM-DD".
 * Justificación: separa el CÁLCULO (capa de servicio) de la PRESENTACIÓN
 *            (capa de vista), cumpliendo SRP.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo.reporte;

import com.carmotors.modelo.Cliente;
import com.carmotors.modelo.Vehiculo;

public class RecordatorioMantenimiento {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final Cliente cliente;
    private final Vehiculo vehiculo;
    private final String ultimoPreventivo;
    private final String proximoPreventivo;
    private final boolean vencido;  // true: la fecha sugerida ya pasó

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public RecordatorioMantenimiento(Cliente cliente, Vehiculo vehiculo, String ultimoPreventivo,
                                     String proximoPreventivo, boolean vencido) {
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.ultimoPreventivo = ultimoPreventivo;
        this.proximoPreventivo = proximoPreventivo;
        this.vencido = vencido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public String getUltimoPreventivo() {
        return ultimoPreventivo;
    }

    public String getProximoPreventivo() {
        return proximoPreventivo;
    }

    public boolean isVencido() {
        return vencido;
    }
}
