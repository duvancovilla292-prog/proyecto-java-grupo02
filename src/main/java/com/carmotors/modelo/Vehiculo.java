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
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - métodos y miembros static -> S05Demo02ConversorTemperatura (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
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
