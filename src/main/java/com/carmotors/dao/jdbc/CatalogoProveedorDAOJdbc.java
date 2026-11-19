/*
 * ============================================================
 * Archivo: CatalogoProveedorDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de CatalogoProveedorDAO.
 * Conceptos clave: PATRÓN DAO, PreparedStatement con parámetros "?"
 *            (evita inyección SQL), try-with-resources que cierra
 *            Connection/PreparedStatement/ResultSet (S18Demo01 UsuarioDAO),
 *            conversión ResultSet -> objeto del modelo dentro del DAO.
 * Justificación: es la ÚNICA clase que conoce el SQL de su tabla; si cambia
 *            el motor o la consulta, solo cambia este archivo (SRP/OCP).
 *            Recibe ConexionFactory por constructor (inyección de dependencias)
 *            y abre/cierra una conexión por operación, como ConexionFactory.
 *            Las consultas usan JOIN para traer los nombres a mostrar.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.CatalogoProveedorDAO;
import com.carmotors.modelo.ProductoCatalogo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
public class CatalogoProveedorDAOJdbc implements CatalogoProveedorDAO {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    private static final String CONSULTA = "SELECT c.proveedor_id, p.nombre AS nombre_proveedor, c.repuesto_id, "
            + "r.nombre AS nombre_repuesto, c.costo FROM proveedor_catalogo c "
            + "JOIN proveedores p ON p.id = c.proveedor_id JOIN repuestos r ON r.id = c.repuesto_id";

    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final ConexionFactory conexiones;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public CatalogoProveedorDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    @Override
    public int insertar(ProductoCatalogo producto) throws SQLException {
        // Visto en clase: JDBC: Connection, PreparedStatement, ResultSet, try-with-resources -> S18Demo01 UsuarioDAO (Sesión 18)
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "INSERT INTO proveedor_catalogo (proveedor_id, repuesto_id, costo) VALUES (?, ?, ?)")) {
            sentencia.setInt(1, producto.getProveedorId());
            sentencia.setInt(2, producto.getRepuestoId());
            sentencia.setInt(3, producto.getCosto());
            return sentencia.executeUpdate();
        }
    }

    @Override
    public int actualizarCosto(int proveedorId, int repuestoId, int costo) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "UPDATE proveedor_catalogo SET costo = ? WHERE proveedor_id = ? AND repuesto_id = ?")) {
            sentencia.setInt(1, costo);
            sentencia.setInt(2, proveedorId);
            sentencia.setInt(3, repuestoId);
            return sentencia.executeUpdate();
        }
    }

    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    @Override
    public List<ProductoCatalogo> listarPorProveedor(int proveedorId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     CONSULTA + " WHERE c.proveedor_id = ? ORDER BY r.nombre")) {
            sentencia.setInt(1, proveedorId);
            return todos(sentencia);
        }
    }

    @Override
    public List<ProductoCatalogo> listarPorRepuesto(int repuestoId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     CONSULTA + " WHERE c.repuesto_id = ? ORDER BY c.costo")) {
            sentencia.setInt(1, repuestoId);
            return todos(sentencia);
        }
    }

    private List<ProductoCatalogo> todos(PreparedStatement sentencia) throws SQLException {
        List<ProductoCatalogo> productos = new ArrayList<>();
        try (ResultSet filas = sentencia.executeQuery()) {
            // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
            while (filas.next()) {
                productos.add(new ProductoCatalogo(filas.getInt("proveedor_id"), filas.getString("nombre_proveedor"),
                        filas.getInt("repuesto_id"), filas.getString("nombre_repuesto"), filas.getInt("costo")));
            }
        }
        return productos;
    }

    @Override
    public int eliminar(int proveedorId, int repuestoId) throws SQLException {
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "DELETE FROM proveedor_catalogo WHERE proveedor_id = ? AND repuesto_id = ?")) {
            sentencia.setInt(1, proveedorId);
            sentencia.setInt(2, repuestoId);
            return sentencia.executeUpdate();
        }
    }
}
