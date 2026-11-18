/*
 * ============================================================
 * Archivo: Cliente.java
 * Propósito: entidad de la tabla clientes.
 * Conceptos clave: encapsulamiento (private final + getters, sesión 06),
 *            validación en el constructor (Usuario de S18Demo01),
 *            strip()/isBlank() (sesión 04).
 * Justificación: objeto inmutable: una vez creado siempre es válido.
 *            id = 0 significa "aún no guardado" (MySQL asigna el id).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
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
