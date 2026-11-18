/*
 * ============================================================
 * Archivo: JdbcUtil.java
 * Propósito: utilidades JDBC compartidas por todos los DAO.
 * Conceptos clave: clase utilitaria final con constructor privado
 *            (como EjecutorPrueba de S16Demo02), SELECT LAST_INSERT_ID().
 * Justificación: evita repetir en 13 DAO el mismo código para recuperar
 *            el id autogenerado y para borrar por id (principio DRY). LAST_INSERT_ID() es por
 *            conexión, por eso se consulta con la MISMA conexión del INSERT.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - métodos y miembros static -> S05Demo02ConversorTemperatura (Sesión 05)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - JDBC: Connection, PreparedStatement, ResultSet, try-with-resources -> S18Demo01 UsuarioDAO (Sesión 18)
 *   - funciones de MySQL en el SQL (NOW, CURDATE, DATE_ADD, LAST_INSERT_ID) -> son SQL de MySQL, no Java; en Java solo se vio LocalDate.of (S12Demo02)
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class JdbcUtil {
    private JdbcUtil() {
    }

    /** Devuelve el último id AUTO_INCREMENT generado en esta conexión. */
    static int ultimoIdGenerado(Connection conexion) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement("SELECT LAST_INSERT_ID()");
             ResultSet resultado = sentencia.executeQuery()) {
            if (!resultado.next()) {
                throw new SQLException("MySQL no devolvió el id generado.");
            }
            return resultado.getInt(1);
        }
    }

    /** Ejecuta un DELETE con un único parámetro id; devuelve filas afectadas. */
    static int eliminarPorId(ConexionFactory conexiones, String sql, int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            return sentencia.executeUpdate();
        }
    }
}
