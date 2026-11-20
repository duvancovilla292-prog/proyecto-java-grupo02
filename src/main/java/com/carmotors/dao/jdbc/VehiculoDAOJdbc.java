/*
 * ============================================================
 * Archivo: VehiculoDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de VehiculoDAO.
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
import com.carmotors.dao.VehiculoDAO;
import com.carmotors.modelo.Vehiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
public class VehiculoDAOJdbc implements VehiculoDAO {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private static final String COLUMNAS = "SELECT id, cliente_id, placa, marca, modelo FROM vehiculos";

    private final ConexionFactory conexiones;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public VehiculoDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    @Override
    public int insertar(Vehiculo vehiculo) throws SQLException {
        // Visto en clase: JDBC: Connection, PreparedStatement, ResultSet, try-with-resources -> S18Demo01 UsuarioDAO (Sesión 18)
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "INSERT INTO vehiculos (cliente_id, placa, marca, modelo) VALUES (?, ?, ?, ?)")) {
            sentencia.setInt(1, vehiculo.getClienteId());
            sentencia.setString(2, vehiculo.getPlaca());
            sentencia.setString(3, vehiculo.getMarca());
            sentencia.setString(4, vehiculo.getModelo());
            sentencia.executeUpdate();
            return JdbcUtil.ultimoIdGenerado(conexion);
        }
    }

    @Override
    public Vehiculo buscarPorId(int id) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE id = ?")) {
            sentencia.setInt(1, id);
            return primero(sentencia);
        }
    }

    @Override
    public Vehiculo buscarPorPlaca(String placa) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE placa = ?")) {
            sentencia.setString(1, Vehiculo.normalizarPlaca(placa));
            return primero(sentencia);
        }
    }

    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    @Override
    public List<Vehiculo> listarPorCliente(int clienteId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " WHERE cliente_id = ? ORDER BY placa")) {
            sentencia.setInt(1, clienteId);
            return todos(sentencia);
        }
    }

    @Override
    public List<Vehiculo> listar() throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(COLUMNAS + " ORDER BY placa")) {
            return todos(sentencia);
        }
    }

    private Vehiculo primero(PreparedStatement sentencia) throws SQLException {
        try (ResultSet filas = sentencia.executeQuery()) {
            // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
            return filas.next() ? mapear(filas) : null;
        }
    }

    private List<Vehiculo> todos(PreparedStatement sentencia) throws SQLException {
        List<Vehiculo> vehiculos = new ArrayList<>();
        try (ResultSet filas = sentencia.executeQuery()) {
            // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
            while (filas.next()) {
                vehiculos.add(mapear(filas));
            }
        }
        return vehiculos;
    }

    private Vehiculo mapear(ResultSet fila) throws SQLException {
        return new Vehiculo(fila.getInt("id"), fila.getInt("cliente_id"), fila.getString("placa"),
                fila.getString("marca"), fila.getString("modelo"));
    }

    @Override
    public int actualizar(Vehiculo vehiculo) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE vehiculos SET cliente_id = ?, marca = ?, modelo = ? WHERE id = ?")) {
            sentencia.setInt(1, vehiculo.getClienteId());
            sentencia.setString(2, vehiculo.getMarca());
            sentencia.setString(3, vehiculo.getModelo());
            sentencia.setInt(4, vehiculo.getId());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int eliminar(int id) throws SQLException {
        return JdbcUtil.eliminarPorId(conexiones, "DELETE FROM vehiculos WHERE id = ?", id);
    }
}
