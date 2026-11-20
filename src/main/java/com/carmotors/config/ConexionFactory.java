/*
 * ============================================================
 * Archivo: ConexionFactory.java
 * Propósito: punto único para obtener conexiones JDBC a MySQL.
 * Conceptos clave: PATRÓN SINGLETON como en S12Demo04: constante
 *            private static final INSTANCIA + constructor privado +
 *            getInstancia() que la devuelve; constantes de conexión y DriverManager.getConnection
 *            como en ConexionFactory de S18Demo01; isValid (S18Demo00).
 * Justificación: toda la aplicación comparte UNA fábrica de conexiones.
 *            Igual que en S18Demo01, no guarda una conexión global: cada DAO
 *            abre y cierra la suya con try-with-resources.
 *            Las credenciales van en constantes (forma vista en clase). Cada
 *            integrante ajusta DB_USER y DB_PASSWORD a su MySQL local.
 *            NOTA: en un proyecto real no se dejan claves en el código.
 *            La configuración del negocio (IVA y carpeta de facturas) vive
 *            en ConfiguracionSistema: esta clase solo entrega conexiones
 *            (Principio de Responsabilidad Única).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.config;

import com.carmotors.excepcion.ConfiguracionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionFactory {
    // ---- Ajustar a la instalación local de MySQL ----
    // Visto en clase: métodos y miembros static -> S05Demo02ConversorTemperatura (Sesión 05)
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    private static final String URL = "jdbc:mysql://localhost:3306/carmotors";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Campus2023!";

    // Única instancia: se crea una sola vez y toda la aplicación la comparte.
    // Visto en clase: PATRÓN SINGLETON -> S12Demo04 ConfiguracionSistema (Sesión 12)
    private static final ConexionFactory INSTANCIA = new ConexionFactory();

    // Privado: nadie fuera de la clase puede hacer "new ConexionFactory()".
    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    private ConexionFactory() {
    }

    /** Devuelve siempre el mismo objeto. */
    public static ConexionFactory getInstancia() {
        return INSTANCIA;
    }

    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    /** Abre una conexión NUEVA; quien la pide debe cerrarla (try-with-resources). */
    public Connection abrir() throws SQLException {
        // Visto en clase: ConexionFactory con DriverManager e isValid -> S18Demo01 ConexionFactory y S18Demo00UsuariosMain (Sesión 18)
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }

    /** Verifica la configuración y la conexión al iniciar la aplicación. */
    public boolean probarConexion() throws SQLException {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        // Visto en clase: String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
        if (URL == null || URL.isBlank() || DB_USER == null || DB_USER.isBlank() || DB_PASSWORD == null) {
            throw new ConfiguracionException("Configura URL, DB_USER y DB_PASSWORD en ConexionFactory.");
        }
        try (Connection conexion = abrir()) {
            return conexion.isValid(5);
        }
    }
}
