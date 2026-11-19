/*
 * ============================================================
 * Archivo: ClienteDAO.java
 * Propósito: contrato de acceso a datos de la tabla clientes.
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
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.dao;

import com.carmotors.modelo.Cliente;

import java.sql.SQLException;
import java.util.List;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
public interface ClienteDAO {
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    /** Inserta y devuelve el id generado por MySQL. */
    int insertar(Cliente cliente) throws SQLException;

    /** Actualiza nombre, teléfono y email; devuelve filas afectadas. */
    int actualizar(Cliente cliente) throws SQLException;

    Cliente buscarPorId(int id) throws SQLException;

    Cliente buscarPorDocumento(String documento) throws SQLException;

    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    List<Cliente> listar() throws SQLException;

    /** Elimina el cliente; devuelve filas afectadas (0 si no existía). */
    int eliminar(int id) throws SQLException;
}
