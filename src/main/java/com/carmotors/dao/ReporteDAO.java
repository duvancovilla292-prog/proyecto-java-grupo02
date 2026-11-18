/*
 * ============================================================
 * Archivo: ReporteDAO.java
 * Propósito: contrato de acceso a datos del Módulo de Reportes: consultas
 *            de solo lectura que agrupan y resumen información de varias
 *            tablas (servicios, técnicos, repuestos, lotes, proveedores...).
 * Conceptos clave: PATRÓN DAO (S18Demo01 UsuarioDAO) definido como
 *            interfaz (sesión 07 Notificable), List genérica (sesión 08),
 *            SQLException verificada (sesión 10).
 * Justificación: los reportes no son una entidad, sino CONSULTAS DE RESUMEN.
 *            Tenerlas en su propio DAO evita mezclar en los DAO de cada
 *            entidad consultas que solo usan los reportes (Responsabilidad
 *            Única) y permite que ReporteServicio dependa de UNA sola
 *            abstracción (Inversión de Dependencias). Cada método devuelve
 *            objetos del paquete modelo.reporte.
 * Visto en clase (demos del curso):
 *   - interfaces e implements -> S07Demo04 Notificable (Sesión 07)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
 * ============================================================
 */
package com.carmotors.dao;

import com.carmotors.modelo.reporte.AlertaStock;
import com.carmotors.modelo.reporte.CalendarioPreventivo;
import com.carmotors.modelo.reporte.ConsumoRepuesto;
import com.carmotors.modelo.reporte.DesempenoProveedor;
import com.carmotors.modelo.reporte.LotePorVencer;
import com.carmotors.modelo.reporte.ProductividadTecnico;
import com.carmotors.modelo.reporte.ResumenDescripcion;
import com.carmotors.modelo.reporte.ResumenTipoServicio;

import java.sql.SQLException;
import java.util.List;

public interface ReporteDAO {
    /** Por tipo de mantenimiento: órdenes, mano de obra terminada y venta de repuestos deducidos. */
    List<ResumenTipoServicio> resumenPorTipo() throws SQLException;

    /** Cantidad de órdenes por descripción del trabajo (sin distinguir mayúsculas). */
    List<ResumenDescripcion> resumenPorDescripcion() throws SQLException;

    /** Por técnico: órdenes terminadas, en curso y mano de obra repartida. */
    List<ProductividadTecnico> productividadTecnicos() throws SQLException;

    /** Por repuesto: unidades usadas, reservadas e ingreso por las usadas. */
    List<ConsumoRepuesto> consumoRepuestos() throws SQLException;

    /** Repuestos cuyo disponible (stock - reservado) está por debajo del mínimo. */
    List<AlertaStock> repuestosBajoMinimo() throws SQLException;

    /** Lotes que caducan desde hoy hasta hoy + dias (o ya caducaron). */
    List<LotePorVencer> lotesPorVencer(int dias) throws SQLException;

    /** Por vehículo: último preventivo terminado, próximo (+meses), hoy y límite de aviso (+dias). */
    List<CalendarioPreventivo> calendarioPreventivos(int meses, int dias) throws SQLException;

    /** Por proveedor: cantidad de evaluaciones y promedios de calidad y puntualidad. */
    List<DesempenoProveedor> desempenoProveedores() throws SQLException;
}
