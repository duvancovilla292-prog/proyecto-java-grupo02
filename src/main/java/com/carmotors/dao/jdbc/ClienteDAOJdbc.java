/*
 * ============================================================
 * Archivo: ClienteDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de ClienteDAO.
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
import com.carmotors.dao.ClienteDAO;
import com.carmotors.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
public class ClienteDAOJdbc implements ClienteDAO {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private static final String COLUMNAS = "SELECT id, nombre, documento_identidad, telefono, email FROM clientes";

    private final ConexionFactory conexiones;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public ClienteDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    @Override
    public int insertar(Cliente cliente) throws SQLException {
        // Visto en clase: JDBC: Connection, PreparedStatement, ResultSet, try-with-resources -> S18Demo01 UsuarioDAO (Sesión 18)
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "INSERT INTO clientes (nombre, documento_identidad, telefono, email) VALUES (?, ?, ?, ?)")) {
            sentencia.setString(1, cliente.getNombre());
            sentencia.setString(2, cliente.getDocumento());
            sentencia.setString(3, cliente.getTelefono());
            sentencia.setString(4, cliente.getEmail());
            sentencia.executeUpdate();
            return JdbcUtil.ultimoIdGenerado(conexion);
        }
    }

    @Override
    public int actualizar(Cliente cliente) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE clientes SET nombre = ?, telefono = ?, email = ? WHERE id = ?")) {
            sentencia.setString(1, cliente.getNombre());
            sentencia.setString(2, cliente.getTelefono());
            sentencia.setString(3, cliente.getEmail());
            sentencia.setInt(4, cliente.getId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public Cliente buscarPorId(int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE id = ?")) {
            sentencia.setInt(1, id);
            return primero(sentencia);
        }
    }

    @Override
    public Cliente buscarPorDocumento(String documento) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE documento_identidad = ?")) {
            sentencia.setString(1, documento);
            return primero(sentencia);
        }
    }

    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    @Override
    public List<Cliente> listar() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " ORDER BY nombre");
             ResultSet filas = sentencia.executeQuery()) {
            // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
            while (filas.next()) {
                clientes.add(mapear(filas));
            }
        }
        return clientes;
    }

    // Ejecuta la consulta y devuelve la primera fila o null.
    private Cliente primero(PreparedStatement sentencia) throws SQLException {
        try (ResultSet filas = sentencia.executeQuery()) {
            // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
            return filas.next() ? mapear(filas) : null;
        }
    }

    // Convierte la fila actual del ResultSet en un Cliente.
    private Cliente mapear(ResultSet fila) throws SQLException {
        return new Cliente(fila.getInt("id"), fila.getString("nombre"), fila.getString("documento_identidad"),
                fila.getString("telefono"), fila.getString("email"));
    }

    @Override
    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM clientes WHERE id = ?", id);
    }
}
