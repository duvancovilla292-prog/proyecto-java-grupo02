/*
 * ============================================================
 * Archivo: TipoRepuestoDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de TipoRepuestoDAO.
 * Conceptos clave: PATRÓN DAO, PreparedStatement con parámetros "?"
 *            (evita inyección SQL), try-with-resources que cierra
 *            Connection/PreparedStatement/ResultSet (S18Demo01 UsuarioDAO),
 *            conversión ResultSet -> objeto del modelo dentro del DAO.
 * Justificación: es la ÚNICA clase que conoce el SQL de su tabla; si cambia
 *            el motor o la consulta, solo cambia este archivo (SRP/OCP).
 *            Recibe ConexionFactory por constructor (inyección de dependencias)
 *            y abre/cierra una conexión por operación, como ConexionFactory.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
 *   - interfaces e implements -> S07Demo04 Notificable (Sesión 07)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - JDBC: Connection, PreparedStatement, ResultSet, try-with-resources -> S18Demo01 UsuarioDAO (Sesión 18)
 *   - PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.TipoRepuestoDAO;
import com.carmotors.modelo.TipoRepuesto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoRepuestoDAOJdbc implements TipoRepuestoDAO {
    private final ConexionFactory conexiones;

    public TipoRepuestoDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public TipoRepuesto buscarPorId(int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("SELECT id, nombre FROM repuesto_tipos WHERE id = ?")) {
            sentencia.setInt(1, id);
            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next() ? new TipoRepuesto(filas.getInt("id"), filas.getString("nombre")) : null;
            }
        }
    }

    @Override
    public List<TipoRepuesto> listar() throws SQLException {
        List<TipoRepuesto> tipos = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("SELECT id, nombre FROM repuesto_tipos ORDER BY id");
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                tipos.add(new TipoRepuesto(filas.getInt("id"), filas.getString("nombre")));
            }
        }
        return tipos;
    }

    @Override
    public int insertar(TipoRepuesto tipo) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("INSERT INTO repuesto_tipos (nombre) VALUES (?)")) {
            sentencia.setString(1, tipo.getNombre());
            sentencia.executeUpdate();
            return JdbcUtil.ultimoIdGenerado(conexion);
        }
    }

    @Override
    public int actualizar(TipoRepuesto tipo) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("UPDATE repuesto_tipos SET nombre = ? WHERE id = ?")) {
            sentencia.setString(1, tipo.getNombre());
            sentencia.setInt(2, tipo.getId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM repuesto_tipos WHERE id = ?", id);
    }
}
