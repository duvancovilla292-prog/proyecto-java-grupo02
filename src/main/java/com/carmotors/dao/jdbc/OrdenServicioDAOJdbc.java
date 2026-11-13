/*
 * ============================================================
 * Archivo: OrdenServicioDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de OrdenServicioDAO.
 * Conceptos clave: PATRÓN DAO, PreparedStatement con parámetros "?"
 *            (evita inyección SQL), try-with-resources que cierra
 *            Connection/PreparedStatement/ResultSet (S18Demo01 UsuarioDAO),
 *            conversión ResultSet -> objeto del modelo dentro del DAO.
 * Justificación: es la ÚNICA clase que conoce el SQL de su tabla; si cambia
 *            el motor o la consulta, solo cambia este archivo (SRP/OCP).
 *            Recibe ConexionFactory por constructor (inyección de dependencias)
 *            y abre/cierra una conexión por operación, como ConexionFactory.
 *            Las fechas se leen con getString; NOW(), CURDATE() y DATE_ADD
 *            los resuelve MySQL (en los demos solo se vio LocalDate.of).
 *            Reconstruye cada fila con OrdenServicioBuilder (patrón Builder)
 *            y traduce los ids de las tablas de consulta a enum.
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.OrdenServicioDAO;
import com.carmotors.modelo.CalendarioPreventivo;
import com.carmotors.modelo.EstadoServicio;
import com.carmotors.modelo.OrdenServicio;
import com.carmotors.modelo.OrdenServicioBuilder;
import com.carmotors.modelo.TipoMantenimiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdenServicioDAOJdbc implements OrdenServicioDAO {
    private static final String COLUMNAS = "SELECT id, vehiculo_id, mantenimiento_tipo_id, fecha_recepcion, "
            + "fecha_entrega, descripcion_problema, costo_mano_obra, estado_id FROM servicios";

    private final ConexionFactory conexiones;

    public OrdenServicioDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int insertar(OrdenServicio orden) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("INSERT INTO servicios (vehiculo_id, "
                     + "mantenimiento_tipo_id, fecha_recepcion, descripcion_problema, costo_mano_obra, "
                     + "estado_id) VALUES (?, ?, NOW(), ?, ?, ?)")) {   // NOW(): fecha y hora actual de MySQL
            sentencia.setInt(1, orden.getVehiculoId());
            sentencia.setInt(2, orden.getTipoMantenimiento().getId());
            sentencia.setString(3, orden.getDescripcionProblema());
            sentencia.setInt(4, orden.getCostoManoObra());
            sentencia.setInt(5, orden.getEstado().getId());
            sentencia.executeUpdate();
            return JdbcUtil.ultimoIdGenerado(conexion);
        }
    }

    @Override
    public OrdenServicio buscarPorId(int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE id = ?")) {
            sentencia.setInt(1, id);
            List<OrdenServicio> encontradas = todas(sentencia);
            return encontradas.isEmpty() ? null : encontradas.get(0);
        }
    }

    @Override
    public List<OrdenServicio> listar() throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " ORDER BY fecha_recepcion, id")) {
            return todas(sentencia);
        }
    }

    @Override
    public List<OrdenServicio> listarPorVehiculo(int vehiculoId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE vehiculo_id = ? ORDER BY fecha_recepcion")) {
            sentencia.setInt(1, vehiculoId);
            return todas(sentencia);
        }
    }

    @Override
    public int actualizarEstado(int id, EstadoServicio estado) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("UPDATE servicios SET estado_id = ? WHERE id = ?")) {
            sentencia.setInt(1, estado.getId());
            sentencia.setInt(2, id);
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int registrarEntrega(int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE servicios SET estado_id = ?, fecha_entrega = NOW() WHERE id = ?")) {
            sentencia.setInt(1, EstadoServicio.ENTREGADO.getId());
            sentencia.setInt(2, id);
            return sentencia.executeUpdate();
        }
    }

    @Override
    public List<CalendarioPreventivo> calendarioPreventivos(int meses, int dias) throws SQLException {
        // MySQL calcula las fechas: DATE() quita la hora, DATE_ADD suma meses o días y CURDATE() es hoy.
        // Solo preventivos (tipo 1) con trabajo terminado (estados 3 Completado, 4 Facturado, 5 Entregado).
        String sql = "SELECT vehiculo_id, DATE(MAX(fecha_recepcion)) AS ultimo, "
                + "DATE_ADD(DATE(MAX(fecha_recepcion)), INTERVAL ? MONTH) AS proximo, "
                + "CURDATE() AS hoy, DATE_ADD(CURDATE(), INTERVAL ? DAY) AS limite "
                + "FROM servicios WHERE mantenimiento_tipo_id = ? AND estado_id IN (?, ?, ?) "
                + "GROUP BY vehiculo_id ORDER BY proximo";
        List<CalendarioPreventivo> resultado = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, meses);
            sentencia.setInt(2, dias);
            sentencia.setInt(3, TipoMantenimiento.PREVENTIVO.getId());
            sentencia.setInt(4, EstadoServicio.COMPLETADO.getId());
            sentencia.setInt(5, EstadoServicio.FACTURADO.getId());
            sentencia.setInt(6, EstadoServicio.ENTREGADO.getId());
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    resultado.add(new CalendarioPreventivo(filas.getInt("vehiculo_id"), filas.getString("ultimo"),
                            filas.getString("proximo"), filas.getString("hoy"), filas.getString("limite")));
                }
            }
        }
        return resultado;
    }

    private List<OrdenServicio> todas(PreparedStatement sentencia) throws SQLException {
        List<OrdenServicio> ordenes = new ArrayList<>();
        try (ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                ordenes.add(new OrdenServicioBuilder()
                        .id(filas.getInt("id"))
                        .vehiculoId(filas.getInt("vehiculo_id"))
                        .tipoMantenimiento(TipoMantenimiento.desdeId(filas.getInt("mantenimiento_tipo_id")))
                        .fechaRecepcion(filas.getString("fecha_recepcion"))
                        .fechaEntrega(filas.getString("fecha_entrega"))   // null si no se ha entregado
                        .descripcionProblema(filas.getString("descripcion_problema"))
                        .costoManoObra(filas.getInt("costo_mano_obra"))
                        .estado(EstadoServicio.desdeId(filas.getInt("estado_id")))
                        .build());
            }
        }
        return ordenes;
    }

    @Override
    public int actualizar(OrdenServicio orden) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("UPDATE servicios SET mantenimiento_tipo_id = ?, "
                     + "descripcion_problema = ?, costo_mano_obra = ? WHERE id = ?")) {
            sentencia.setInt(1, orden.getTipoMantenimiento().getId());
            sentencia.setString(2, orden.getDescripcionProblema());
            sentencia.setInt(3, orden.getCostoManoObra());
            sentencia.setInt(4, orden.getId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM servicios WHERE id = ?", id);
    }
}
