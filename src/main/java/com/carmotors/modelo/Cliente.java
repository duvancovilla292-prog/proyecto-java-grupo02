/*
 * ============================================================
 * Archivo: Cliente.java
 * Propósito: entidad de la tabla clientes.
 * Conceptos clave: encapsulamiento (private final + getters, sesión 06),
 *            validación en el constructor (Usuario de S18Demo01),
 *            strip()/isBlank() (sesión 04).
 * Justificación: objeto inmutable: una vez creado siempre es válido.
 *            id = 0 significa "aún no guardado" (MySQL asigna el id).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.modelo;

import com.carmotors.excepcion.DatoInvalidoException;

public class Cliente {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final int id;
    private final String nombre;
    private final String documento;  // documento_identidad (único)
    private final String telefono;   // opcional
    private final String email;      // único y obligatorio

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public Cliente(int id, String nombre, String documento, String telefono, String email) {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (id < 0) {
            // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
            throw new DatoInvalidoException("El id no puede ser negativo.");
        }
        // Visto en clase: String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
        if (nombre == null || nombre.isBlank() || nombre.strip().length() > 255) {
            throw new DatoInvalidoException("Nombre obligatorio de hasta 255 caracteres.");
        }
        if (documento == null || documento.isBlank() || documento.strip().length() > 20) {
            throw new DatoInvalidoException("Documento obligatorio de hasta 20 caracteres.");
        }
        // Visto en clase: toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
        if (email == null || !email.contains("@") || email.strip().length() > 100) {
            throw new DatoInvalidoException("Email obligatorio, con @ y hasta 100 caracteres.");
        }
        this.id = id;
        this.nombre = nombre.strip();
        this.documento = documento.strip();
        // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
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
