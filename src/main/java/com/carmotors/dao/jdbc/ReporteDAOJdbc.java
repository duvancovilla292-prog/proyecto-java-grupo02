/*
 * ============================================================
 * Archivo: ReporteDAOJdbc.java
 * Propósito: implementación JDBC/MySQL de ReporteDAO: consultas de resumen
 *            con JOIN, GROUP BY y funciones de agregación.
 * Conceptos clave: PATRÓN DAO, try-with-resources (Connection,
 *            PreparedStatement, ResultSet), PreparedStatement con parámetros
 *            (evita inyección SQL), interfaz funcional propia + lambdas
 *            (sesión 19) para convertir cada fila en un objeto del modelo.
 * Justificación: MySQL AGRUPA y SUMA (COUNT, SUM, LEFT JOIN, GROUP BY) y
 *            devuelve una fila por tipo, técnico, repuesto o proveedor; así no
 *            se traen tablas completas a memoria. Las REGLAS de negocio
 *            (orden del ranking, top N, a quién notificar) quedan en
 *            ReporteServicio. Los ids de estados y tipos se toman de los enum
 *            para no repetir números mágicos.
 *            Los montos se truncan a pesos enteros (TRUNCATE) igual que el
 *            resto de la aplicación, que trabaja con int.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - interfaces e implements -> S07Demo04 Notificable (Sesión 07)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - enum con atributos, values() y == -> S10Demo02 DiaSemana y S10Demo02EnumMain (Sesión 10)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
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

public class ReporteDAOJdbc implements ReporteDAO {
    // Estados con el trabajo técnico terminado (ver EstadoServicio.trabajoTerminado).
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

    /** Convierte la fila actual del ResultSet en un objeto (se implementa con una lambda). */
    private interface LectorFila<T> {
        T leer(ResultSet fila) throws SQLException;
    }

    private final ConexionFactory conexiones;

    public ReporteDAOJdbc(ConexionFactory conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public List<ResumenTipoServicio> resumenPorTipo() throws SQLException {
        return consultar(SQL_POR_TIPO, fila -> new ResumenTipoServicio(
                TipoMantenimiento.desdeId(fila.getInt("tipo_id")), fila.getInt("solicitudes"),
                fila.getInt("mano_obra"), fila.getInt("venta_repuestos")));
    }

    @Override
    public List<ResumenDescripcion> resumenPorDescripcion() throws SQLException {
        return consultar(SQL_POR_DESCRIPCION,
                fila -> new ResumenDescripcion(fila.getString("descripcion"), fila.getInt("cantidad")));
    }

    @Override
    public List<ProductividadTecnico> productividadTecnicos() throws SQLException {
        return consultar(SQL_PRODUCTIVIDAD, fila -> new ProductividadTecnico(
                new Tecnico(fila.getInt("id"), fila.getString("nombre"), fila.getString("especialidad"),
                        fila.getBoolean("activo")),
                fila.getInt("terminadas"), fila.getInt("en_curso"), fila.getInt("mano_obra")));
    }

    @Override
    public List<ConsumoRepuesto> consumoRepuestos() throws SQLException {
        return consultar(SQL_CONSUMO, fila -> new ConsumoRepuesto(fila.getInt("id"), fila.getString("nombre"),
                fila.getInt("costo"), fila.getInt("usadas"), fila.getInt("reservadas"), fila.getInt("ingreso")));
    }

    @Override
    public List<AlertaStock> repuestosBajoMinimo() throws SQLException {
        return consultar(SQL_BAJO_MINIMO, fila -> new AlertaStock(fila.getInt("id"), fila.getString("nombre"),
                fila.getInt("cantidad_stock"), fila.getInt("cantidad_reservada"), fila.getInt("stock_minimo")));
    }

    @Override
    public List<LotePorVencer> lotesPorVencer(int dias) throws SQLException {
        return consultar(SQL_POR_VENCER, fila -> new LotePorVencer(fila.getString("codigo"),
                fila.getString("repuesto"), fila.getString("fecha_caducidad"), fila.getInt("cantidad")), dias);
    }

    @Override
    public List<CalendarioPreventivo> calendarioPreventivos(int meses, int dias) throws SQLException {
        return consultar(SQL_CALENDARIO, fila -> new CalendarioPreventivo(
                new Cliente(fila.getInt("cliente_id"), fila.getString("nombre"),
                        fila.getString("documento_identidad"), fila.getString("telefono"), fila.getString("email")),
                new Vehiculo(fila.getInt("vehiculo_id"), fila.getInt("cliente_id"), fila.getString("placa"),
                        fila.getString("marca"), fila.getString("modelo")),
                fila.getString("ultimo"), fila.getString("proximo"), fila.getString("hoy"),
                fila.getString("limite"), fila.getBoolean("orden_abierta")), meses, dias);
    }

    @Override
    public List<DesempenoProveedor> desempenoProveedores() throws SQLException {
        return consultar(SQL_DESEMPENO, fila -> {
            int cantidad = fila.getInt("evaluaciones");
            // Cast a double (S08Demo00) para obtener el promedio con decimales.
            double calidad = cantidad == 0 ? 0 : (double) fila.getInt("suma_calidad") / cantidad;
            double puntualidad = cantidad == 0 ? 0 : (double) fila.getInt("suma_puntualidad") / cantidad;
            return new DesempenoProveedor(new Proveedor(fila.getInt("id"), fila.getString("nombre"),
                    fila.getString("contacto"), fila.getString("telefono"), fila.getString("email")),
                    cantidad, calidad, puntualidad);
        });
    }

    // Ejecuta la consulta con sus parámetros enteros (en orden) y convierte cada fila con el lector.
    private <T> List<T> consultar(String sql, LectorFila<T> lector, int... parametros) throws SQLException {
        List<T> resultado = new ArrayList<>();
        try (Connection conexion = conexiones.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                sentencia.setInt(i + 1, parametros[i]);
            }
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    resultado.add(lector.leer(filas));
                }
            }
        }
        return resultado;
    }
}
