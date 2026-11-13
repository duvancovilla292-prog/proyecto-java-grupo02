/*
 * ============================================================
 * Archivo: EvaluacionProveedorDAO.java
 * Propósito: contrato de acceso a datos de evaluaciones_proveedor.
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

import com.carmotors.modelo.EvaluacionProveedor;

import java.sql.SQLException;
import java.util.List;

public interface EvaluacionProveedorDAO {
    int insertar(EvaluacionProveedor evaluacion) throws SQLException;

    List<EvaluacionProveedor> listar() throws SQLException;

    List<EvaluacionProveedor> listarPorProveedor(int proveedorId) throws SQLException;

    EvaluacionProveedor buscarPorId(int id) throws SQLException;

    /** Corrige calidad, puntualidad y comentario. */
    int actualizar(EvaluacionProveedor evaluacion) throws SQLException;

    int eliminar(int id) throws SQLException;
}
