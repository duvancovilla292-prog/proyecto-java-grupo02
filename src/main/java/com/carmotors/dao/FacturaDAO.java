/*
 * ============================================================
 * Archivo: FacturaDAO.java
 * Propósito: contrato de acceso a datos de la tabla facturas.
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

import com.carmotors.modelo.Factura;

import java.sql.SQLException;
import java.util.List;

public interface FacturaDAO {
    int insertar(Factura factura) throws SQLException;

    Factura buscarPorServicio(int servicioId) throws SQLException;

    List<Factura> listar() throws SQLException;

    /** Actualiza los totales de una factura existente. */
    int actualizar(Factura factura) throws SQLException;

    int eliminar(int id) throws SQLException;
}
