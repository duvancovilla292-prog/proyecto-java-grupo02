/*
 * ============================================================
 * Archivo: OrdenServicioDAO.java
 * Propósito: contrato de acceso a datos de la tabla servicios (órdenes).
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
 * Visto en clase (demos del curso; cada concepto también se señala con
 *   un comentario "// Visto en clase" junto al código donde se usa):
 *   - funciones de MySQL en el SQL (NOW, CURDATE, DATE_ADD, LAST_INSERT_ID) -> son SQL de MySQL, no Java; en Java solo se vio LocalDate.of (S12Demo02)
 * ============================================================
 */
package com.carmotors.dao;

import com.carmotors.modelo.EstadoServicio;
import com.carmotors.modelo.OrdenServicio;

import java.sql.SQLException;
import java.util.List;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
public interface OrdenServicioDAO {
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    int insertar(OrdenServicio orden) throws SQLException;

    OrdenServicio buscarPorId(int id) throws SQLException;

    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    List<OrdenServicio> listar() throws SQLException;

    List<OrdenServicio> listarPorVehiculo(int vehiculoId) throws SQLException;

    /** Cambia el estado de la orden. */
    int actualizarEstado(int id, EstadoServicio estado) throws SQLException;

    /** Marca la orden como Entregada y registra la fecha de entrega (NOW() de MySQL). */
    int registrarEntrega(int id) throws SQLException;

    /** Actualiza tipo de mantenimiento, descripción y costo de mano de obra. */
    int actualizar(OrdenServicio orden) throws SQLException;

    int eliminar(int id) throws SQLException;
}
