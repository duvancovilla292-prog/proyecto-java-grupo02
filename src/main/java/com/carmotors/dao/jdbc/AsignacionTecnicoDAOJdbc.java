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
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
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

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
public class AsignacionTecnicoDAOJdbc implements AsignacionTecnicoDAO {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final ConexionFactory conexiones;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public AsignacionTecnicoDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    @Override
    public int insertar(AsignacionTecnico asignacion) throws SQLException {
        // Visto en clase: JDBC: Connection, PreparedStatement, ResultSet, try-with-resources -> S18Demo01 UsuarioDAO (Sesión 18)
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "INSERT INTO servicios_tecnicos (servicio_id, tecnico_id) VALUES (?, ?)")) {
            sentencia.setInt(1, asignacion.getServicioId());
            sentencia.setInt(2, asignacion.getTecnicoId());
            return sentencia.executeUpdate();
        }
    }

    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    @Override
    public List<AsignacionTecnico> listar() throws SQLException {
        List<AsignacionTecnico> asignaciones = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "SELECT servicio_id, tecnico_id FROM servicios_tecnicos ORDER BY servicio_id");
             ResultSet filas = sentencia.executeQuery()) {
            // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
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
