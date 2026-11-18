/*
 * ============================================================
 * Archivo: OrdenServicioController.java
 * Propósito: controlador del Módulo de Gestión de Servicios (órdenes,
 *            técnicos, repuestos y ciclo de vida).
 * Conceptos clave: MVC - Controlador, COMMAND, conversión de id a enum
 *            (TipoMantenimiento.desdeId / EstadoServicio.desdeId).
 * Justificación: cada opción es una acción pequeña que delega en
 *            OrdenServicioServicio (o en TallerFacade para la recepción rápida); las reglas (State, reserva, deducción,
 *            liberación de reservas) no aparecen aquí.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN FACADE -> S13Demo01 CheckoutFacade (Sesión 13)
 *   - PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.modelo.EstadoServicio;
import com.carmotors.modelo.OrdenServicio;
import com.carmotors.modelo.Tecnico;
import com.carmotors.modelo.TipoMantenimiento;
import com.carmotors.servicio.OrdenServicioServicio;
import com.carmotors.servicio.TallerFacade;
import com.carmotors.vista.OrdenServicioVista;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;
import java.util.List;

public class OrdenServicioController extends ControladorBase {
    private final OrdenServicioVista vista;
    private final OrdenServicioServicio servicio;
    private final TallerFacade taller;  // Facade para la recepción completa

    /** Crea su vista y su servicio: el módulo se instancia solo cuando el usuario entra a él. */
    public OrdenServicioController(VistaConsola consola) {
        this(consola, new OrdenServicioVista(consola), new OrdenServicioServicio(), new TallerFacade());
    }

    public OrdenServicioController(VistaConsola consola, OrdenServicioVista vista, OrdenServicioServicio servicio,
                                   TallerFacade taller) {
        super(consola, vista);
        this.vista = vista;
        this.servicio = servicio;
        this.taller = taller;
        registrar("1", () -> crearOrden());
        registrar("2", () -> vista.mostrarOrdenes(servicio.listarOrdenes()));
        registrar("3", () -> listarPorEstado());
        registrar("4", () -> verDetalle());
        registrar("5", () -> actualizarOrden());
        registrar("6", () -> asignarTecnico());
        registrar("7", () -> reemplazarTecnico());
        registrar("8", () -> quitarTecnico());
        registrar("9", () -> asignarRepuesto());
        registrar("10", () -> quitarRepuesto());
        registrar("11", () -> iniciarOrden());
        registrar("12", () -> completar());
        registrar("13", () -> entregar());
        registrar("14", () -> cancelarOrden());
        registrar("15", () -> registrarTecnico());
        registrar("16", () -> vista.mostrarTecnicos(servicio.listarTecnicos()));
        registrar("17", () -> actualizarTecnico());
        registrar("18", () -> cambiarActividad());
        registrar("19", () -> eliminarTecnico());
        registrar("20", () -> recepcionRapida());
    }

    private void crearOrden() throws SQLException {
        String placa = consola.leerTextoObligatorio("Placa del vehículo: ");
        vista.mostrarTiposMantenimiento();
        TipoMantenimiento tipo = TipoMantenimiento.desdeId(consola.leerEntero("Tipo de mantenimiento: "));
        String descripcion = consola.leerTextoObligatorio("Descripción del problema: ");
        int manoObra = consola.leerMonto("Costo estimado de mano de obra: ");
        int id = servicio.crearOrden(placa, tipo, descripcion, manoObra);
        consola.mostrarExito("Orden #" + id + " creada en estado Pendiente.");
    }

    // Pide solo los datos que faltan: si el cliente o el vehículo ya existen, no los vuelve a pedir.
    private void recepcionRapida() throws SQLException {
        String documento = consola.leerTextoObligatorio("Documento del cliente: ");
        String nombre = "";
        String telefono = "";
        String email = "";
        if (taller.esClienteNuevo(documento)) {
            consola.mostrar("Cliente nuevo: complete sus datos.");
            nombre = consola.leerTextoObligatorio("Nombre completo: ");
            telefono = consola.leerTexto("Teléfono: ");
            email = consola.leerTextoObligatorio("Email: ");
        }
        String placa = consola.leerTextoObligatorio("Placa del vehículo: ");
        String marca = "";
        String modelo = "";
        if (taller.esVehiculoNuevo(placa)) {
            consola.mostrar("Vehículo nuevo: complete sus datos.");
            marca = consola.leerTexto("Marca: ");
            modelo = consola.leerTexto("Modelo: ");
        }
        vista.mostrarTiposMantenimiento();
        TipoMantenimiento tipo = TipoMantenimiento.desdeId(consola.leerEntero("Tipo de mantenimiento: "));
        String descripcion = consola.leerTextoObligatorio("Descripción del problema: ");
        int manoObra = consola.leerMonto("Costo estimado de mano de obra: ");
        int id = taller.recibirVehiculo(documento, nombre, telefono, email, placa, marca, modelo, tipo, descripcion,
                manoObra);
        consola.mostrarExito("Recepción completa: orden #" + id + " creada en estado Pendiente.");
    }

    private void listarPorEstado() throws SQLException {
        vista.mostrarEstados();
        vista.mostrarOrdenes(servicio.listarPorEstado(EstadoServicio.desdeId(consola.leerEntero("Estado: "))));
    }

    private void verDetalle() throws SQLException {
        OrdenServicio orden = servicio.obtenerOrden(consola.leerEntero("Número de orden: "));
        vista.mostrarDetalle(orden, servicio.vehiculoDe(orden), servicio.tecnicosDe(orden.getId()),
                servicio.repuestosDe(orden.getId()));
    }

    private void actualizarOrden() throws SQLException {
        OrdenServicio actual = servicio.obtenerOrden(consola.leerEntero("Número de orden: "));
        consola.mostrar("Deje vacío para conservar el valor actual.");
        vista.mostrarTiposMantenimiento();
        String tipoTexto = consola.leerTexto("Tipo [" + actual.getTipoMantenimiento().getId() + "]: ");
        TipoMantenimiento tipo = tipoTexto.isBlank() ? actual.getTipoMantenimiento()
                : TipoMantenimiento.desdeId(Integer.parseInt(tipoTexto));
        String descripcion = consola.leerTexto("Descripción [" + actual.getDescripcionProblema() + "]: ");
        String manoObra = consola.leerTexto("Mano de obra [" + actual.getCostoManoObra() + "]: ");
        servicio.actualizarOrden(actual.getId(), tipo,
                descripcion.isBlank() ? actual.getDescripcionProblema() : descripcion,
                manoObra.isBlank() ? actual.getCostoManoObra() : Integer.parseInt(manoObra));
        consola.mostrarExito("Orden actualizada.");
    }

    private void asignarTecnico() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        vista.mostrarTecnicos(servicio.listarTecnicos());
        servicio.asignarTecnico(orden, consola.leerEntero("Id del técnico: "));
        consola.mostrarExito("Técnico asignado.");
    }

    private void reemplazarTecnico() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        vista.mostrarTecnicos(servicio.tecnicosDe(orden));
        int anterior = consola.leerEntero("Id del técnico a reemplazar: ");
        int nuevo = consola.leerEntero("Id del técnico nuevo: ");
        servicio.reemplazarTecnico(orden, anterior, nuevo);
        consola.mostrarExito("Técnico reemplazado.");
    }

    private void quitarTecnico() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        List<Tecnico> asignados = servicio.tecnicosDe(orden);
        vista.mostrarTecnicos(asignados);
        servicio.quitarTecnico(orden, consola.leerEntero("Id del técnico a quitar: "));
        consola.mostrarExito("Técnico retirado de la orden.");
    }

    private void asignarRepuesto() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        int repuesto = consola.leerEntero("Id del repuesto: ");
        int cantidad = consola.leerEntero("Cantidad: ");
        servicio.asignarRepuesto(orden, repuesto, cantidad);
        consola.mostrarExito("Repuesto asignado y RESERVADO en inventario.");
    }

    private void quitarRepuesto() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        servicio.quitarRepuesto(orden, consola.leerEntero("Id del repuesto a quitar: "));
        consola.mostrarExito("Repuesto retirado; su reserva volvió al disponible.");
    }

    private void iniciarOrden() throws SQLException {
        servicio.iniciar(consola.leerEntero("Número de orden: "));
        consola.mostrarExito("Orden en proceso.");
    }

    private void completar() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        if (consola.confirmar("Se descontarán del inventario los repuestos reservados.")) {
            servicio.completar(orden);
            consola.mostrarExito("Orden completada; stock deducido. Ya puede facturarse.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void entregar() throws SQLException {
        servicio.entregar(consola.leerEntero("Número de orden: "));
        consola.mostrarExito("Vehículo entregado al cliente.");
    }

    private void cancelarOrden() throws SQLException {
        int orden = consola.leerEntero("Número de orden: ");
        if (consola.confirmar("Se eliminará la orden y se liberarán sus repuestos reservados.")) {
            servicio.cancelarOrden(orden);
            consola.mostrarExito("Orden cancelada.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void registrarTecnico() throws SQLException {
        String nombre = consola.leerTextoObligatorio("Nombre: ");
        String especialidad = consola.leerTexto("Especialidad: ");
        consola.mostrarExito("Técnico registrado con id " + servicio.registrarTecnico(nombre, especialidad) + ".");
    }

    private void actualizarTecnico() throws SQLException {
        int id = consola.leerEntero("Id del técnico: ");
        String nombre = consola.leerTextoObligatorio("Nombre: ");
        String especialidad = consola.leerTexto("Especialidad: ");
        servicio.actualizarTecnico(id, nombre, especialidad);
        consola.mostrarExito("Técnico actualizado.");
    }

    private void cambiarActividad() throws SQLException {
        int id = consola.leerEntero("Id del técnico: ");
        boolean activo = consola.leerEntero("1 = activar, 2 = desactivar: ") == 1;
        servicio.cambiarActividadTecnico(id, activo);
        consola.mostrarExito("Técnico " + (activo ? "activado." : "desactivado."));
    }

    private void eliminarTecnico() throws SQLException {
        servicio.eliminarTecnico(consola.leerEntero("Id del técnico: "));
        consola.mostrarExito("Técnico eliminado.");
    }
}
