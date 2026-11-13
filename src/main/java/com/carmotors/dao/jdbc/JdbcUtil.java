/*
 * ============================================================
 * Archivo: JdbcUtil.java
 * Propósito: utilidades JDBC compartidas por todos los DAO.
 * Conceptos clave: clase utilitaria final con constructor privado
 *            (como EjecutorPrueba de S16Demo02), SELECT LAST_INSERT_ID().
 * Justificación: evita repetir en 13 DAO el mismo código para recuperar
 *            el id autogenerado y para borrar por id (principio DRY). LAST_INSERT_ID() es por
 *            conexión, por eso se consulta con la MISMA conexión del INSERT.
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
