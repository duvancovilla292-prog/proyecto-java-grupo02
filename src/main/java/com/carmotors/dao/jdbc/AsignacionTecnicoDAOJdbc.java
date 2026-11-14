/*
 * ============================================================
 * Archivo: AsignacionTecnicoDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de AsignacionTecnicoDAO.
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
import com.carmotors.dao.AsignacionTecnicoDAO;
import com.carmotors.modelo.AsignacionTecnico;
import com.carmotors.modelo.Tecnico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsignacionTecnicoDAOJdbc implements AsignacionTecnicoDAO {
    private final ConexionFactory conexiones;

    public AsignacionTecnicoDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int insertar(AsignacionTecnico asignacion) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "INSERT INTO servicios_tecnicos (servicio_id, tecnico_id) VALUES (?, ?)")) {
            sentencia.setInt(1, asignacion.getServicioId());
            sentencia.setInt(2, asignacion.getTecnicoId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public List<AsignacionTecnico> listar() throws SQLException {
        List<AsignacionTecnico> asignaciones = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "SELECT servicio_id, tecnico_id FROM servicios_tecnicos ORDER BY servicio_id");
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                asignaciones.add(new AsignacionTecnico(filas.getInt("servicio_id"), filas.getInt("tecnico_id")));
            }
        }
        return asignaciones;
    }

    @Override
    public List<Tecnico> listarTecnicosPorServicio(int servicioId) throws SQLException {
        List<Tecnico> tecnicos = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("SELECT t.id, t.nombre, t.especialidad, t.activo "
                     + "FROM servicios_tecnicos st JOIN tecnicos t ON t.id = st.tecnico_id "
                     + "WHERE st.servicio_id = ? ORDER BY t.nombre")) {
            sentencia.setInt(1, servicioId);
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    tecnicos.add(TecnicoDAOJdbc.mapear(filas));
                }
            }
        }
        return tecnicos;
    }

    @Override
    public int actualizar(int servicioId, int tecnicoAnterior, int tecnicoNuevo) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE servicios_tecnicos SET tecnico_id = ? WHERE servicio_id = ? AND tecnico_id = ?")) {
            sentencia.setInt(1, tecnicoNuevo);
            sentencia.setInt(2, servicioId);
            sentencia.setInt(3, tecnicoAnterior);
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int eliminar(int servicioId, int tecnicoId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "DELETE FROM servicios_tecnicos WHERE servicio_id = ? AND tecnico_id = ?")) {
            sentencia.setInt(1, servicioId);
            sentencia.setInt(2, tecnicoId);
            return sentencia.executeUpdate();
        }
    }
}
