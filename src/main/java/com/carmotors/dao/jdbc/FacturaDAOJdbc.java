/*
 * ============================================================
 * Archivo: FacturaDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de FacturaDAO.
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
import com.carmotors.dao.FacturaDAO;
import com.carmotors.modelo.Factura;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAOJdbc implements FacturaDAO {
    private static final String COLUMNAS = "SELECT id, servicio_id, fecha_emision, total_repuestos, total_mano_obra, "
            + "impuestos, total_final FROM facturas";

    private final ConexionFactory conexiones;

    public FacturaDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int insertar(Factura factura) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("INSERT INTO facturas (servicio_id, fecha_emision, "
                     + "total_repuestos, total_mano_obra, impuestos, total_final) VALUES (?, NOW(), ?, ?, ?, ?)")) {
            sentencia.setInt(1, factura.getServicioId());   // fecha_emision = NOW() de MySQL
            sentencia.setInt(2, factura.getTotalRepuestos());
            sentencia.setInt(3, factura.getTotalManoObra());
            sentencia.setInt(4, factura.getImpuestos());
            sentencia.setInt(5, factura.getTotalFinal());
            sentencia.executeUpdate();
            return JdbcUtil.ultimoIdGenerado(conexion);
        }
    }

    @Override
    public Factura buscarPorServicio(int servicioId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE servicio_id = ?")) {
            sentencia.setInt(1, servicioId);
            List<Factura> encontradas = todas(sentencia);
            return encontradas.isEmpty() ? null : encontradas.get(0);
        }
    }

    @Override
    public List<Factura> listar() throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " ORDER BY fecha_emision")) {
            return todas(sentencia);
        }
    }

    private List<Factura> todas(PreparedStatement sentencia) throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        try (ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                facturas.add(new Factura(filas.getInt("id"), filas.getInt("servicio_id"),
                        filas.getString("fecha_emision"), filas.getInt("total_repuestos"),
                        filas.getInt("total_mano_obra"), filas.getInt("impuestos"), filas.getInt("total_final")));
            }
        }
        return facturas;
    }

    @Override
    public int actualizar(Factura factura) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("UPDATE facturas SET total_repuestos = ?, "
                     + "total_mano_obra = ?, impuestos = ?, total_final = ? WHERE id = ?")) {
            sentencia.setInt(1, factura.getTotalRepuestos());
            sentencia.setInt(2, factura.getTotalManoObra());
            sentencia.setInt(3, factura.getImpuestos());
            sentencia.setInt(4, factura.getTotalFinal());
            sentencia.setInt(5, factura.getId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM facturas WHERE id = ?", id);
    }
}
