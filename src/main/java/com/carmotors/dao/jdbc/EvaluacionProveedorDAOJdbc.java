/*
 * ============================================================
 * Archivo: EvaluacionProveedorDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de EvaluacionProveedorDAO.
 * Conceptos clave: PATRÓN DAO, PreparedStatement con parámetros "?"
 *            (evita inyección SQL), try-with-resources que cierra
 *            Connection/PreparedStatement/ResultSet (S18Demo01 UsuarioDAO),
 *            conversión ResultSet -> objeto del modelo dentro del DAO.
 * Justificación: es la ÚNICA clase que conoce el SQL de su tabla; si cambia
 *            el motor o la consulta, solo cambia este archivo (SRP/OCP).
 *            Recibe ConexionFactory por constructor (inyección de dependencias)
 *            y abre/cierra una conexión por operación, como ConexionFactory.
 *            La fecha se lee con getString y al insertar la pone MySQL (CURDATE()).
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.EvaluacionProveedorDAO;
import com.carmotors.modelo.EvaluacionProveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
public class EvaluacionProveedorDAOJdbc implements EvaluacionProveedorDAO {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    private static final String COLUMNAS =
            "SELECT id, proveedor_id, calidad, puntualidad, fecha, comentario FROM evaluaciones_proveedor";

    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final ConexionFactory conexiones;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public EvaluacionProveedorDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    @Override
    public int insertar(EvaluacionProveedor evaluacion) throws SQLException {
        // Visto en clase: JDBC: Connection, PreparedStatement, ResultSet, try-with-resources -> S18Demo01 UsuarioDAO (Sesión 18)
        try (Connection conexion = conexiones.abrir();
             // Visto en clase: funciones de MySQL en el SQL (NOW, CURDATE, DATE_ADD, LAST_INSERT_ID) -> son SQL de MySQL, no Java; en Java solo se vio LocalDate.of (S12Demo02)
             PreparedStatement sentencia = conexion.prepareStatement("INSERT INTO evaluaciones_proveedor "
                     + "(proveedor_id, calidad, puntualidad, fecha, comentario) VALUES (?, ?, ?, CURDATE(), ?)")) {
            sentencia.setInt(1, evaluacion.getProveedorId());
            sentencia.setInt(2, evaluacion.getCalidad());
            sentencia.setInt(3, evaluacion.getPuntualidad());
            sentencia.setString(4, evaluacion.getComentario());
            sentencia.executeUpdate();
            return JdbcUtil.ultimoIdGenerado(conexion);
        }
    }

    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    @Override
    public List<EvaluacionProveedor> listar() throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " ORDER BY fecha")) {
            return todos(sentencia);
        }
    }

    @Override
    public List<EvaluacionProveedor> listarPorProveedor(int proveedorId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE proveedor_id = ? ORDER BY fecha")) {
            sentencia.setInt(1, proveedorId);
            return todos(sentencia);
        }
    }

    private List<EvaluacionProveedor> todos(PreparedStatement sentencia) throws SQLException {
        List<EvaluacionProveedor> evaluaciones = new ArrayList<>();
        try (ResultSet filas = sentencia.executeQuery()) {
            // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
            while (filas.next()) {
                evaluaciones.add(new EvaluacionProveedor(filas.getInt("id"), filas.getInt("proveedor_id"),
                        filas.getInt("calidad"), filas.getInt("puntualidad"),
                        filas.getString("fecha"), filas.getString("comentario")));
            }
        }
        return evaluaciones;
    }

    @Override
    public EvaluacionProveedor buscarPorId(int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE id = ?")) {
            sentencia.setInt(1, id);
            List<EvaluacionProveedor> encontradas = todos(sentencia);
            // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
            return encontradas.isEmpty() ? null : encontradas.get(0);
        }
    }

    @Override
    public int actualizar(EvaluacionProveedor evaluacion) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE evaluaciones_proveedor SET calidad = ?, puntualidad = ?, comentario = ? WHERE id = ?")) {
            sentencia.setInt(1, evaluacion.getCalidad());
            sentencia.setInt(2, evaluacion.getPuntualidad());
            sentencia.setString(3, evaluacion.getComentario());
            sentencia.setInt(4, evaluacion.getId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM evaluaciones_proveedor WHERE id = ?", id);
    }
}
