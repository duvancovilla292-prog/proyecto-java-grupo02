/*
 * ============================================================
 * Archivo: RepuestoDAO.java
 * Propósito: contrato de acceso a datos de la tabla repuestos.
 * Conceptos clave: PATRÓN DAO (S18Demo01 UsuarioDAO) definido como
 *            interfaz (sesión 07 Notificable), List genérica (sesión 08),
 *            SQLException verificada (sesión 10).
 * Justificación: los servicios dependen de esta ABSTRACCIÓN y no de JDBC
 *            (Inversión de Dependencias, la "D" de SOLID). Cada DAO atiende
 *            UNA sola entidad (Responsabilidad Única) y expone solo los
 *            métodos que sus clientes usan (Segregación de Interfaces).
 *            Cada DAO ofrece el CRUD completo (insertar, buscar/listar,
 *            actualizar, eliminar) más sus consultas específicas.
 *            actualizarCantidades es la única forma de mover stock y
 *            reservas: así las reglas quedan en el servicio de inventario.
 *            Convención: buscarPor... devuelve null si no hay resultado
 *            (como Grupo.obtener de S08Demo04); el llamador lo comprueba.
 * ============================================================
 */
package com.carmotors.dao;

import com.carmotors.modelo.Repuesto;

import java.sql.SQLException;
import java.util.List;

public interface RepuestoDAO {
    int insertar(Repuesto repuesto) throws SQLException;

    /** Actualiza nombre, tipo, marca, proveedor, precios y stock mínimo (no toca cantidades). */
    int actualizarFicha(Repuesto repuesto) throws SQLException;

    /** Escribe las nuevas cantidades de stock físico y reservado. */
    int actualizarCantidades(int id, int cantidadStock, int cantidadReservada) throws SQLException;

    Repuesto buscarPorId(int id) throws SQLException;

    List<Repuesto> listar() throws SQLException;

    int eliminar(int id) throws SQLException;
}
