/*
 * ============================================================
 * Archivo: LoteDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de LoteDAO.
 * Conceptos clave: PATRÓN DAO, PreparedStatement con parámetros "?"
 *            (evita inyección SQL), try-with-resources que cierra
 *            Connection/PreparedStatement/ResultSet (S18Demo01 UsuarioDAO),
 *            conversión ResultSet -> objeto del modelo dentro del DAO.
 * Justificación: es la ÚNICA clase que conoce el SQL de su tabla; si cambia
 *            el motor o la consulta, solo cambia este archivo (SRP/OCP).
 *            Recibe ConexionFactory por constructor (inyección de dependencias)
 *            y abre/cierra una conexión por operación, como ConexionFactory.
 *            Fechas con setString/getString; la fecha de ingreso la pone MySQL
 *            (CURDATE()) y "por vencer" se calcula con DATE_ADD en el SQL.
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.LoteDAO;
import com.carmotors.modelo.Lote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoteDAOJdbc implements LoteDAO {
    private static final String COLUMNAS =
            "SELECT id, codigo, repuesto_id, proveedor_id, fecha_ingreso, fecha_caducidad, cantidad FROM lotes";

    private final ConexionFactory conexiones;

    public LoteDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int insertar(Lote lote) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("INSERT INTO lotes (codigo, repuesto_id, "
                     + "proveedor_id, fecha_ingreso, fecha_caducidad, cantidad) VALUES (?, ?, CURDATE(), ?, ?)")) {
            sentencia.setString(1, lote.getCodigo());
            sentencia.setInt(2, lote.getRepuestoId());
            sentencia.setInt(3, lote.getProveedorId());
            sentencia.setString(4, lote.getFechaCaducidad()); // null -> NULL en MySQL
            sentencia.setInt(5, lote.getCantidad());
            sentencia.executeUpdate();
            return JdbcUtil.ultimoIdGenerado(conexion);
        }
    }

    @Override
    public Lote buscarPorCodigo(String codigo) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE codigo = ?")) {
            sentencia.setString(1, codigo.strip().toUpperCase());
            List<Lote> encontrados = todos(sentencia);
            return encontrados.isEmpty() ? null : encontrados.get(0);
        }
    }

    @Override
    public List<Lote> listar() throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " ORDER BY fecha_ingreso, id")) {
            return todos(sentencia);
        }
    }

    @Override
    public List<Lote> listarPorVencer(int dias) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE fecha_caducidad IS NOT NULL "
                     + "AND fecha_caducidad <= DATE_ADD(CURDATE(), INTERVAL ? DAY) ORDER BY fecha_caducidad")) {
            sentencia.setInt(1, dias);
            return todos(sentencia);
        }
    }

    @Override
    public List<Lote> listarPorRepuesto(int repuestoId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE repuesto_id = ? ORDER BY fecha_ingreso")) {
            sentencia.setInt(1, repuestoId);
            return todos(sentencia);
        }
    }

    private List<Lote> todos(PreparedStatement sentencia) throws SQLException {
        List<Lote> lotes = new ArrayList<>();
        try (ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                lotes.add(new Lote(filas.getInt("id"), filas.getString("codigo"), filas.getInt("repuesto_id"),
                        filas.getInt("proveedor_id"), filas.getString("fecha_ingreso"),
                        filas.getString("fecha_caducidad"), filas.getInt("cantidad")));
            }
        }
        return lotes;
    }

    @Override
    public int actualizar(Lote lote) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE lotes SET proveedor_id = ?, fecha_caducidad = ? WHERE id = ?")) {
            sentencia.setInt(1, lote.getProveedorId());
            sentencia.setString(2, lote.getFechaCaducidad());
            sentencia.setInt(3, lote.getId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM lotes WHERE id = ?", id);
    }
}
