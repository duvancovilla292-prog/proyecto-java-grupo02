/*
 * ============================================================
 * Archivo: TecnicoDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de TecnicoDAO.
 * Conceptos clave: PATRÓN DAO, PreparedStatement con parámetros "?"
 *            (evita inyección SQL), try-with-resources que cierra
 *            Connection/PreparedStatement/ResultSet (S18Demo01 UsuarioDAO),
 *            conversión ResultSet -> objeto del modelo dentro del DAO.
 * Justificación: es la ÚNICA clase que conoce el SQL de su tabla; si cambia
 *            el motor o la consulta, solo cambia este archivo (SRP/OCP).
 *            Recibe ConexionFactory por constructor (inyección de dependencias)
 *            y abre/cierra una conexión por operación, como ConexionFactory.
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.TecnicoDAO;
import com.carmotors.modelo.Tecnico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TecnicoDAOJdbc implements TecnicoDAO {
    private static final String COLUMNAS = "SELECT id, nombre, especialidad, activo FROM tecnicos";

    private final ConexionFactory conexiones;

    public TecnicoDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int insertar(Tecnico tecnico) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "INSERT INTO tecnicos (nombre, especialidad, activo) VALUES (?, ?, ?)")) {
            sentencia.setString(1, tecnico.getNombre());
            sentencia.setString(2, tecnico.getEspecialidad());
            sentencia.setInt(3, tecnico.isActivo() ? 1 : 0);  // BOOLEAN de MySQL = 1/0
            sentencia.executeUpdate();
            return JdbcUtil.ultimoIdGenerado(conexion);
        }
    }

    @Override
    public int actualizarActivo(int id, boolean activo) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("UPDATE tecnicos SET activo = ? WHERE id = ?")) {
            sentencia.setInt(1, activo ? 1 : 0);
            sentencia.setInt(2, id);
            return sentencia.executeUpdate();
        }
    }

    @Override
    public Tecnico buscarPorId(int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE id = ?")) {
            sentencia.setInt(1, id);
            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next() ? mapear(filas) : null;
            }
        }
    }

    @Override
    public List<Tecnico> listar() throws SQLException {
        List<Tecnico> tecnicos = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " ORDER BY nombre");
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                tecnicos.add(mapear(filas));
            }
        }
        return tecnicos;
    }

    /** Reutilizado por AsignacionTecnicoDAOJdbc (mismo paquete) para mapear el JOIN. */
    static Tecnico mapear(ResultSet fila) throws SQLException {
        return new Tecnico(fila.getInt("id"), fila.getString("nombre"), fila.getString("especialidad"),
                fila.getInt("activo") == 1);
    }

    @Override
    public int actualizar(Tecnico tecnico) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE tecnicos SET nombre = ?, especialidad = ?, activo = ? WHERE id = ?")) {
            sentencia.setString(1, tecnico.getNombre());
            sentencia.setString(2, tecnico.getEspecialidad());
            sentencia.setInt(3, tecnico.isActivo() ? 1 : 0);  // BOOLEAN de MySQL = 1/0
            sentencia.setInt(4, tecnico.getId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM tecnicos WHERE id = ?", id);
    }
}
