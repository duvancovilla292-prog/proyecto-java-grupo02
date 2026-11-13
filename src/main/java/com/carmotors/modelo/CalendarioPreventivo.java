/*
 * ============================================================
 * Archivo: CalendarioPreventivo.java
 * Propósito: fechas del mantenimiento preventivo de un vehículo, calculadas
 *            por MySQL: último preventivo terminado, próximo sugerido, hoy y
 *            fecha límite de aviso.
 * Conceptos clave: objeto de valor inmutable (S06), fechas como String
 *            "AAAA-MM-DD" comparadas con compareTo (S19Demo03).
 * Justificación: en los demos solo se vio LocalDate.of; sumar meses o días
 *            a una fecha y obtener la fecha actual lo resuelve MySQL
 *            (DATE_ADD, CURDATE) en la consulta del DAO. La REGLA de negocio
 *            (¿se notifica?, ¿está vencido?) queda aquí, en Java.
 * ============================================================
 */
package com.carmotors.modelo;

public class CalendarioPreventivo {
    private final int vehiculoId;
    private final String ultimo;   // fecha del último preventivo terminado
    private final String proximo;  // último + N meses
    private final String hoy;      // fecha actual del servidor
    private final String limite;   // hoy + días de anticipación

    public CalendarioPreventivo(int vehiculoId, String ultimo, String proximo, String hoy, String limite) {
        this.vehiculoId = vehiculoId;
        this.ultimo = ultimo;
        this.proximo = proximo;
        this.hoy = hoy;
        this.limite = limite;
    }

    /** Se notifica si la fecha sugerida ya llegó o llega antes del límite de aviso. */
    public boolean debeNotificarse() {
        return proximo.compareTo(limite) <= 0;
    }

    /** Vencido: la fecha sugerida es anterior a hoy. */
    public boolean estaVencido() {
        return proximo.compareTo(hoy) < 0;
    }

    public int getVehiculoId() {
        return vehiculoId;
    }

    public String getUltimo() {
        return ultimo;
    }

    public String getProximo() {
        return proximo;
    }
}
