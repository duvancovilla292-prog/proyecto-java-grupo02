/*
 * ============================================================
 * Archivo: DetalleRepuestoDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de DetalleRepuestoDAO.
 * Conceptos clave: PATRÓN DAO, PreparedStatement con parámetros "?"
 *            (evita inyección SQL), try-with-resources que cierra
 *            Connection/PreparedStatement/ResultSet (S18Demo01 UsuarioDAO),
 *            conversión ResultSet -> objeto del modelo dentro del DAO.
 * Justificación: es la ÚNICA clase que conoce el SQL de su tabla; si cambia
 *            el motor o la consulta, solo cambia este archivo (SRP/OCP).
 *            Recibe ConexionFactory por constructor (inyección de dependencias)
 *            y abre/cierra una conexión por operación, como ConexionFactory.
 *            El estado RESERVADO/DEDUCIDO se guarda con name() y se lee con valueOf().
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.DetalleRepuestoDAO;
import com.carmotors.modelo.DetalleRepuesto;
import com.carmotors.modelo.EstadoReserva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleRepuestoDAOJdbc implements DetalleRepuestoDAO {
    private static final String CONSULTA = "SELECT sr.servicio_id, sr.repuesto_id, r.nombre AS nombre_repuesto, "
            + "sr.cantidad_usada, sr.precio_unitario_venta, sr.estado "
            + "FROM servicios_repuestos sr JOIN repuestos r ON r.id = sr.repuesto_id";

    private final ConexionFactory conexiones;

    public DetalleRepuestoDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int insertar(DetalleRepuesto detalle) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("INSERT INTO servicios_repuestos (servicio_id, "
                     + "repuesto_id, cantidad_usada, precio_unitario_venta, estado) VALUES (?, ?, ?, ?, ?)")) {
            sentencia.setInt(1, detalle.getServicioId());
            sentencia.setInt(2, detalle.getRepuestoId());
            sentencia.setInt(3, detalle.getCantidad());
            sentencia.setInt(4, detalle.getPrecioUnitario());
            sentencia.setString(5, detalle.getEstado().name());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public List<DetalleRepuesto> listar() throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA + " ORDER BY sr.servicio_id")) {
            return todos(sentencia);
        }
    }

    @Override
    public List<DetalleRepuesto> listarPorServicio(int servicioId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA + " WHERE sr.servicio_id = ? ORDER BY r.nombre")) {
            sentencia.setInt(1, servicioId);
            return todos(sentencia);
        }
    }

    @Override
    public int actualizarEstado(int servicioId, int repuestoId, EstadoReserva estado) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE servicios_repuestos SET estado = ? WHERE servicio_id = ? AND repuesto_id = ?")) {
            sentencia.setString(1, estado.name());
            sentencia.setInt(2, servicioId);
            sentencia.setInt(3, repuestoId);
            return sentencia.executeUpdate();
        }
    }

    private List<DetalleRepuesto> todos(PreparedStatement sentencia) throws SQLException {
        List<DetalleRepuesto> detalles = new ArrayList<>();
        try (ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                detalles.add(new DetalleRepuesto(filas.getInt("servicio_id"), filas.getInt("repuesto_id"),
                        filas.getString("nombre_repuesto"), filas.getInt("cantidad_usada"),
                        filas.getInt("precio_unitario_venta"), EstadoReserva.valueOf(filas.getString("estado"))));
            }
        }
        return detalles;
    }

    @Override
    public int eliminar(int servicioId, int repuestoId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "DELETE FROM servicios_repuestos WHERE servicio_id = ? AND repuesto_id = ?")) {
            sentencia.setInt(1, servicioId);
            sentencia.setInt(2, repuestoId);
            return sentencia.executeUpdate();
        }
    }
}
