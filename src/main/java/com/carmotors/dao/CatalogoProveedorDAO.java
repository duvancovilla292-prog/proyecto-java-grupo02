/*
 * ============================================================
 * Archivo: CatalogoProveedorDAO.java
 * Propósito: contrato de acceso a datos de proveedor_catalogo.
 * Conceptos clave: PATRÓN DAO (S18Demo01 UsuarioDAO) definido como
 *            interfaz (sesión 07 Notificable), List genérica (sesión 08),
 *            SQLException verificada (sesión 10).
 * Justificación: los servicios dependen de esta ABSTRACCIÓN y no de JDBC
 *            (Inversión de Dependencias, la "D" de SOLID). Cada DAO atiende
 *            UNA sola entidad (Responsabilidad Única) y expone solo los
 *            métodos que sus clientes usan (Segregación de Interfaces).
 *            Cada DAO ofrece el CRUD completo (insertar, buscar/listar,
 *            actualizar, eliminar) más sus consultas específicas.
 *            Convención: buscarPor... devuelve null si no hay resultado
 *            (como Grupo.obtener de S08Demo04); el llamador lo comprueba.
 * ============================================================
 */
package com.carmotors.dao;

import com.carmotors.modelo.ProductoCatalogo;

import java.sql.SQLException;
import java.util.List;

public interface CatalogoProveedorDAO {
    int insertar(ProductoCatalogo producto) throws SQLException;

    /** Cambia el costo de un producto ya registrado en el catálogo. */
    int actualizarCosto(int proveedorId, int repuestoId, int costo) throws SQLException;

    List<ProductoCatalogo> listarPorProveedor(int proveedorId) throws SQLException;

    List<ProductoCatalogo> listarPorRepuesto(int repuestoId) throws SQLException;

    int eliminar(int proveedorId, int repuestoId) throws SQLException;
}
