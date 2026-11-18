/*
 * ============================================================
 * Archivo: ClienteServicio.java
 * Propósito: lógica del Módulo de Gestión de Clientes: perfiles,
 *            vehículos, historial de servicios y recordatorios de
 *            mantenimiento preventivo.
 * Conceptos clave: capa de servicio, Streams (filter, map, sorted,
 *            anyMatch, toList), Map/HashMap para búsquedas por id
 *            (sesión 10). Las fechas del recordatorio las calcula MySQL
 *            (CalendarioPreventivo); aquí solo se aplica la regla.
 * Justificación: las validaciones de unicidad (documento, email, placa)
 *            se hacen ANTES de llamar al DAO para dar mensajes claros; la BD
 *            conserva las restricciones UNIQUE como respaldo.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - break / continue -> S03DemoFor (Sesión 03)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - Map / HashMap / LinkedHashMap / Map.Entry -> S10Demo01MapMain (Sesión 10)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 *   - Predicate<T> / Comparator<T> / compareTo -> S19Demo03ReglasOrdenMain (Sesión 19)
 *   - referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
 *   - Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
 *   - Stream: count / anyMatch / Arrays.stream -> S20Demo01FuentesMain (Sesión 20)
 *   - Stream: distinct / sorted / mapToInt+sum / reduce(0, Integer::sum) -> S20Demo02OrdenesMain (Sesión 20)
 * ============================================================
 */
package com.carmotors.servicio;

import com.carmotors.dao.ClienteDAO;
import com.carmotors.dao.OrdenServicioDAO;
import com.carmotors.dao.VehiculoDAO;
import com.carmotors.excepcion.DatoInvalidoException;
import com.carmotors.excepcion.RecursoNoEncontradoException;
import com.carmotors.excepcion.ReglaNegocioException;
import com.carmotors.modelo.CalendarioPreventivo;
import com.carmotors.modelo.Cliente;
import com.carmotors.modelo.OrdenServicio;
import com.carmotors.modelo.RecordatorioMantenimiento;
import com.carmotors.modelo.TipoMantenimiento;
import com.carmotors.modelo.Vehiculo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteServicio {
    private final ClienteDAO clienteDAO;
    private final VehiculoDAO vehiculoDAO;
    private final OrdenServicioDAO ordenDAO;

    public ClienteServicio(ClienteDAO clienteDAO, VehiculoDAO vehiculoDAO, OrdenServicioDAO ordenDAO) {
        this.clienteDAO = clienteDAO;
        this.vehiculoDAO = vehiculoDAO;
        this.ordenDAO = ordenDAO;
    }

    public int registrarCliente(String nombre, String documento, String telefono, String email) throws SQLException {
        Cliente nuevo = new Cliente(0, nombre, documento, telefono, email); // valida formato
        if (clienteDAO.buscarPorDocumento(nuevo.getDocumento()) != null) {
            throw new ReglaNegocioException("Ya existe un cliente con el documento " + nuevo.getDocumento() + ".");
        }
        validarEmailLibre(nuevo.getEmail(), 0);
        return clienteDAO.insertar(nuevo);
    }

    /** Actualiza los datos de contacto del cliente identificado por documento. */
    public void actualizarCliente(String documento, String nombre, String telefono, String email) throws SQLException {
        Cliente actual = obtenerPorDocumento(documento);
        Cliente modificado = new Cliente(actual.getId(), nombre, actual.getDocumento(), telefono, email);
        validarEmailLibre(modificado.getEmail(), actual.getId());
        clienteDAO.actualizar(modificado);
    }

    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.listar();
    }

    public Cliente obtenerPorDocumento(String documento) throws SQLException {
        Cliente cliente = clienteDAO.buscarPorDocumento(documento == null ? "" : documento.strip());
        if (cliente == null) {
            throw new RecursoNoEncontradoException("No existe un cliente con el documento " + documento + ".");
        }
        return cliente;
    }

    public Cliente obtenerPorId(int id) throws SQLException {
        Cliente cliente = clienteDAO.buscarPorId(id);
        if (cliente == null) {
            throw new RecursoNoEncontradoException("No existe el cliente " + id + ".");
        }
        return cliente;
    }

    public int registrarVehiculo(String documentoCliente, String placa, String marca, String modelo)
            throws SQLException {
        Cliente dueno = obtenerPorDocumento(documentoCliente);
        Vehiculo nuevo = new Vehiculo(0, dueno.getId(), placa, marca, modelo);
        if (vehiculoDAO.buscarPorPlaca(nuevo.getPlaca()) != null) {
            throw new ReglaNegocioException("La placa " + nuevo.getPlaca() + " ya está registrada.");
        }
        return vehiculoDAO.insertar(nuevo);
    }

    public List<Vehiculo> vehiculosDe(String documentoCliente) throws SQLException {
        return vehiculoDAO.listarPorCliente(obtenerPorDocumento(documentoCliente).getId());
    }

    /** Historial: todas las órdenes de todos los vehículos del cliente, de la más reciente a la más antigua. */
    public List<OrdenServicio> historialServicios(String documentoCliente) throws SQLException {
        List<Integer> idsVehiculos = vehiculosDe(documentoCliente).stream()
                .map(Vehiculo::getId)
                .toList();
        return ordenDAO.listar().stream()
                .filter(orden -> idsVehiculos.contains(orden.getVehiculoId()))
                .sorted((a, b) -> b.getFechaRecepcion().compareTo(a.getFechaRecepcion()))
                .toList();
    }

    /**
     * Recordatorios automáticos: para cada vehículo toma su último
     * mantenimiento PREVENTIVO terminado y suma "meses". Si esa fecha ya pasó
     * o cae dentro de los próximos "diasAnticipacion", hay que notificar.
     * Se omiten vehículos que ya tienen un preventivo abierto en el taller.
     */
    public List<RecordatorioMantenimiento> recordatoriosMantenimiento(int meses, int diasAnticipacion)
            throws SQLException {
        if (meses <= 0 || diasAnticipacion < 0) {
            throw new DatoInvalidoException("Meses positivos y días de anticipación no negativos.");
        }
        List<OrdenServicio> preventivas = ordenDAO.listar().stream()
                .filter(o -> o.getTipoMantenimiento() == TipoMantenimiento.PREVENTIVO)
                .toList();
        Map<Integer, Cliente> clientesPorId = new HashMap<>();
        for (Cliente cliente : clienteDAO.listar()) {
            clientesPorId.put(cliente.getId(), cliente);
        }
        Map<Integer, Vehiculo> vehiculosPorId = new HashMap<>();
        for (Vehiculo vehiculo : vehiculoDAO.listar()) {
            vehiculosPorId.put(vehiculo.getId(), vehiculo);
        }
        List<RecordatorioMantenimiento> recordatorios = new ArrayList<>();

        // MySQL devuelve, por vehículo, el último preventivo, el próximo (+meses), hoy y el límite (+días).
        for (CalendarioPreventivo calendario : ordenDAO.calendarioPreventivos(meses, diasAnticipacion)) {
            int vehiculoId = calendario.getVehiculoId();
            boolean tieneAbierto = preventivas.stream()
                    .anyMatch(o -> o.getVehiculoId() == vehiculoId && !o.getEstado().trabajoTerminado());
            if (tieneAbierto || !calendario.debeNotificarse()) {
                continue;  // continue de la sesión 03
            }
            Vehiculo vehiculo = vehiculosPorId.get(vehiculoId);
            recordatorios.add(new RecordatorioMantenimiento(clientesPorId.get(vehiculo.getClienteId()), vehiculo,
                    calendario.getUltimo(), calendario.getProximo(), calendario.estaVencido()));
        }
        return recordatorios.stream()
                .sorted((a, b) -> a.getProximoPreventivo().compareTo(b.getProximoPreventivo()))
                .toList();
    }

    // El email es UNIQUE: se rechaza si otro cliente (id distinto) ya lo usa.
    private void validarEmailLibre(String email, int idPropio) throws SQLException {
        boolean usado = clienteDAO.listar().stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email) && c.getId() != idPropio);
        if (usado) {
            throw new ReglaNegocioException("El email " + email + " ya pertenece a otro cliente.");
        }
    }

    /** Elimina un cliente sin vehículos registrados (su historial se conserva si tiene). */
    public void eliminarCliente(String documento) throws SQLException {
        Cliente cliente = obtenerPorDocumento(documento);
        if (!vehiculoDAO.listarPorCliente(cliente.getId()).isEmpty()) {
            throw new ReglaNegocioException("El cliente tiene vehículos registrados; elimínelos o transfiéralos primero.");
        }
        clienteDAO.eliminar(cliente.getId());
    }

    /** Actualiza marca/modelo y, si se indica un documento, transfiere el vehículo a otro cliente. */
    public void actualizarVehiculo(String placa, String documentoNuevoDueno, String marca, String modelo)
            throws SQLException {
        Vehiculo actual = obtenerVehiculo(placa);
        int dueno = documentoNuevoDueno == null || documentoNuevoDueno.isBlank()
                ? actual.getClienteId() : obtenerPorDocumento(documentoNuevoDueno).getId();
        vehiculoDAO.actualizar(new Vehiculo(actual.getId(), dueno, actual.getPlaca(), marca, modelo));
    }

    /** Elimina un vehículo sin órdenes de servicio (el historial nunca se borra). */
    public void eliminarVehiculo(String placa) throws SQLException {
        Vehiculo vehiculo = obtenerVehiculo(placa);
        if (!ordenDAO.listarPorVehiculo(vehiculo.getId()).isEmpty()) {
            throw new ReglaNegocioException("El vehículo tiene historial de servicios; no se puede eliminar.");
        }
        vehiculoDAO.eliminar(vehiculo.getId());
    }

    /** Consulta sin excepción: ¿existe un cliente con ese documento? (la usa TallerFacade). */
    public boolean existeCliente(String documento) throws SQLException {
        return documento != null && clienteDAO.buscarPorDocumento(documento.strip()) != null;
    }

    /** Consulta sin excepción: ¿existe un vehículo con esa placa? (la usa TallerFacade). */
    public boolean existeVehiculo(String placa) throws SQLException {
        return vehiculoDAO.buscarPorPlaca(placa) != null;
    }

    public Vehiculo obtenerVehiculo(String placa) throws SQLException {
        Vehiculo vehiculo = vehiculoDAO.buscarPorPlaca(placa);
        if (vehiculo == null) {
            throw new RecursoNoEncontradoException("No existe un vehículo con placa " + Vehiculo.normalizarPlaca(placa) + ".");
        }
        return vehiculo;
    }
}
