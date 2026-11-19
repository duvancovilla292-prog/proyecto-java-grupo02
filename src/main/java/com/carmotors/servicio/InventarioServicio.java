/*
 * ============================================================
 * Archivo: InventarioServicio.java
 * Propósito: lógica de negocio del Módulo de Gestión de Inventario:
 *            registro de repuestos, ingreso de lotes, búsqueda, alertas en
 *            tiempo real y las reglas RESERVAR / DEDUCIR stock (el reporte
 *            de alertas está en ReporteServicio).
 * Conceptos clave: capa de servicio, inyección de dependencias por
 *            constructor, Streams (filter, sorted, toList - sesión 20),
 *            PATRÓN OBSERVER (publica alertas).
 * Justificación: es la única clase que modifica cantidades de stock.
 *            Las órdenes de servicio no tocan RepuestoDAO directamente: piden
 *            reservar/deducir aquí, de modo que la regla vive en un solo lugar (SRP).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN OBSERVER -> S14Demo01 PublicadorPedidos (Sesión 14)
 *   - PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 *   - Predicate<T> / Comparator<T> / compareTo -> S19Demo03ReglasOrdenMain (Sesión 19)
 *   - referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
 *   - Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
 *   - Stream: count / anyMatch / Arrays.stream -> S20Demo01FuentesMain (Sesión 20)
 *   - Stream: distinct / sorted / mapToInt+sum / reduce(0, Integer::sum) -> S20Demo02OrdenesMain (Sesión 20)
 *   - funciones de MySQL en el SQL (NOW, CURDATE, DATE_ADD, LAST_INSERT_ID) -> son SQL de MySQL, no Java; en Java solo se vio LocalDate.of (S12Demo02)
 * ============================================================
 */
package com.carmotors.servicio;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.jdbc.LoteDAOJdbc;
import com.carmotors.dao.jdbc.ProveedorDAOJdbc;
import com.carmotors.dao.jdbc.RepuestoDAOJdbc;
import com.carmotors.dao.jdbc.TipoRepuestoDAOJdbc;
import com.carmotors.dao.LoteDAO;
import com.carmotors.dao.ProveedorDAO;
import com.carmotors.dao.RepuestoDAO;
import com.carmotors.dao.TipoRepuestoDAO;
import com.carmotors.excepcion.DatoInvalidoException;
import com.carmotors.excepcion.RecursoNoEncontradoException;
import com.carmotors.excepcion.ReglaNegocioException;
import com.carmotors.modelo.Lote;
import com.carmotors.modelo.Repuesto;
import com.carmotors.modelo.TipoRepuesto;
import com.carmotors.servicio.observador.PublicadorInventario;

import java.sql.SQLException;
import java.util.List;

public class InventarioServicio {
    private final RepuestoDAO repuestoDAO;
    private final TipoRepuestoDAO tipoDAO;
    private final LoteDAO loteDAO;
    private final ProveedorDAO proveedorDAO;
    private final PublicadorInventario publicador; // sujeto del patrón Observer

    /** Crea sus propios DAO JDBC y usa el publicador único de alertas (Observer). */
    public InventarioServicio() {
        this.repuestoDAO = new RepuestoDAOJdbc(ConexionFactory.getInstancia());
        this.tipoDAO = new TipoRepuestoDAOJdbc(ConexionFactory.getInstancia());
        this.loteDAO = new LoteDAOJdbc(ConexionFactory.getInstancia());
        this.proveedorDAO = new ProveedorDAOJdbc(ConexionFactory.getInstancia());
        this.publicador = PublicadorInventario.getInstancia();
    }

    public List<TipoRepuesto> listarTipos() throws SQLException {
        return tipoDAO.listar();
    }

    /**
     * Registra la ficha de un repuesto con stock inicial 0.
     * El stock solo entra mediante lotes, para que todo ingreso sea trazable.
     */
    public int registrarRepuesto(String nombre, int tipoId, String marca, int proveedorId, int precioCompra,
                                 int precioVenta, int stockMinimo) throws SQLException {
        if (tipoDAO.buscarPorId(tipoId) == null) {
            throw new RecursoNoEncontradoException("No existe el tipo de repuesto " + tipoId + ".");
        }
        if (proveedorDAO.buscarPorId(proveedorId) == null) {
            throw new RecursoNoEncontradoException("No existe el proveedor " + proveedorId + ".");
        }
        Repuesto nuevo = new Repuesto(0, nombre, tipoId, "", marca, proveedorId, precioCompra, precioVenta,
                0, stockMinimo, 0);
        return repuestoDAO.insertar(nuevo);
    }

    /** Actualiza precios y stock mínimo conservando las cantidades actuales. */
    public void actualizarPrecios(int repuestoId, int precioCompra, int precioVenta, int stockMinimo)
            throws SQLException {
        Repuesto actual = obtenerRepuesto(repuestoId);
        Repuesto modificado = new Repuesto(actual.getId(), actual.getNombre(), actual.getTipoId(),
                actual.getNombreTipo(), actual.getMarca(), actual.getProveedorId(), precioCompra, precioVenta,
                actual.getCantidadStock(), stockMinimo, actual.getCantidadReservada());
        repuestoDAO.actualizarFicha(modificado);
    }

    /**
     * Ingresa un lote: guarda la trazabilidad (código, proveedor, fecha) y
     * suma la cantidad al stock físico del repuesto.
     */
    public int registrarLote(String codigo, int repuestoId, int proveedorId, int cantidad,
                             String fechaCaducidad) throws SQLException {
        Repuesto repuesto = obtenerRepuesto(repuestoId);
        if (proveedorDAO.buscarPorId(proveedorId) == null) {
            throw new RecursoNoEncontradoException("No existe el proveedor " + proveedorId + ".");
        }
        if (codigo != null && loteDAO.buscarPorCodigo(codigo) != null) {
            throw new ReglaNegocioException("Ya existe un lote con el código " + codigo.strip().toUpperCase() + ".");
        }
        Lote lote = new Lote(0, codigo, repuestoId, proveedorId, null, fechaCaducidad, cantidad);  // fecha de ingreso: CURDATE() en MySQL
        int id = loteDAO.insertar(lote);
        repuestoDAO.actualizarCantidades(repuestoId, repuesto.getCantidadStock() + cantidad,
                repuesto.getCantidadReservada());
        return id;
    }

    public List<Repuesto> listarRepuestos() throws SQLException {
        return repuestoDAO.listar();
    }

    /** Búsqueda por nombre, marca o tipo sin distinguir mayúsculas (Stream + filter). */
    public List<Repuesto> buscar(String texto) throws SQLException {
        String criterio = texto == null ? "" : texto.strip().toLowerCase();
        return repuestoDAO.listar().stream()
                .filter(r -> r.getNombre().toLowerCase().contains(criterio)
                        || r.getMarca().toLowerCase().contains(criterio)
                        || r.getNombreTipo().toLowerCase().contains(criterio))
                .toList();
    }

    public Repuesto obtenerRepuesto(int id) throws SQLException {
        Repuesto repuesto = repuestoDAO.buscarPorId(id);
        if (repuesto == null) {
            throw new RecursoNoEncontradoException("No existe el repuesto " + id + ".");
        }
        return repuesto;
    }

    public List<Lote> listarLotes(int repuestoId) throws SQLException {
        return loteDAO.listarPorRepuesto(repuestoId);
    }

    /** Comprueba que hay unidades libres SIN modificar nada (validación previa). */
    public void verificarDisponibilidad(int repuestoId, int cantidad) throws SQLException {
        if (cantidad <= 0) {
            throw new DatoInvalidoException("La cantidad debe ser positiva.");
        }
        Repuesto repuesto = obtenerRepuesto(repuestoId);
        if (repuesto.getDisponible() < cantidad) {
            throw new ReglaNegocioException("Stock insuficiente de " + repuesto.getNombre() + ": disponible "
                    + repuesto.getDisponible() + ", solicitado " + cantidad + ".");
        }
    }

    /**
     * REGLA CRÍTICA (1/2): al asignar un repuesto a una orden, las unidades
     * pasan a "Reservado": el stock físico no cambia, pero ya no están disponibles.
     */
    public void reservar(int repuestoId, int cantidad) throws SQLException {
        verificarDisponibilidad(repuestoId, cantidad);
        Repuesto actual = obtenerRepuesto(repuestoId);
        repuestoDAO.actualizarCantidades(repuestoId, actual.getCantidadStock(),
                actual.getCantidadReservada() + cantidad);
        avisarSiBajoMinimo(repuestoId);
    }

    /**
     * REGLA CRÍTICA (2/2): al completar la orden, las unidades reservadas se
     * descuentan PERMANENTEMENTE del stock físico y se liberan de la reserva.
     */
    public void deducirReservado(int repuestoId, int cantidad) throws SQLException {
        Repuesto actual = obtenerRepuesto(repuestoId);
        if (cantidad <= 0 || cantidad > actual.getCantidadReservada()) {
            throw new ReglaNegocioException("No hay " + cantidad + " unidades reservadas de " + actual.getNombre() + ".");
        }
        repuestoDAO.actualizarCantidades(repuestoId, actual.getCantidadStock() - cantidad,
                actual.getCantidadReservada() - cantidad);
        avisarSiBajoMinimo(repuestoId);
    }

    // Observer: publica la alerta si, tras el movimiento, quedó por debajo del mínimo.
    private void avisarSiBajoMinimo(int repuestoId) throws SQLException {
        Repuesto actualizado = obtenerRepuesto(repuestoId);
        if (actualizado.estaBajoMinimo()) {
            publicador.notificarStockBajo(actualizado);
        }
    }

    // ------------------------------------------------------------ CRUD de tipos de repuesto

    public int registrarTipo(String nombre) throws SQLException {
        TipoRepuesto nuevo = new TipoRepuesto(0, nombre);
        boolean existe = tipoDAO.listar().stream().anyMatch(t -> t.getNombre().equalsIgnoreCase(nuevo.getNombre()));
        if (existe) {
            throw new ReglaNegocioException("Ya existe el tipo " + nuevo.getNombre() + ".");
        }
        return tipoDAO.insertar(nuevo);
    }

    public void renombrarTipo(int id, String nombre) throws SQLException {
        obtenerTipo(id);
        tipoDAO.actualizar(new TipoRepuesto(id, nombre));
    }

    /** Solo se elimina un tipo que ningún repuesto usa. */
    public void eliminarTipo(int id) throws SQLException {
        obtenerTipo(id);
        if (repuestoDAO.listar().stream().anyMatch(r -> r.getTipoId() == id)) {
            throw new ReglaNegocioException("Hay repuestos de ese tipo; reasígnelos antes de eliminarlo.");
        }
        tipoDAO.eliminar(id);
    }

    private TipoRepuesto obtenerTipo(int id) throws SQLException {
        TipoRepuesto tipo = tipoDAO.buscarPorId(id);
        if (tipo == null) {
            throw new RecursoNoEncontradoException("No existe el tipo de repuesto " + id + ".");
        }
        return tipo;
    }

    // ------------------------------------------------------------ Eliminar / corregir

    /**
     * Elimina la ficha de un repuesto registrado por error: solo si nunca
     * tuvo existencias (sin lotes) y no tiene stock ni reservas.
     */
    public void eliminarRepuesto(int id) throws SQLException {
        Repuesto repuesto = obtenerRepuesto(id);
        if (repuesto.getCantidadStock() > 0 || repuesto.getCantidadReservada() > 0
                || !loteDAO.listarPorRepuesto(id).isEmpty()) {
            throw new ReglaNegocioException("El repuesto tiene existencias o lotes registrados; no se puede eliminar.");
        }
        repuestoDAO.eliminar(id);
    }

    /** Corrige la fecha de caducidad de un lote (null = no caduca). */
    public void actualizarCaducidadLote(String codigo, String fechaCaducidad) throws SQLException {
        Lote actual = obtenerLote(codigo);
        loteDAO.actualizar(new Lote(actual.getId(), actual.getCodigo(), actual.getRepuestoId(), actual.getProveedorId(),
                actual.getFechaIngreso(), fechaCaducidad, actual.getCantidad()));
    }

    /**
     * Anula un lote ingresado por error: resta su cantidad del stock.
     * No se permite si esas unidades ya están reservadas o consumidas.
     */
    public void eliminarLote(String codigo) throws SQLException {
        Lote lote = obtenerLote(codigo);
        Repuesto repuesto = obtenerRepuesto(lote.getRepuestoId());
        int nuevoStock = repuesto.getCantidadStock() - lote.getCantidad();
        if (nuevoStock < repuesto.getCantidadReservada()) {
            throw new ReglaNegocioException("Las unidades del lote ya se usaron o están reservadas; no se puede anular.");
        }
        loteDAO.eliminar(lote.getId());
        repuestoDAO.actualizarCantidades(repuesto.getId(), nuevoStock, repuesto.getCantidadReservada());
        avisarSiBajoMinimo(repuesto.getId());
    }

    private Lote obtenerLote(String codigo) throws SQLException {
        Lote lote = codigo == null ? null : loteDAO.buscarPorCodigo(codigo);
        if (lote == null) {
            throw new RecursoNoEncontradoException("No existe el lote " + codigo + ".");
        }
        return lote;
    }

    /** Devuelve unidades reservadas al disponible (se quitó el repuesto de una orden). */
    public void liberarReserva(int repuestoId, int cantidad) throws SQLException {
        Repuesto actual = obtenerRepuesto(repuestoId);
        if (cantidad <= 0 || cantidad > actual.getCantidadReservada()) {
            throw new ReglaNegocioException("No hay " + cantidad + " unidades reservadas de " + actual.getNombre() + ".");
        }
        repuestoDAO.actualizarCantidades(repuestoId, actual.getCantidadStock(), actual.getCantidadReservada() - cantidad);
    }
}
