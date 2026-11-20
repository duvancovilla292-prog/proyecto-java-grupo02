/*
 * ============================================================
 * Archivo: ReporteDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de ReporteDAO: consultas de resumen
 *            con JOIN, GROUP BY y funciones de agregación.
 * Conceptos clave: PATRÓN DAO, try-with-resources (Connection,
 *            PreparedStatement, ResultSet), PreparedStatement con parámetros
 *            (evita inyección SQL), ResultSet.next() en un while para
 *            convertir cada fila en un objeto del modelo (S18Demo01 UsuarioDAO).
 * Justificación: MySQL AGRUPA y SUMA (COUNT, SUM, LEFT JOIN, GROUP BY) y
 *            devuelve una fila por tipo, técnico, repuesto o proveedor; así no
 *            se traen tablas completas a memoria. Las REGLAS de negocio
 *            (orden del ranking, top N, a quién notificar) quedan en
 *            ReporteServicio. Los ids de estados y tipos se toman de los enum
 *            para no repetir números mágicos.
 *            Los montos se truncan a pesos enteros (TRUNCATE) igual que el
 *            resto de la aplicación, que trabaja con int.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.dao.jdbc;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.ReporteDAO;
import com.carmotors.modelo.Cliente;
import com.carmotors.modelo.EstadoReserva;
import com.carmotors.modelo.EstadoServicio;
import com.carmotors.modelo.Proveedor;
import com.carmotors.modelo.Tecnico;
import com.carmotors.modelo.TipoMantenimiento;
import com.carmotors.modelo.Vehiculo;
import com.carmotors.modelo.reporte.AlertaStock;
import com.carmotors.modelo.reporte.CalendarioPreventivo;
import com.carmotors.modelo.reporte.ConsumoRepuesto;
import com.carmotors.modelo.reporte.DesempenoProveedor;
import com.carmotors.modelo.reporte.LotePorVencer;
import com.carmotors.modelo.reporte.ProductividadTecnico;
import com.carmotors.modelo.reporte.ResumenDescripcion;
import com.carmotors.modelo.reporte.ResumenTipoServicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
public class ReporteDAOJdbc implements ReporteDAO {
    // Estados con el trabajo técnico terminado (ver EstadoServicio.trabajoTerminado).
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: enum con atributos, values() y == -> S10Demo02 DiaSemana y S10Demo02EnumMain (Sesión 10)
    private static final String TERMINADOS = "(" + EstadoServicio.COMPLETADO.getId() + ", "
            + EstadoServicio.FACTURADO.getId() + ", " + EstadoServicio.ENTREGADO.getId() + ")";
    private static final String DEDUCIDO = "'" + EstadoReserva.DEDUCIDO.name() + "'";
    private static final String RESERVADO = "'" + EstadoReserva.RESERVADO.name() + "'";

    private static final String SQL_POR_TIPO =
            "SELECT mt.id AS tipo_id, COUNT(s.id) AS solicitudes, "
            + "COALESCE(SUM(CASE WHEN s.estado_id IN " + TERMINADOS
            + " THEN TRUNCATE(s.costo_mano_obra, 0) END), 0) AS mano_obra, "
            + "(SELECT COALESCE(SUM(sr.cantidad_usada * TRUNCATE(sr.precio_unitario_venta, 0)), 0) "
            + " FROM servicios_repuestos sr JOIN servicios so ON so.id = sr.servicio_id "
            + " WHERE so.mantenimiento_tipo_id = mt.id AND sr.estado = " + DEDUCIDO + ") AS venta_repuestos "
            + "FROM mantenimiento_tipos mt LEFT JOIN servicios s ON s.mantenimiento_tipo_id = mt.id "
            + "GROUP BY mt.id ORDER BY mt.id";

    // COLLATE utf8mb4_bin: agrupa solo por mayúsculas/minúsculas (LOWER), sin confundir tildes.
    private static final String SQL_POR_DESCRIPCION =
            "SELECT LOWER(descripcion_problema) COLLATE utf8mb4_bin AS descripcion, COUNT(*) AS cantidad "
            + "FROM servicios WHERE descripcion_problema IS NOT NULL "
            + "GROUP BY LOWER(descripcion_problema) COLLATE utf8mb4_bin";

    // La mano de obra de cada orden se reparte en partes iguales (DIV = división entera) entre sus técnicos.
    private static final String SQL_PRODUCTIVIDAD =
            "SELECT t.id, t.nombre, t.especialidad, t.activo, "
            + "COUNT(CASE WHEN s.estado_id IN " + TERMINADOS + " THEN 1 END) AS terminadas, "
            + "COUNT(s.id) - COUNT(CASE WHEN s.estado_id IN " + TERMINADOS + " THEN 1 END) AS en_curso, "
            + "COALESCE(SUM(CASE WHEN s.estado_id IN " + TERMINADOS + " THEN "
            + " TRUNCATE(COALESCE(s.costo_mano_obra, 0), 0) DIV "
            + " (SELECT COUNT(*) FROM servicios_tecnicos x WHERE x.servicio_id = s.id) END), 0) AS mano_obra "
            + "FROM tecnicos t "
            + "LEFT JOIN servicios_tecnicos st ON st.tecnico_id = t.id "
            + "LEFT JOIN servicios s ON s.id = st.servicio_id "
            + "GROUP BY t.id, t.nombre, t.especialidad, t.activo ORDER BY t.id";

    private static final String SQL_CONSUMO =
            "SELECT r.id, r.nombre, TRUNCATE(COALESCE(r.precio_compra, 0), 0) AS costo, "
            + "COALESCE(SUM(CASE WHEN sr.estado = " + DEDUCIDO + " THEN sr.cantidad_usada END), 0) AS usadas, "
            + "COALESCE(SUM(CASE WHEN sr.estado = " + RESERVADO + " THEN sr.cantidad_usada END), 0) AS reservadas, "
            + "COALESCE(SUM(CASE WHEN sr.estado = " + DEDUCIDO
            + " THEN sr.cantidad_usada * TRUNCATE(sr.precio_unitario_venta, 0) END), 0) AS ingreso "
            + "FROM repuestos r LEFT JOIN servicios_repuestos sr ON sr.repuesto_id = r.id "
            + "GROUP BY r.id, r.nombre, r.precio_compra ORDER BY r.id";

    private static final String SQL_BAJO_MINIMO =
            "SELECT id, nombre, cantidad_stock, cantidad_reservada, stock_minimo FROM repuestos "
            + "WHERE cantidad_stock - cantidad_reservada < stock_minimo ORDER BY id";

    private static final String SQL_POR_VENCER =
            "SELECT l.codigo, r.nombre AS repuesto, l.fecha_caducidad, l.cantidad "
            + "FROM lotes l JOIN repuestos r ON r.id = l.repuesto_id "
            + "WHERE l.fecha_caducidad IS NOT NULL AND l.fecha_caducidad <= DATE_ADD(CURDATE(), INTERVAL ? DAY) "
            + "ORDER BY l.fecha_caducidad";

    // MySQL calcula las fechas: DATE() quita la hora, DATE_ADD suma meses o días y CURDATE() es hoy.
    // EXISTS marca si el vehículo ya tiene un preventivo abierto (pendiente o en proceso).
    private static final String SQL_CALENDARIO =
            "SELECT v.id AS vehiculo_id, v.cliente_id, v.placa, v.marca, v.modelo, "
            + "c.nombre, c.documento_identidad, c.telefono, c.email, "
            + "DATE(MAX(s.fecha_recepcion)) AS ultimo, "
            + "DATE_ADD(DATE(MAX(s.fecha_recepcion)), INTERVAL ? MONTH) AS proximo, "
            + "CURDATE() AS hoy, DATE_ADD(CURDATE(), INTERVAL ? DAY) AS limite, "
            + "EXISTS (SELECT 1 FROM servicios a WHERE a.vehiculo_id = v.id "
            + " AND a.mantenimiento_tipo_id = " + TipoMantenimiento.PREVENTIVO.getId()
            + " AND a.estado_id NOT IN " + TERMINADOS + ") AS orden_abierta "
            + "FROM servicios s JOIN vehiculos v ON v.id = s.vehiculo_id JOIN clientes c ON c.id = v.cliente_id "
            + "WHERE s.mantenimiento_tipo_id = " + TipoMantenimiento.PREVENTIVO.getId()
            + " AND s.estado_id IN " + TERMINADOS + " "
            + "GROUP BY v.id, v.cliente_id, v.placa, v.marca, v.modelo, "
            + "c.nombre, c.documento_identidad, c.telefono, c.email "
            + "ORDER BY proximo";

    private static final String SQL_DESEMPENO =
            "SELECT p.id, p.nombre, p.contacto, p.telefono, p.email, COUNT(e.id) AS evaluaciones, "
            + "COALESCE(SUM(e.calidad), 0) AS suma_calidad, COALESCE(SUM(e.puntualidad), 0) AS suma_puntualidad "
            + "FROM proveedores p LEFT JOIN evaluaciones_proveedor e ON e.proveedor_id = p.id "
            + "GROUP BY p.id, p.nombre, p.contacto, p.telefono, p.email ORDER BY p.id";

    private final ConexionFactory conexiones;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public ReporteDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    // Visto en clase: List / ArrayList -> S08Demo01ArrayListMain (Sesión 08)
    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    @Override
    public List<ResumenTipoServicio> resumenPorTipo() throws SQLException {
        List<ResumenTipoServicio> resultado = new ArrayList<>();
        // Visto en clase: JDBC: Connection, PreparedStatement, ResultSet, try-with-resources -> S18Demo01 UsuarioDAO (Sesión 18)
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_POR_TIPO);
             ResultSet fila = sentencia.executeQuery()) {
            // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
            while (fila.next()) {
                resultado.add(new ResumenTipoServicio(TipoMantenimiento.desdeId(fila.getInt("tipo_id")),
                        fila.getInt("solicitudes"), fila.getInt("mano_obra"), fila.getInt("venta_repuestos")));
            }
        }
        return resultado;
    }

    @Override
    public List<ResumenDescripcion> resumenPorDescripcion() throws SQLException {
        List<ResumenDescripcion> resultado = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_POR_DESCRIPCION);
             ResultSet fila = sentencia.executeQuery()) {
            while (fila.next()) {
                resultado.add(new ResumenDescripcion(fila.getString("descripcion"), fila.getInt("cantidad")));
            }
        }
        return resultado;
    }

    @Override
    public List<ProductividadTecnico> productividadTecnicos() throws SQLException {
        List<ProductividadTecnico> resultado = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_PRODUCTIVIDAD);
             ResultSet fila = sentencia.executeQuery()) {
            while (fila.next()) {
                Tecnico tecnico = new Tecnico(fila.getInt("id"), fila.getString("nombre"),
                        fila.getString("especialidad"), fila.getBoolean("activo"));
                resultado.add(new ProductividadTecnico(tecnico, fila.getInt("terminadas"), fila.getInt("en_curso"),
                        fila.getInt("mano_obra")));
            }
        }
        return resultado;
    }

    @Override
    public List<ConsumoRepuesto> consumoRepuestos() throws SQLException {
        List<ConsumoRepuesto> resultado = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSUMO);
             ResultSet fila = sentencia.executeQuery()) {
            while (fila.next()) {
                resultado.add(new ConsumoRepuesto(fila.getInt("id"), fila.getString("nombre"), fila.getInt("costo"),
                        fila.getInt("usadas"), fila.getInt("reservadas"), fila.getInt("ingreso")));
            }
        }
        return resultado;
    }

    @Override
    public List<AlertaStock> repuestosBajoMinimo() throws SQLException {
        List<AlertaStock> resultado = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BAJO_MINIMO);
             ResultSet fila = sentencia.executeQuery()) {
            while (fila.next()) {
                resultado.add(new AlertaStock(fila.getInt("id"), fila.getString("nombre"),
                        fila.getInt("cantidad_stock"), fila.getInt("cantidad_reservada"), fila.getInt("stock_minimo")));
            }
        }
        return resultado;
    }

    @Override
    public List<LotePorVencer> lotesPorVencer(int dias) throws SQLException {
        List<LotePorVencer> resultado = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_POR_VENCER)) {
            sentencia.setInt(1, dias);
            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    resultado.add(new LotePorVencer(fila.getString("codigo"), fila.getString("repuesto"),
                            fila.getString("fecha_caducidad"), fila.getInt("cantidad")));
                }
            }
        }
        return resultado;
    }

    @Override
    public List<CalendarioPreventivo> calendarioPreventivos(int meses, int dias) throws SQLException {
        List<CalendarioPreventivo> resultado = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CALENDARIO)) {
            sentencia.setInt(1, meses);
            sentencia.setInt(2, dias);
            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    Cliente cliente = new Cliente(fila.getInt("cliente_id"), fila.getString("nombre"),
                            fila.getString("documento_identidad"), fila.getString("telefono"), fila.getString("email"));
                    Vehiculo vehiculo = new Vehiculo(fila.getInt("vehiculo_id"), fila.getInt("cliente_id"),
                            fila.getString("placa"), fila.getString("marca"), fila.getString("modelo"));
                    resultado.add(new CalendarioPreventivo(cliente, vehiculo, fila.getString("ultimo"),
                            fila.getString("proximo"), fila.getString("hoy"), fila.getString("limite"),
                            fila.getBoolean("orden_abierta")));
                }
            }
        }
        return resultado;
    }

    @Override
    public List<DesempenoProveedor> desempenoProveedores() throws SQLException {
        List<DesempenoProveedor> resultado = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_DESEMPENO);
             ResultSet fila = sentencia.executeQuery()) {
            while (fila.next()) {
                int cantidad = fila.getInt("evaluaciones");
                // Cast a double (S08Demo00) para obtener el promedio con decimales.
                // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
                // Visto en clase: conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
                double calidad = cantidad == 0 ? 0 : (double) fila.getInt("suma_calidad") / cantidad;
                double puntualidad = cantidad == 0 ? 0 : (double) fila.getInt("suma_puntualidad") / cantidad;
                Proveedor proveedor = new Proveedor(fila.getInt("id"), fila.getString("nombre"),
                        fila.getString("contacto"), fila.getString("telefono"), fila.getString("email"));
                resultado.add(new DesempenoProveedor(proveedor, cantidad, calidad, puntualidad));
            }
        }
        return resultado;
    }
}
