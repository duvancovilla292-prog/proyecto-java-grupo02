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
 * ============================================================
 */
package com.carmotors.servicio;

import com.carmotors.excepcion.ReglaNegocioException;
import com.carmotors.modelo.Cliente;
import com.carmotors.modelo.TipoMantenimiento;
import com.carmotors.modelo.Vehiculo;

import java.sql.SQLException;

public class TallerFacade {
    private final ClienteServicio clientes;
    private final OrdenServicioServicio ordenes;

    public TallerFacade(ClienteServicio clientes, OrdenServicioServicio ordenes) {
        this.clientes = clientes;
        this.ordenes = ordenes;
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
