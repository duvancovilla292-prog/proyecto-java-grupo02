/*
 * ============================================================
 * Archivo: LoteDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de LoteDAO.
 * Conceptos clave: PATRÓN DAO, PreparedStatement con parámetros "?"
 *            (evita inyección SQL), try-with-resources que cierra
 *            Connection/PreparedStatement/ResultSet (S18Demo01 UsuarioDAO),
 *            conversión ResultSet -> objeto del modelo dentro del DAO.
 * Justificación: es la ÚNICA clase que conoce el SQL de su tabla; si cambia
 *            el motor o la consulta, solo cambia este archivo (SRP/OCP).
 *            Recibe ConexionFactory por constructor (inyección de dependencias)
 *            y abre/cierra una conexión por operación, como ConexionFactory.
 *            Fechas con setString/getString; la fecha de ingreso la pone MySQL
 *            (CURDATE()) y "por vencer" se calcula con DATE_ADD en el SQL.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.LoteDAO;
import com.carmotors.modelo.Lote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
public class LoteDAOJdbc implements LoteDAO {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    private static final String COLUMNAS =
            "SELECT id, codigo, repuesto_id, proveedor_id, fecha_ingreso, fecha_caducidad, cantidad FROM lotes";

    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final ConexionFactory conexiones;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public LoteDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    @Override
    public int insertar(Lote lote) throws SQLException {
        // Visto en clase: JDBC: Connection, PreparedStatement, ResultSet, try-with-resources -> S18Demo01 UsuarioDAO (Sesión 18)
        try (Connection conexion = conexiones.abrir();
             // Visto en clase: funciones de MySQL en el SQL (NOW, CURDATE, DATE_ADD, LAST_INSERT_ID) -> son SQL de MySQL, no Java; en Java solo se vio LocalDate.of (S12Demo02)
             PreparedStatement sentencia = conexion.prepareStatement("INSERT INTO lotes (codigo, repuesto_id, "
                     + "proveedor_id, fecha_ingreso, fecha_caducidad, cantidad) VALUES (?, ?, ?, CURDATE(), ?, ?)")) {
            sentencia.setString(1, lote.getCodigo());
            sentencia.setInt(2, lote.getRepuestoId());
            sentencia.setInt(3, lote.getProveedorId());
            sentencia.setString(4, lote.getFechaCaducidad()); // null -> NULL en MySQL
            sentencia.setInt(5, lote.getCantidad());
            sentencia.executeUpdate();
            return JdbcUtil.ultimoIdGenerado(conexion);
        }
    }

    @Override
    public Lote buscarPorCodigo(String codigo) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE codigo = ?")) {
            // Visto en clase: String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
            // Visto en clase: toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
            sentencia.setString(1, codigo.strip().toUpperCase());
            // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
            List<Lote> encontrados = todos(sentencia);
            // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
            return encontrados.isEmpty() ? null : encontrados.get(0);
        }
    }

    @Override
    public List<Lote> listar() throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " ORDER BY fecha_ingreso, id")) {
            return todos(sentencia);
        }
    }

    @Override
    public List<Lote> listarPorRepuesto(int repuestoId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE repuesto_id = ? ORDER BY fecha_ingreso")) {
            sentencia.setInt(1, repuestoId);
            return todos(sentencia);
        }
    }

    private List<Lote> todos(PreparedStatement sentencia) throws SQLException {
        List<Lote> lotes = new ArrayList<>();
        try (ResultSet filas = sentencia.executeQuery()) {
            // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
            while (filas.next()) {
                lotes.add(new Lote(filas.getInt("id"), filas.getString("codigo"), filas.getInt("repuesto_id"),
                        filas.getInt("proveedor_id"), filas.getString("fecha_ingreso"),
                        filas.getString("fecha_caducidad"), filas.getInt("cantidad")));
            }
        }
        return lotes;
    }

    @Override
    public int actualizar(Lote lote) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE lotes SET proveedor_id = ?, fecha_caducidad = ? WHERE id = ?")) {
            sentencia.setInt(1, lote.getProveedorId());
            sentencia.setString(2, lote.getFechaCaducidad());
            sentencia.setInt(3, lote.getId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM lotes WHERE id = ?", id);
    }
}
