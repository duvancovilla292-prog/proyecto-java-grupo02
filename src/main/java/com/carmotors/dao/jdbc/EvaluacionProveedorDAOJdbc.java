/*
 * ============================================================
 * Archivo: EvaluacionProveedorDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de EvaluacionProveedorDAO.
 * Conceptos clave: PATRÓN DAO, PreparedStatement con parámetros "?"
 *            (evita inyección SQL), try-with-resources que cierra
 *            Connection/PreparedStatement/ResultSet (S18Demo01 UsuarioDAO),
 *            conversión ResultSet -> objeto del modelo dentro del DAO.
 * Justificación: es la ÚNICA clase que conoce el SQL de su tabla; si cambia
 *            el motor o la consulta, solo cambia este archivo (SRP/OCP).
 *            Recibe ConexionFactory por constructor (inyección de dependencias)
 *            y abre/cierra una conexión por operación, como ConexionFactory.
 *            La fecha se lee con getString y al insertar la pone MySQL (CURDATE()).
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.EvaluacionProveedorDAO;
import com.carmotors.modelo.EvaluacionProveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvaluacionProveedorDAOJdbc implements EvaluacionProveedorDAO {
    private static final String COLUMNAS =
            "SELECT id, proveedor_id, calidad, puntualidad, fecha, comentario FROM evaluaciones_proveedor";

    private final ConexionFactory conexiones;

    public EvaluacionProveedorDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int insertar(EvaluacionProveedor evaluacion) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("INSERT INTO evaluaciones_proveedor "
                     + "(proveedor_id, calidad, puntualidad, fecha, comentario) VALUES (?, ?, ?, CURDATE(), ?)")) {
            sentencia.setInt(1, evaluacion.getProveedorId());
            sentencia.setInt(2, evaluacion.getCalidad());
            sentencia.setInt(3, evaluacion.getPuntualidad());
            sentencia.setString(4, evaluacion.getComentario());
            sentencia.executeUpdate();
            return JdbcUtil.ultimoIdGenerado(conexion);
        }
    }

    @Override
    public List<EvaluacionProveedor> listar() throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " ORDER BY fecha")) {
            return todos(sentencia);
        }
    }

    @Override
    public List<EvaluacionProveedor> listarPorProveedor(int proveedorId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE proveedor_id = ? ORDER BY fecha")) {
            sentencia.setInt(1, proveedorId);
            return todos(sentencia);
        }
    }

    private List<EvaluacionProveedor> todos(PreparedStatement sentencia) throws SQLException {
        List<EvaluacionProveedor> evaluaciones = new ArrayList<>();
        try (ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                evaluaciones.add(new EvaluacionProveedor(filas.getInt("id"), filas.getInt("proveedor_id"),
                        filas.getInt("calidad"), filas.getInt("puntualidad"),
                        filas.getString("fecha"), filas.getString("comentario")));
            }
        }
        return evaluaciones;
    }

    @Override
    public EvaluacionProveedor buscarPorId(int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE id = ?")) {
            sentencia.setInt(1, id);
            List<EvaluacionProveedor> encontradas = todos(sentencia);
            return encontradas.isEmpty() ? null : encontradas.get(0);
        }
    }

    @Override
    public int actualizar(EvaluacionProveedor evaluacion) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE evaluaciones_proveedor SET calidad = ?, puntualidad = ?, comentario = ? WHERE id = ?")) {
            sentencia.setInt(1, evaluacion.getCalidad());
            sentencia.setInt(2, evaluacion.getPuntualidad());
            sentencia.setString(3, evaluacion.getComentario());
            sentencia.setInt(4, evaluacion.getId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM evaluaciones_proveedor WHERE id = ?", id);
    }
}
