/*
 * ============================================================
 * Archivo: Proveedor.java
 * Propósito: entidad de la tabla proveedores (datos de contacto).
 * Conceptos clave: encapsulamiento, validación en constructor.
 * Justificación: entidad inmutable; los campos opcionales se guardan
 *            como texto vacío para evitar null en la vista.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class Proveedor {
    private final int id;
    private final String nombre;
    private final String contacto;  // Persona de contacto
    private final String telefono;
    private final String email;

    public Proveedor(int id, String nombre, String contacto, String telefono, String email) {
        if (id < 0) {
            throw new DatoInvalidoException("El id no puede ser negativo.");
        }
        if (nombre == null || nombre.isBlank() || nombre.strip().length() > 255) {
            throw new DatoInvalidoException("Nombre del proveedor obligatorio.");
        }
        this.id = id;
        this.nombre = nombre.strip();
        this.contacto = contacto == null ? "" : contacto.strip();
        this.telefono = telefono == null ? "" : telefono.strip();
        this.email = email == null ? "" : email.strip();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContacto() {
        return contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }
}
