/*
 * ============================================================
 * Archivo: RepuestoDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de RepuestoDAO.
 * Conceptos clave: PATRÓN DAO, PreparedStatement con parámetros "?"
 *            (evita inyección SQL), try-with-resources que cierra
 *            Connection/PreparedStatement/ResultSet (S18Demo01 UsuarioDAO),
 *            conversión ResultSet -> objeto del modelo dentro del DAO.
 * Justificación: es la ÚNICA clase que conoce el SQL de su tabla; si cambia
 *            el motor o la consulta, solo cambia este archivo (SRP/OCP).
 *            Recibe ConexionFactory por constructor (inyección de dependencias)
 *            y abre/cierra una conexión por operación, como ConexionFactory.
 *            Los DECIMAL se leen con getLong porque el taller maneja pesos enteros.
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.RepuestoDAO;
import com.carmotors.modelo.Repuesto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepuestoDAOJdbc implements RepuestoDAO {
    private static final String CONSULTA = "SELECT r.id, r.nombre, r.repuesto_tipo_id, t.nombre AS nombre_tipo, r.marca, "
            + "r.proveedor_id, r.precio_compra, r.precio_venta, r.cantidad_stock, r.stock_minimo, r.cantidad_reservada "
            + "FROM repuestos r JOIN repuesto_tipos t ON t.id = r.repuesto_tipo_id";

    private final ConexionFactory conexiones;

    public RepuestoDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int insertar(Repuesto repuesto) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("INSERT INTO repuestos (nombre, repuesto_tipo_id, "
                     + "marca, proveedor_id, precio_compra, precio_venta, cantidad_stock, stock_minimo, cantidad_reservada) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            sentencia.setString(1, repuesto.getNombre());
            sentencia.setInt(2, repuesto.getTipoId());
            sentencia.setString(3, repuesto.getMarca());
            sentencia.setInt(4, repuesto.getProveedorId());
            sentencia.setInt(5, repuesto.getPrecioCompra());
            sentencia.setInt(6, repuesto.getPrecioVenta());
            sentencia.setInt(7, repuesto.getCantidadStock());
            sentencia.setInt(8, repuesto.getStockMinimo());
            sentencia.setInt(9, repuesto.getCantidadReservada());
            sentencia.executeUpdate();
            return JdbcUtil.ultimoIdGenerado(conexion);
        }
    }

    @Override
    public int actualizarFicha(Repuesto repuesto) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("UPDATE repuestos SET nombre = ?, repuesto_tipo_id = ?, "
                     + "marca = ?, proveedor_id = ?, precio_compra = ?, precio_venta = ?, stock_minimo = ? WHERE id = ?")) {
            sentencia.setString(1, repuesto.getNombre());
            sentencia.setInt(2, repuesto.getTipoId());
            sentencia.setString(3, repuesto.getMarca());
            sentencia.setInt(4, repuesto.getProveedorId());
            sentencia.setInt(5, repuesto.getPrecioCompra());
            sentencia.setInt(6, repuesto.getPrecioVenta());
            sentencia.setInt(7, repuesto.getStockMinimo());
            sentencia.setInt(8, repuesto.getId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int actualizarCantidades(int id, int cantidadStock, int cantidadReservada) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE repuestos SET cantidad_stock = ?, cantidad_reservada = ? WHERE id = ?")) {
            sentencia.setInt(1, cantidadStock);
            sentencia.setInt(2, cantidadReservada);
            sentencia.setInt(3, id);
            return sentencia.executeUpdate();
        }
    }

    @Override
    public Repuesto buscarPorId(int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA + " WHERE r.id = ?")) {
            sentencia.setInt(1, id);
            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next() ? mapear(filas) : null;
            }
        }
    }

    @Override
    public List<Repuesto> listar() throws SQLException {
        List<Repuesto> repuestos = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA + " ORDER BY r.id");
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                repuestos.add(mapear(filas));
            }
        }
        return repuestos;
    }

    private Repuesto mapear(ResultSet fila) throws SQLException {
        return new Repuesto(fila.getInt("id"), fila.getString("nombre"), fila.getInt("repuesto_tipo_id"),
                fila.getString("nombre_tipo"), fila.getString("marca"), fila.getInt("proveedor_id"),
                fila.getInt("precio_compra"), fila.getInt("precio_venta"), fila.getInt("cantidad_stock"),
                fila.getInt("stock_minimo"), fila.getInt("cantidad_reservada"));
    }

    @Override
    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM repuestos WHERE id = ?", id);
    }
}
