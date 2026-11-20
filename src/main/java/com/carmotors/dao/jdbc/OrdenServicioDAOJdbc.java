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
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.OrdenServicioDAO;
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

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
public class OrdenServicioDAOJdbc implements OrdenServicioDAO {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    private static final String COLUMNAS = "SELECT id, vehiculo_id, mantenimiento_tipo_id, fecha_recepcion, "
            + "fecha_entrega, descripcion_problema, costo_mano_obra, estado_id FROM servicios";

    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final ConexionFactory conexiones;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public OrdenServicioDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    public int insertar(OrdenServicio orden) throws SQLException {
        // Visto en clase: JDBC: Connection, PreparedStatement, ResultSet, try-with-resources -> S18Demo01 UsuarioDAO (Sesión 18)
        try (Connection conexion = conexiones.abrir();
             // Visto en clase: funciones de MySQL en el SQL (NOW, CURDATE, DATE_ADD, LAST_INSERT_ID) -> son SQL de MySQL, no Java; en Java solo se vio LocalDate.of (S12Demo02)
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

    public OrdenServicio buscarPorId(int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE id = ?")) {
            sentencia.setInt(1, id);
            // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
            List<OrdenServicio> encontradas = todas(sentencia);
            // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
            return encontradas.isEmpty() ? null : encontradas.get(0);
        }
    }

    public List<OrdenServicio> listar() throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " ORDER BY fecha_recepcion, id")) {
            return todas(sentencia);
        }
    }

    public List<OrdenServicio> listarPorVehiculo(int vehiculoId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE vehiculo_id = ? ORDER BY fecha_recepcion")) {
            sentencia.setInt(1, vehiculoId);
            return todas(sentencia);
        }
    }

    public int actualizarEstado(int id, EstadoServicio estado) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement("UPDATE servicios SET estado_id = ? WHERE id = ?")) {
            sentencia.setInt(1, estado.getId());
            sentencia.setInt(2, id);
            return sentencia.executeUpdate();
        }
    }

    public int registrarEntrega(int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE servicios SET estado_id = ?, fecha_entrega = NOW() WHERE id = ?")) {
            sentencia.setInt(1, EstadoServicio.ENTREGADO.getId());
            sentencia.setInt(2, id);
            return sentencia.executeUpdate();
        }
    }

    private List<OrdenServicio> todas(PreparedStatement sentencia) throws SQLException {
        List<OrdenServicio> ordenes = new ArrayList<>();
        try (ResultSet filas = sentencia.executeQuery()) {
            // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
            while (filas.next()) {
                // Visto en clase: PATRÓN BUILDER -> S12Demo02 SolicitudBuilder (Sesión 12)
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

    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM servicios WHERE id = ?", id);
    }
}
