/*
 * ============================================================
 * Archivo: Vehiculo.java
 * Propósito: entidad de la tabla vehiculos (pertenece a un cliente).
 * Conceptos clave: encapsulamiento, validación, normalización de texto
 *            con toUpperCase (sesión 04).
 * Justificación: la placa se guarda en mayúsculas para que "abc123" y
 *            "ABC123" sean el mismo vehículo (misma idea que normalizar
 *            correos en S10Demo00). La relación con el cliente se guarda
 *            por id, igual que la clave foránea de la tabla.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class Vehiculo {
    private final int id;
    private final int clienteId;  // FK -> clientes.id
    private final String placa;
    private final String marca;
    private final String modelo;

    public Vehiculo(int id, int clienteId, String placa, String marca, String modelo) {
        if (id < 0 || clienteId <= 0) {
            throw new DatoInvalidoException("Id de vehículo o cliente inválido.");
        }
        if (placa == null || placa.isBlank() || placa.strip().length() > 10) {
            throw new DatoInvalidoException("Placa obligatoria de hasta 10 caracteres.");
        }
        this.id = id;
        this.clienteId = clienteId;
        this.placa = normalizarPlaca(placa);
        this.marca = marca == null ? "" : marca.strip();
        this.modelo = modelo == null ? "" : modelo.strip();
    }

    /** Regla única de normalización, reutilizada por servicios al buscar por placa. */
    public static String normalizarPlaca(String placa) {
        return placa == null ? "" : placa.strip().toUpperCase();
    }

    public int getId() {
        return id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String descripcion() {
        return placa + " - " + marca + " " + modelo;
    }
}
