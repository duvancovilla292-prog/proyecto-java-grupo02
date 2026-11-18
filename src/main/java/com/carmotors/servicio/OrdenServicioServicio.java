/*
 * ============================================================
 * Archivo: OrdenServicioServicio.java
 * Propósito: lógica del Módulo de Gestión de Servicios: crear órdenes,
 *            asignar técnicos y repuestos, y mover la orden por su ciclo
 *            de vida Pendiente -> En Proceso -> Completado -> Facturado -> Entregado.
 * Conceptos clave: PATRÓN BUILDER (crear la orden), PATRÓN STATE
 *            (validar transiciones), orquestación de varios DAO y de
 *            InventarioServicio, Streams (anyMatch, filter, toList).
 * Justificación: tal como pide el enunciado, al COMPLETAR una orden es este
 *            servicio quien llama al inventario para deducir el stock y al
 *            DAO de servicios para cambiar el estado. El controlador nunca
 *            coordina entidades; solo pide la operación.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN BUILDER -> S12Demo02 SolicitudBuilder (Sesión 12)
 *   - PATRÓN STATE -> S14Demo04 EstadoPedido (Sesión 14)
 *   - PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 *   - Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
 *   - Stream: count / anyMatch / Arrays.stream -> S20Demo01FuentesMain (Sesión 20)
 *   - funciones de MySQL en el SQL (NOW, CURDATE, DATE_ADD, LAST_INSERT_ID) -> son SQL de MySQL, no Java; en Java solo se vio LocalDate.of (S12Demo02)
 * ============================================================
 */
package com.carmotors.servicio;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.jdbc.AsignacionTecnicoDAOJdbc;
import com.carmotors.dao.jdbc.DetalleRepuestoDAOJdbc;
import com.carmotors.dao.jdbc.OrdenServicioDAOJdbc;
import com.carmotors.dao.jdbc.TecnicoDAOJdbc;
import com.carmotors.dao.jdbc.VehiculoDAOJdbc;
import com.carmotors.dao.AsignacionTecnicoDAO;
import com.carmotors.dao.DetalleRepuestoDAO;
import com.carmotors.dao.OrdenServicioDAO;
import com.carmotors.dao.TecnicoDAO;
import com.carmotors.dao.VehiculoDAO;
import com.carmotors.excepcion.RecursoNoEncontradoException;
import com.carmotors.excepcion.ReglaNegocioException;
import com.carmotors.modelo.AsignacionTecnico;
import com.carmotors.modelo.DetalleRepuesto;
import com.carmotors.modelo.EstadoReserva;
import com.carmotors.modelo.EstadoServicio;
import com.carmotors.modelo.OrdenServicio;
import com.carmotors.modelo.OrdenServicioBuilder;
import com.carmotors.modelo.Repuesto;
import com.carmotors.modelo.Tecnico;
import com.carmotors.modelo.TipoMantenimiento;
import com.carmotors.modelo.Vehiculo;
import com.carmotors.servicio.estado.EstadoOrden;
import com.carmotors.servicio.estado.FabricaEstadoOrden;

import java.sql.SQLException;
import java.util.List;

public class OrdenServicioServicio {
    private final OrdenServicioDAO ordenDAO;
    private final VehiculoDAO vehiculoDAO;
    private final TecnicoDAO tecnicoDAO;
    private final AsignacionTecnicoDAO asignacionDAO;
    private final DetalleRepuestoDAO detalleDAO;
    private final InventarioServicio inventario;  // colaborador para reservar/deducir

    /** Crea sus propios DAO JDBC y el servicio de inventario con el que reserva/deduce stock. */
    public OrdenServicioServicio() {
        this(new OrdenServicioDAOJdbc(ConexionFactory.getInstancia()), new VehiculoDAOJdbc(ConexionFactory.getInstancia()),
                new TecnicoDAOJdbc(ConexionFactory.getInstancia()), new AsignacionTecnicoDAOJdbc(ConexionFactory.getInstancia()),
                new DetalleRepuestoDAOJdbc(ConexionFactory.getInstancia()), new InventarioServicio());
    }

    public OrdenServicioServicio(OrdenServicioDAO ordenDAO, VehiculoDAO vehiculoDAO, TecnicoDAO tecnicoDAO,
                                 AsignacionTecnicoDAO asignacionDAO, DetalleRepuestoDAO detalleDAO,
                                 InventarioServicio inventario) {
        this.ordenDAO = ordenDAO;
        this.vehiculoDAO = vehiculoDAO;
        this.tecnicoDAO = tecnicoDAO;
        this.asignacionDAO = asignacionDAO;
        this.detalleDAO = detalleDAO;
        this.inventario = inventario;
    }

    // ------------------------------------------------------------ Órdenes

    /** Crea una orden Pendiente para el vehículo con esa placa (patrón Builder). */
    public int crearOrden(String placa, TipoMantenimiento tipo, String descripcion, int costoManoObra)
            throws SQLException {
        Vehiculo vehiculo = obtenerVehiculoPorPlaca(placa);
        OrdenServicio orden = new OrdenServicioBuilder()
                .vehiculoId(vehiculo.getId())
                .tipoMantenimiento(tipo)
                .descripcionProblema(descripcion)
                .costoManoObra(costoManoObra)
                .build();  // estado PENDIENTE y fecha actual por defecto
        return ordenDAO.insertar(orden);
    }

    public OrdenServicio obtenerOrden(int id) throws SQLException {
        OrdenServicio orden = ordenDAO.buscarPorId(id);
        if (orden == null) {
            throw new RecursoNoEncontradoException("No existe la orden de servicio " + id + ".");
        }
        return orden;
    }

    public List<OrdenServicio> listarOrdenes() throws SQLException {
        return ordenDAO.listar();
    }

    /** Seguimiento: órdenes en un estado concreto (ej. "en espera" = PENDIENTE). */
    public List<OrdenServicio> listarPorEstado(EstadoServicio estado) throws SQLException {
        return ordenDAO.listar().stream()
                .filter(orden -> orden.getEstado() == estado)
                .toList();
    }

    public Vehiculo vehiculoDe(OrdenServicio orden) throws SQLException {
        return vehiculoDAO.buscarPorId(orden.getVehiculoId());
    }

    public List<Tecnico> tecnicosDe(int servicioId) throws SQLException {
        return asignacionDAO.listarTecnicosPorServicio(servicioId);
    }

    public List<DetalleRepuesto> repuestosDe(int servicioId) throws SQLException {
        return detalleDAO.listarPorServicio(servicioId);
    }

    // ------------------------------------------------------------ Técnicos

    public int registrarTecnico(String nombre, String especialidad) throws SQLException {
        return tecnicoDAO.insertar(new Tecnico(0, nombre, especialidad, true));
    }

    public List<Tecnico> listarTecnicos() throws SQLException {
        return tecnicoDAO.listar();
    }

    public void cambiarActividadTecnico(int tecnicoId, boolean activo) throws SQLException {
        obtenerTecnico(tecnicoId);
        tecnicoDAO.actualizarActivo(tecnicoId, activo);
    }

    // ------------------------------------------------------------ Asignaciones

    public void asignarTecnico(int servicioId, int tecnicoId) throws SQLException {
        OrdenServicio orden = obtenerOrden(servicioId);
        exigirAsignable(orden);
        Tecnico tecnico = obtenerTecnico(tecnicoId);
        if (!tecnico.isActivo()) {
            throw new ReglaNegocioException("El técnico " + tecnico.getNombre() + " está inactivo.");
        }
        boolean yaAsignado = tecnicosDe(servicioId).stream().anyMatch(t -> t.getId() == tecnicoId);
        if (yaAsignado) {
            throw new ReglaNegocioException("El técnico ya está asignado a esta orden.");
        }
        asignacionDAO.insertar(new AsignacionTecnico(servicioId, tecnicoId));
    }

    /**
     * Asigna un repuesto: valida disponibilidad, registra el detalle con el
     * precio de venta vigente y RESERVA las unidades en el inventario.
     */
    public void asignarRepuesto(int servicioId, int repuestoId, int cantidad) throws SQLException {
        OrdenServicio orden = obtenerOrden(servicioId);
        exigirAsignable(orden);
        boolean yaAsignado = repuestosDe(servicioId).stream().anyMatch(d -> d.getRepuestoId() == repuestoId);
        if (yaAsignado) {
            throw new ReglaNegocioException("Ese repuesto ya está en la orden.");
        }
        inventario.verificarDisponibilidad(repuestoId, cantidad);  // 1) validar antes de escribir
        Repuesto repuesto = inventario.obtenerRepuesto(repuestoId);
        detalleDAO.insertar(new DetalleRepuesto(servicioId, repuestoId, repuesto.getNombre(), cantidad,
                repuesto.getPrecioVenta(), EstadoReserva.RESERVADO));                  // 2) detalle
        inventario.reservar(repuestoId, cantidad);                                     // 3) "Reservado"
    }

    // ------------------------------------------------------------ Ciclo de vida (State)

    /** Pendiente -> En Proceso. Requiere al menos un técnico asignado. */
    public void iniciar(int servicioId) throws SQLException {
        OrdenServicio orden = obtenerOrden(servicioId);
        validarTransicion(orden, EstadoServicio.EN_PROCESO);
        if (tecnicosDe(servicioId).isEmpty()) {
            throw new ReglaNegocioException("Asigne al menos un técnico antes de iniciar la orden.");
        }
        ordenDAO.actualizarEstado(servicioId, EstadoServicio.EN_PROCESO);
    }

    /**
     * En Proceso -> Completado. REGLA CRÍTICA: cada repuesto RESERVADO se
     * deduce permanentemente del inventario y su detalle pasa a DEDUCIDO.
     */
    public void completar(int servicioId) throws SQLException {
        OrdenServicio orden = obtenerOrden(servicioId);
        validarTransicion(orden, EstadoServicio.COMPLETADO);
        List<DetalleRepuesto> reservados = repuestosDe(servicioId).stream()
                .filter(d -> d.getEstado() == EstadoReserva.RESERVADO)
                .toList();
        for (DetalleRepuesto detalle : reservados) {
            inventario.deducirReservado(detalle.getRepuestoId(), detalle.getCantidad());
            detalleDAO.actualizarEstado(servicioId, detalle.getRepuestoId(), EstadoReserva.DEDUCIDO);
        }
        ordenDAO.actualizarEstado(servicioId, EstadoServicio.COMPLETADO);
    }

    /** Completado -> Facturado. Solo debe usarse desde la facturación, tras emitir la factura. */
    public void marcarFacturado(int servicioId) throws SQLException {
        validarTransicion(obtenerOrden(servicioId), EstadoServicio.FACTURADO);
        ordenDAO.actualizarEstado(servicioId, EstadoServicio.FACTURADO);
    }

    /** Facturado -> Entregado. Registra la fecha de entrega. */
    public void entregar(int servicioId) throws SQLException {
        validarTransicion(obtenerOrden(servicioId), EstadoServicio.ENTREGADO);
        ordenDAO.registrarEntrega(servicioId);  // fecha de entrega = NOW() en MySQL
    }

    /** Consulta del patrón State: ¿la orden está en el estado previo a "destino"? */
    public void validarTransicion(OrdenServicio orden, EstadoServicio destino) {
        EstadoOrden actual = FabricaEstadoOrden.crear(orden.getEstado());
        EstadoOrden siguiente = actual.avanzar();  // lanza excepción si es estado final
        if (siguiente.getCodigo() != destino) {
            throw new ReglaNegocioException("La orden " + orden.getId() + " está " + actual.getCodigo().getDescripcion()
                    + "; su siguiente estado es " + siguiente.getCodigo().getDescripcion()
                    + ", no " + destino.getDescripcion() + ".");
        }
    }

    // ------------------------------------------------------------ Apoyo

    private void exigirAsignable(OrdenServicio orden) {
        if (!FabricaEstadoOrden.crear(orden.getEstado()).permiteAsignaciones()) {
            throw new ReglaNegocioException("La orden está " + orden.getEstado().getDescripcion()
                    + ": ya no admite técnicos ni repuestos.");
        }
    }

    private Tecnico obtenerTecnico(int tecnicoId) throws SQLException {
        Tecnico tecnico = tecnicoDAO.buscarPorId(tecnicoId);
        if (tecnico == null) {
            throw new RecursoNoEncontradoException("No existe el técnico " + tecnicoId + ".");
        }
        return tecnico;
    }

    private Vehiculo obtenerVehiculoPorPlaca(String placa) throws SQLException {
        Vehiculo vehiculo = vehiculoDAO.buscarPorPlaca(placa);
        if (vehiculo == null) {
            throw new RecursoNoEncontradoException("No hay un vehículo con placa " + Vehiculo.normalizarPlaca(placa)
                    + ". Regístrelo primero en el módulo de clientes.");
        }
        return vehiculo;
    }

    // ------------------------------------------------------------ Actualizar / cancelar órdenes

    /** Corrige tipo, descripción y mano de obra mientras la orden admite cambios (State). */
    public void actualizarOrden(int servicioId, TipoMantenimiento tipo, String descripcion, int costoManoObra)
            throws SQLException {
        OrdenServicio actual = obtenerOrden(servicioId);
        exigirAsignable(actual);
        ordenDAO.actualizar(new OrdenServicioBuilder()
                .id(actual.getId())
                .vehiculoId(actual.getVehiculoId())
                .fechaRecepcion(actual.getFechaRecepcion())
                .estado(actual.getEstado())
                .tipoMantenimiento(tipo)
                .descripcionProblema(descripcion)
                .costoManoObra(costoManoObra)
                .build());  // el Builder vuelve a validar los datos
    }

    /**
     * Cancela (elimina) una orden PENDIENTE: libera las reservas de repuestos,
     * borra sus detalles y asignaciones y finalmente la orden.
     */
    public void cancelarOrden(int servicioId) throws SQLException {
        OrdenServicio orden = obtenerOrden(servicioId);
        if (orden.getEstado() != EstadoServicio.PENDIENTE) {
            throw new ReglaNegocioException("Solo se puede cancelar una orden Pendiente; esta está "
                    + orden.getEstado().getDescripcion() + ".");
        }
        for (DetalleRepuesto detalle : repuestosDe(servicioId)) {
            inventario.liberarReserva(detalle.getRepuestoId(), detalle.getCantidad());
            detalleDAO.eliminar(servicioId, detalle.getRepuestoId());
        }
        for (Tecnico tecnico : tecnicosDe(servicioId)) {
            asignacionDAO.eliminar(servicioId, tecnico.getId());
        }
        ordenDAO.eliminar(servicioId);
    }

    // ------------------------------------------------------------ Quitar / reemplazar asignaciones

    /** Quita un técnico; una orden En Proceso debe conservar al menos uno. */
    public void quitarTecnico(int servicioId, int tecnicoId) throws SQLException {
        OrdenServicio orden = obtenerOrden(servicioId);
        exigirAsignable(orden);
        List<Tecnico> asignados = tecnicosDe(servicioId);
        if (!asignados.stream().anyMatch(t -> t.getId() == tecnicoId)) {
            throw new RecursoNoEncontradoException("El técnico " + tecnicoId + " no está asignado a la orden.");
        }
        if (orden.getEstado() == EstadoServicio.EN_PROCESO && asignados.size() == 1) {
            throw new ReglaNegocioException("Una orden en proceso debe conservar al menos un técnico.");
        }
        asignacionDAO.eliminar(servicioId, tecnicoId);
    }

    /** Reemplaza un técnico asignado por otro activo que no esté en la orden. */
    public void reemplazarTecnico(int servicioId, int tecnicoAnterior, int tecnicoNuevo) throws SQLException {
        exigirAsignable(obtenerOrden(servicioId));
        List<Tecnico> asignados = tecnicosDe(servicioId);
        if (!asignados.stream().anyMatch(t -> t.getId() == tecnicoAnterior)) {
            throw new RecursoNoEncontradoException("El técnico " + tecnicoAnterior + " no está asignado a la orden.");
        }
        Tecnico nuevo = obtenerTecnico(tecnicoNuevo);
        if (!nuevo.isActivo() || asignados.stream().anyMatch(t -> t.getId() == tecnicoNuevo)) {
            throw new ReglaNegocioException("El técnico nuevo debe estar activo y no estar ya en la orden.");
        }
        asignacionDAO.actualizar(servicioId, tecnicoAnterior, tecnicoNuevo);
    }

    /** Quita un repuesto RESERVADO de la orden y devuelve sus unidades al disponible. */
    public void quitarRepuesto(int servicioId, int repuestoId) throws SQLException {
        exigirAsignable(obtenerOrden(servicioId));
        List<DetalleRepuesto> encontrados = repuestosDe(servicioId).stream()
                .filter(d -> d.getRepuestoId() == repuestoId)
                .toList();
        if (encontrados.isEmpty()) {
            throw new RecursoNoEncontradoException("El repuesto " + repuestoId + " no está en la orden.");
        }
        DetalleRepuesto detalle = encontrados.get(0);
        detalleDAO.eliminar(servicioId, repuestoId);
        inventario.liberarReserva(repuestoId, detalle.getCantidad());
    }

    // ------------------------------------------------------------ CRUD de técnicos

    public void actualizarTecnico(int tecnicoId, String nombre, String especialidad) throws SQLException {
        Tecnico actual = obtenerTecnico(tecnicoId);
        tecnicoDAO.actualizar(new Tecnico(tecnicoId, nombre, especialidad, actual.isActivo()));
    }

    /** Solo se elimina un técnico sin órdenes; si tiene historial, debe desactivarse. */
    public void eliminarTecnico(int tecnicoId) throws SQLException {
        obtenerTecnico(tecnicoId);
        if (asignacionDAO.listar().stream().anyMatch(a -> a.getTecnicoId() == tecnicoId)) {
            throw new ReglaNegocioException("El técnico tiene órdenes en su historial; desactívelo en lugar de eliminarlo.");
        }
        tecnicoDAO.eliminar(tecnicoId);
    }
}
