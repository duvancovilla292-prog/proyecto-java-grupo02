/*
 * ============================================================
 * Archivo: Cliente.java
 * Propósito: entidad de la tabla clientes.
 * Conceptos clave: encapsulamiento (private final + getters, sesión 06),
 *            validación en el constructor (Usuario de S18Demo01),
 *            strip()/isBlank() (sesión 04).
 * Justificación: objeto inmutable: una vez creado siempre es válido.
 *            id = 0 significa "aún no guardado" (MySQL asigna el id).
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class Cliente {
    private final int id;
    private final String nombre;
    private final String documento;  // documento_identidad (único)
    private final String telefono;   // opcional
    private final String email;      // único y obligatorio

    public Cliente(int id, String nombre, String documento, String telefono, String email) {
        if (id < 0) {
            throw new DatoInvalidoException("El id no puede ser negativo.");
        }
        if (nombre == null || nombre.isBlank() || nombre.strip().length() > 255) {
            throw new DatoInvalidoException("Nombre obligatorio de hasta 255 caracteres.");
        }
        if (documento == null || documento.isBlank() || documento.strip().length() > 20) {
            throw new DatoInvalidoException("Documento obligatorio de hasta 20 caracteres.");
        }
        if (email == null || !email.contains("@") || email.strip().length() > 100) {
            throw new DatoInvalidoException("Email obligatorio, con @ y hasta 100 caracteres.");
        }
        this.id = id;
        this.nombre = nombre.strip();
        this.documento = documento.strip();
        this.telefono = telefono == null ? "" : telefono.strip();
        this.email = email.strip().toLowerCase();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }
}
