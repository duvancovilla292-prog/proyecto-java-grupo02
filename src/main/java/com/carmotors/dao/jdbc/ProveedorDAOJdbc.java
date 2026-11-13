/*
 * ============================================================
 * Archivo: ProveedorDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de ProveedorDAO.
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
import com.carmotors.dao.ProveedorDAO;
import com.carmotors.modelo.Proveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAOJdbc implements ProveedorDAO {
    private static final String COLUMNAS = "SELECT id, nombre, contacto, telefono, email FROM proveedores";

    private final ConexionFactory conexiones;

    public ProveedorDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int insertar(Proveedor proveedor) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "INSERT INTO proveedores (nombre, contacto, telefono, email) VALUES (?, ?, ?, ?)")) {
            sentencia.setString(1, proveedor.getNombre());
            sentencia.setString(2, proveedor.getContacto());
            sentencia.setString(3, proveedor.getTelefono());
            sentencia.setString(4, proveedor.getEmail());
            sentencia.executeUpdate();
            return JdbcUtil.ultimoIdGenerado(conexion);
        }
    }

    @Override
    public int actualizar(Proveedor proveedor) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE proveedores SET nombre = ?, contacto = ?, telefono = ?, email = ? WHERE id = ?")) {
            sentencia.setString(1, proveedor.getNombre());
            sentencia.setString(2, proveedor.getContacto());
            sentencia.setString(3, proveedor.getTelefono());
            sentencia.setString(4, proveedor.getEmail());
            sentencia.setInt(5, proveedor.getId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public Proveedor buscarPorId(int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE id = ?")) {
            sentencia.setInt(1, id);
            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next() ? mapear(filas) : null;
            }
        }
    }

    @Override
    public List<Proveedor> listar() throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " ORDER BY nombre");
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                proveedores.add(mapear(filas));
            }
        }
        return proveedores;
    }

    private Proveedor mapear(ResultSet fila) throws SQLException {
        return new Proveedor(fila.getInt("id"), fila.getString("nombre"), fila.getString("contacto"),
                fila.getString("telefono"), fila.getString("email"));
    }

    @Override
    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM proveedores WHERE id = ?", id);
    }
}
