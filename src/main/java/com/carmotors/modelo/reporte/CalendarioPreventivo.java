/*
 * ============================================================
 * Archivo: CalendarioPreventivo.java
 * Propósito: fechas del mantenimiento preventivo de un vehículo, calculadas
 *            por MySQL: último preventivo terminado, próximo sugerido, hoy y
 *            fecha límite de aviso, junto con el vehículo, su dueño y si
 *            tiene un preventivo abierto.
 * Conceptos clave: objeto de valor inmutable (S06), fechas como String
 *            "AAAA-MM-DD" comparadas con compareTo (S19Demo03).
 * Justificación: en los demos solo se vio LocalDate.of; sumar meses o días
 *            a una fecha y obtener la fecha actual lo resuelve MySQL
 *            (DATE_ADD, CURDATE) en la consulta del DAO. La REGLA de negocio
 *            (¿se notifica?, ¿está vencido?) queda aquí, en Java.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - Predicate<T> / Comparator<T> / compareTo -> S19Demo03ReglasOrdenMain (Sesión 19)
 * ============================================================
 */
package com.carmotors.modelo.reporte;

import com.carmotors.modelo.Cliente;
import com.carmotors.modelo.Vehiculo;

public class CalendarioPreventivo {
    private final Cliente cliente;
    private final Vehiculo vehiculo;
    private final String ultimo;         // fecha del último preventivo terminado
    private final String proximo;        // último + N meses
    private final String hoy;            // fecha actual del servidor
    private final String limite;         // hoy + días de anticipación
    private final boolean ordenAbierta;  // ya tiene un preventivo pendiente o en proceso

    public CalendarioPreventivo(Cliente cliente, Vehiculo vehiculo, String ultimo, String proximo, String hoy,
                                String limite, boolean ordenAbierta) {
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.ultimo = ultimo;
        this.proximo = proximo;
        this.hoy = hoy;
        this.limite = limite;
        this.ordenAbierta = ordenAbierta;
    }

    /** Se notifica si no hay un preventivo abierto y la fecha sugerida llega antes del límite de aviso. */
    public boolean debeNotificarse() {
        return !ordenAbierta && proximo.compareTo(limite) <= 0;
    }

    /** Vencido: la fecha sugerida es anterior a hoy. */
    public boolean estaVencido() {
        return proximo.compareTo(hoy) < 0;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public String getUltimo() {
        return ultimo;
    }

    public String getProximo() {
        return proximo;
    }

    public boolean isOrdenAbierta() {
        return ordenAbierta;
    }
}
