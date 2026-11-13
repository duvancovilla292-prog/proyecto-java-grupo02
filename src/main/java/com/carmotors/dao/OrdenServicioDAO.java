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
 * ============================================================
 */
package com.carmotors.dao;

import com.carmotors.modelo.CalendarioPreventivo;
import com.carmotors.modelo.EstadoServicio;
import com.carmotors.modelo.OrdenServicio;

import java.sql.SQLException;
import java.util.List;

public interface OrdenServicioDAO {
    int insertar(OrdenServicio orden) throws SQLException;

    OrdenServicio buscarPorId(int id) throws SQLException;

    List<OrdenServicio> listar() throws SQLException;

    List<OrdenServicio> listarPorVehiculo(int vehiculoId) throws SQLException;

    /** Cambia el estado de la orden. */
    int actualizarEstado(int id, EstadoServicio estado) throws SQLException;

    /** Marca la orden como Entregada y registra la fecha de entrega (NOW() de MySQL). */
    int registrarEntrega(int id) throws SQLException;

    /**
     * Consulta específica: por vehículo, fecha del último preventivo terminado,
     * próximo sugerido (+meses), hoy y límite de aviso (+dias), calculadas por MySQL.
     */
    List<CalendarioPreventivo> calendarioPreventivos(int meses, int dias) throws SQLException;

    /** Actualiza tipo de mantenimiento, descripción y costo de mano de obra. */
    int actualizar(OrdenServicio orden) throws SQLException;

    int eliminar(int id) throws SQLException;
}
