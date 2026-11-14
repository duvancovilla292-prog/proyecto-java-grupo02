/*
 * ============================================================
 * Archivo: AsignacionTecnicoDAO.java
 * Propósito: contrato de acceso a la tabla de unión servicios_tecnicos.
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

import com.carmotors.modelo.AsignacionTecnico;
import com.carmotors.modelo.Tecnico;

import java.sql.SQLException;
import java.util.List;

public interface AsignacionTecnicoDAO {
    int insertar(AsignacionTecnico asignacion) throws SQLException;

    List<AsignacionTecnico> listar() throws SQLException;

    /** Consulta específica con JOIN: técnicos asignados a una orden. */
    List<Tecnico> listarTecnicosPorServicio(int servicioId) throws SQLException;

    /** Reemplaza el técnico de una asignación (la clave es compuesta). */
    int actualizar(int servicioId, int tecnicoAnterior, int tecnicoNuevo) throws SQLException;

    int eliminar(int servicioId, int tecnicoId) throws SQLException;
}
