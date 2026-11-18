/*
 * ============================================================
 * Archivo: ConexionFactory.java
 * Propósito: punto único para obtener conexiones JDBC a MySQL.
 * Conceptos clave: PATRÓN SINGLETON (S12Demo04 ConfiguracionSistema):
 *            constructor privado + getInstancia() static synchronized
 *            (monitor de clase, S16Demo00 Numerador) con inicialización
 *            perezosa; constantes de conexión y DriverManager.getConnection
 *            como en ConexionFactory de S18Demo01; isValid (S18Demo00).
 * Justificación: toda la aplicación comparte UNA fábrica de conexiones.
 *            Igual que en S18Demo01, no guarda una conexión global: cada DAO
 *            abre y cierra la suya con try-with-resources.
 *            Las credenciales van en constantes (forma vista en clase). Cada
 *            integrante ajusta DB_USER y DB_PASSWORD a su MySQL local.
 *            NOTA: en un proyecto real no se dejan claves en el código.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - métodos y miembros static -> S05Demo02ConversorTemperatura (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - synchronized (método static) -> S16Demo00 Numerador (Sesión 16)
 *   - PATRÓN SINGLETON -> S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - ConexionFactory con DriverManager e isValid -> S18Demo01 ConexionFactory y S18Demo00UsuariosMain (Sesión 18)
 * ============================================================
 */
package com.carmotors.config;

import com.carmotors.excepcion.ConfiguracionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionFactory {
    // ---- Ajustar a la instalación local de MySQL ----
    private static final String URL = "jdbc:mysql://localhost:3306/carmotors";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Campus2023!";

    // Única instancia; null hasta el primer uso (inicialización perezosa).
    private static ConexionFactory instancia;

    // Privado: nadie fuera de la clase puede hacer "new ConexionFactory()".
    private ConexionFactory() {
        if (URL == null || URL.isBlank() || DB_USER == null || DB_USER.isBlank() || DB_PASSWORD == null) {
            throw new ConfiguracionException("Configura URL, DB_USER y DB_PASSWORD en ConexionFactory.");
        }
    }

    /** Devuelve siempre el mismo objeto; synchronized evita crear dos instancias desde dos hilos. */
    public static synchronized ConexionFactory getInstancia() {
        if (instancia == null) {
            instancia = new ConexionFactory();
        }
        return instancia;
    }

    /** Abre una conexión NUEVA; quien la pide debe cerrarla (try-with-resources). */
    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }

    /** Verifica la conexión al iniciar la aplicación. */
    public boolean probarConexion() throws SQLException {
        try (Connection conexion = abrir()) {
            return conexion.isValid(5);
        }
    }
}
