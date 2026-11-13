/*
 * ============================================================
 * Archivo: CatalogoProveedorDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de CatalogoProveedorDAO.
 * Conceptos clave: PATRÓN DAO, PreparedStatement con parámetros "?"
 *            (evita inyección SQL), try-with-resources que cierra
 *            Connection/PreparedStatement/ResultSet (S18Demo01 UsuarioDAO),
 *            conversión ResultSet -> objeto del modelo dentro del DAO.
 * Justificación: es la ÚNICA clase que conoce el SQL de su tabla; si cambia
 *            el motor o la consulta, solo cambia este archivo (SRP/OCP).
 *            Recibe ConexionFactory por constructor (inyección de dependencias)
 *            y abre/cierra una conexión por operación, como ConexionFactory.
 *            Las consultas usan JOIN para traer los nombres a mostrar.
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.CatalogoProveedorDAO;
import com.carmotors.modelo.ProductoCatalogo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogoProveedorDAOJdbc implements CatalogoProveedorDAO {
    private static final String CONSULTA = "SELECT c.proveedor_id, p.nombre AS nombre_proveedor, c.repuesto_id, "
            + "r.nombre AS nombre_repuesto, c.costo FROM proveedor_catalogo c "
            + "JOIN proveedores p ON p.id = c.proveedor_id JOIN repuestos r ON r.id = c.repuesto_id";

    private final ConexionFactory conexiones;

    public CatalogoProveedorDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int insertar(ProductoCatalogo producto) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "INSERT INTO proveedor_catalogo (proveedor_id, repuesto_id, costo) VALUES (?, ?, ?)")) {
            sentencia.setInt(1, producto.getProveedorId());
            sentencia.setInt(2, producto.getRepuestoId());
            sentencia.setInt(3, producto.getCosto());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int actualizarCosto(int proveedorId, int repuestoId, int costo) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE proveedor_catalogo SET costo = ? WHERE proveedor_id = ? AND repuesto_id = ?")) {
            sentencia.setInt(1, costo);
            sentencia.setInt(2, proveedorId);
            sentencia.setInt(3, repuestoId);
            return sentencia.executeUpdate();
        }
    }

    @Override
    public List<ProductoCatalogo> listarPorProveedor(int proveedorId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     CONSULTA + " WHERE c.proveedor_id = ? ORDER BY r.nombre")) {
            sentencia.setInt(1, proveedorId);
            return todos(sentencia);
        }
    }

    @Override
    public List<ProductoCatalogo> listarPorRepuesto(int repuestoId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     CONSULTA + " WHERE c.repuesto_id = ? ORDER BY c.costo")) {
            sentencia.setInt(1, repuestoId);
            return todos(sentencia);
        }
    }

    private List<ProductoCatalogo> todos(PreparedStatement sentencia) throws SQLException {
        List<ProductoCatalogo> productos = new ArrayList<>();
        try (ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                productos.add(new ProductoCatalogo(filas.getInt("proveedor_id"), filas.getString("nombre_proveedor"),
                        filas.getInt("repuesto_id"), filas.getString("nombre_repuesto"), filas.getInt("costo")));
            }
        }
        return productos;
    }

    @Override
    public int eliminar(int proveedorId, int repuestoId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "DELETE FROM proveedor_catalogo WHERE proveedor_id = ? AND repuesto_id = ?")) {
            sentencia.setInt(1, proveedorId);
            sentencia.setInt(2, repuestoId);
            return sentencia.executeUpdate();
        }
    }
}
