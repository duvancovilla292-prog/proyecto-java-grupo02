/*
 * ============================================================
 * Archivo: TallerFacade.java
 * Propósito: ofrecer en UNA sola operación la "recepción de un vehículo":
 *            registra al cliente si es nuevo, registra el vehículo si es
 *            nuevo y abre la orden de servicio.
 * Conceptos clave: PATRÓN FACADE (S13Demo01 CheckoutFacade): una fachada
 *            simple delante de varios servicios; composición e inyección por
 *            constructor.
 * Justificación: sin la fachada, recibir a un cliente nuevo exige pasar por
 *            tres menús y dos módulos (clientes y servicios). La fachada
 *            coordina ClienteServicio y OrdenServicioServicio sin duplicar sus
 *            reglas: cada servicio sigue validando lo suyo (SRP), y quien usa la
 *            fachada no necesita conocer el orden de los pasos.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.servicio;

import com.carmotors.excepcion.ReglaNegocioException;
import com.carmotors.modelo.Cliente;
import com.carmotors.modelo.TipoMantenimiento;
import com.carmotors.modelo.Vehiculo;

import java.sql.SQLException;
import java.util.List;

// Visto en clase: PATRÓN FACADE -> S13Demo01 CheckoutFacade (Sesión 13)
public class TallerFacade {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final ClienteServicio clientes;
    private final OrdenServicioServicio ordenes;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    /** Crea los servicios que coordina. */
    public TallerFacade() {
        this.clientes = new ClienteServicio();
        this.ordenes = new OrdenServicioServicio();
    }

    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    /** Clientes registrados: referencia al pedir el documento. */
    public List<Cliente> listarClientes() throws SQLException {
        return clientes.listarClientes();
    }

    /** Vehículos del cliente: referencia al pedir la placa. */
    public List<Vehiculo> vehiculosDe(String documento) throws SQLException {
        return clientes.vehiculosDe(documento);
    }

    /** true si hay que pedir los datos del cliente porque aún no existe. */
    public boolean esClienteNuevo(String documento) throws SQLException {
        return !clientes.existeCliente(documento);
    }

    /** true si hay que pedir marca y modelo porque la placa aún no existe. */
    public boolean esVehiculoNuevo(String placa) throws SQLException {
        return !clientes.existeVehiculo(placa);
    }

    /**
     * Recepción completa. Los datos del cliente o del vehículo solo se usan si
     * son nuevos. Si la placa ya existe, debe pertenecer a ese cliente.
     * Devuelve el número de la orden creada (estado Pendiente).
     */
    public int recibirVehiculo(String documento, String nombre, String telefono, String email,
                               String placa, String marca, String modelo,
                               TipoMantenimiento tipo, String descripcion, int costoManoObra) throws SQLException {
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (esClienteNuevo(documento)) {
            clientes.registrarCliente(nombre, documento, telefono, email);
        }
        Cliente cliente = clientes.obtenerPorDocumento(documento);
        if (esVehiculoNuevo(placa)) {
            clientes.registrarVehiculo(documento, placa, marca, modelo);
        } else {
            Vehiculo vehiculo = clientes.obtenerVehiculo(placa);
            if (vehiculo.getClienteId() != cliente.getId()) {
                throw new ReglaNegocioException("La placa " + vehiculo.getPlaca()
                        + " pertenece a otro cliente; transfiérala primero en el módulo de clientes.");
            }
        }
        return ordenes.crearOrden(placa, tipo, descripcion, costoManoObra);
    }
}
