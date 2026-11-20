/*
 * ============================================================
 * Archivo: OrdenServicioController.java
 * Propósito: controlador del Módulo de Gestión de Servicios (órdenes,
 *            técnicos, repuestos y ciclo de vida).
 * Conceptos clave: MVC - Controlador, COMMAND, conversión de id a enum
 *            (TipoMantenimiento.desdeId / EstadoServicio.desdeId).
 * Justificación: cada opción es una acción pequeña que delega en
 *            OrdenServicioServicio (o en TallerFacade para la recepción
 *            rápida); las reglas (State, reserva, deducción, liberación de
 *            reservas) no aparecen aquí.
 *            Antes de pedir un id, placa, documento o código muestra la lista
 *            de opciones válidas con referencia(...) (heredado de
 *            ControladorBase), separada con título y línea, para que el
 *            usuario no tenga que saberla de memoria.
 *            Las órdenes se filtran según la operación (ej. "Iniciar" solo
 *            lista las Pendientes). Reutiliza ClienteVista e InventarioVista
 *            para mostrar clientes, vehículos y repuestos.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.excepcion.DatoInvalidoException;
import com.carmotors.modelo.EstadoServicio;
import com.carmotors.modelo.OrdenServicio;
import com.carmotors.modelo.Tecnico;
import com.carmotors.modelo.TipoMantenimiento;
import com.carmotors.servicio.OrdenServicioServicio;
import com.carmotors.servicio.TallerFacade;
import com.carmotors.vista.ClienteVista;
import com.carmotors.vista.InventarioVista;
import com.carmotors.vista.OrdenServicioVista;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;
import java.util.List;

// Visto en clase: herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
// Visto en clase: PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public class OrdenServicioController extends ControladorBase {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final OrdenServicioVista vista;
    private final OrdenServicioServicio servicio;
    // Visto en clase: PATRÓN FACADE -> S13Demo01 CheckoutFacade (Sesión 13)
    private final TallerFacade taller;           // Facade para la recepción completa
    private final ClienteVista clientes;         // reutiliza las tablas de clientes y vehículos como referencia
    private final InventarioVista inventario;    // reutiliza la tabla de repuestos como referencia

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    /** Recibe su vista (como UsuarioController de S18Demo01) y crea su servicio; se instancia solo cuando el usuario entra al módulo. */
    public OrdenServicioController(VistaConsola consola, OrdenServicioVista vista) {
        super(consola, vista);
        this.vista = vista;
        this.servicio = new OrdenServicioServicio();
        this.taller = new TallerFacade();
        this.clientes = new ClienteVista(consola);
        this.inventario = new InventarioVista(consola);
        // Visto en clase: expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
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

    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    private void crearOrden() throws SQLException {
        referencia("Vehículos registrados", () -> clientes.mostrarVehiculos(servicio.listarVehiculos()));
        String placa = consola.leerTextoObligatorio("Placa del vehículo: ");
        TipoMantenimiento tipo = pedirTipoMantenimiento();
        String descripcion = consola.leerTextoObligatorio("Descripción del problema: ");
        int manoObra = consola.leerMonto("Costo estimado de mano de obra: ");
        int id = servicio.crearOrden(placa, tipo, descripcion, manoObra);
        consola.mostrarExito("Orden #" + id + " creada en estado Pendiente.");
    }

    // Pide solo los datos que faltan: si el cliente o el vehículo ya existen, no los vuelve a pedir.
    private void recepcionRapida() throws SQLException {
        referencia("Clientes registrados (si es nuevo, escriba su documento)",
                () -> clientes.mostrarClientes(taller.listarClientes()));
        String documento = consola.leerTextoObligatorio("Documento del cliente: ");
        String nombre = "";
        String telefono = "";
        String email = "";
        boolean clienteNuevo = taller.esClienteNuevo(documento);
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (clienteNuevo) {
            consola.mostrarSeccion("Cliente nuevo: complete sus datos");
            nombre = consola.leerTextoObligatorio("Nombre completo: ");
            telefono = consola.leerTexto("Teléfono: ");
            email = consola.leerTextoObligatorio("Email: ");
        } else {
            referencia("Vehículos del cliente (si es otro, escriba su placa)",
                    () -> clientes.mostrarVehiculos(taller.vehiculosDe(documento)));
        }
        String placa = consola.leerTextoObligatorio("Placa del vehículo: ");
        String marca = "";
        String modelo = "";
        if (taller.esVehiculoNuevo(placa)) {
            consola.mostrarSeccion("Vehículo nuevo: complete sus datos");
            marca = consola.leerTexto("Marca: ");
            modelo = consola.leerTexto("Modelo: ");
        }
        TipoMantenimiento tipo = pedirTipoMantenimiento();
        String descripcion = consola.leerTextoObligatorio("Descripción del problema: ");
        int manoObra = consola.leerMonto("Costo estimado de mano de obra: ");
        int id = taller.recibirVehiculo(documento, nombre, telefono, email, placa, marca, modelo, tipo, descripcion,
                manoObra);
        consola.mostrarExito("Recepción completa: orden #" + id + " creada en estado Pendiente.");
    }

    private void listarPorEstado() throws SQLException {
        referencia("Estados de una orden", () -> vista.mostrarEstados());
        vista.mostrarOrdenes(servicio.listarPorEstado(EstadoServicio.desdeId(consola.leerEntero("Estado: "))));
    }

    private void verDetalle() throws SQLException {
        referencia("Órdenes registradas", () -> vista.mostrarOrdenes(servicio.listarOrdenes()));
        OrdenServicio orden = servicio.obtenerOrden(consola.leerEntero("Número de orden: "));
        vista.mostrarDetalle(orden, servicio.vehiculoDe(orden), servicio.tecnicosDe(orden.getId()),
                servicio.repuestosDe(orden.getId()));
    }

    private void actualizarOrden() throws SQLException {
        OrdenServicio actual = servicio.obtenerOrden(pedirOrdenAsignable());
        consola.mostrar("Deje vacío para conservar el valor actual.");
        referencia("Tipos de mantenimiento", () -> vista.mostrarTiposMantenimiento());
        String tipoTexto = consola.leerTexto("Tipo [" + actual.getTipoMantenimiento().getId() + "]: ");
        // Visto en clase: String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
        TipoMantenimiento tipo = tipoTexto.isBlank() ? actual.getTipoMantenimiento()
                : TipoMantenimiento.desdeId(entero(tipoTexto));
        String descripcion = consola.leerTexto("Descripción [" + actual.getDescripcionProblema() + "]: ");
        String manoObra = consola.leerTexto("Mano de obra [" + actual.getCostoManoObra() + "]: ");
        // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
        servicio.actualizarOrden(actual.getId(), tipo,
                descripcion.isBlank() ? actual.getDescripcionProblema() : descripcion,
                manoObra.isBlank() ? actual.getCostoManoObra() : entero(manoObra));
        consola.mostrarExito("Orden actualizada.");
    }

    private void asignarTecnico() throws SQLException {
        int orden = pedirOrdenAsignable();
        referencia("Técnicos activos", () -> vista.mostrarTecnicos(servicio.listarTecnicosActivos()));
        servicio.asignarTecnico(orden, consola.leerEntero("Id del técnico: "));
        consola.mostrarExito("Técnico asignado.");
    }

    private void reemplazarTecnico() throws SQLException {
        int orden = pedirOrdenAsignable();
        // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
        List<Tecnico> asignados = servicio.tecnicosDe(orden);
        referencia("Técnicos asignados a la orden #" + orden, () -> vista.mostrarTecnicos(asignados));
        int anterior = consola.leerEntero("Id del técnico a reemplazar: ");
        referencia("Técnicos activos disponibles", () -> vista.mostrarTecnicos(servicio.listarTecnicosActivos()
                .stream()
                .filter(t -> asignados.stream().noneMatch(a -> a.getId() == t.getId()))
                .toList()));
        int nuevo = consola.leerEntero("Id del técnico nuevo: ");
        servicio.reemplazarTecnico(orden, anterior, nuevo);
        consola.mostrarExito("Técnico reemplazado.");
    }

    private void quitarTecnico() throws SQLException {
        int orden = pedirOrdenAsignable();
        referencia("Técnicos asignados a la orden #" + orden, () -> vista.mostrarTecnicos(servicio.tecnicosDe(orden)));
        servicio.quitarTecnico(orden, consola.leerEntero("Id del técnico a quitar: "));
        consola.mostrarExito("Técnico retirado de la orden.");
    }

    private void asignarRepuesto() throws SQLException {
        int orden = pedirOrdenAsignable();
        referencia("Repuestos del inventario (columna Disp = unidades libres)",
                () -> inventario.mostrarRepuestos(servicio.listarRepuestos()));
        int repuesto = consola.leerEntero("Id del repuesto: ");
        int cantidad = consola.leerEntero("Cantidad: ");
        servicio.asignarRepuesto(orden, repuesto, cantidad);
        consola.mostrarExito("Repuesto asignado y RESERVADO en inventario.");
    }

    private void quitarRepuesto() throws SQLException {
        int orden = pedirOrdenAsignable();
        referencia("Repuestos de la orden #" + orden, () -> vista.mostrarRepuestosOrden(servicio.repuestosDe(orden)));
        servicio.quitarRepuesto(orden, consola.leerEntero("Id del repuesto a quitar: "));
        consola.mostrarExito("Repuesto retirado; su reserva volvió al disponible.");
    }

    private void iniciarOrden() throws SQLException {
        servicio.iniciar(pedirOrdenEn(EstadoServicio.PENDIENTE));
        consola.mostrarExito("Orden en proceso.");
    }

    private void completar() throws SQLException {
        int orden = pedirOrdenEn(EstadoServicio.EN_PROCESO);
        if (consola.confirmar("Se descontarán del inventario los repuestos reservados.")) {
            servicio.completar(orden);
            consola.mostrarExito("Orden completada; stock deducido. Ya puede facturarse.");
        } else {
            consola.mostrar("Operación cancelada.");
        }
    }

    private void entregar() throws SQLException {
        servicio.entregar(pedirOrdenEn(EstadoServicio.FACTURADO));
        consola.mostrarExito("Vehículo entregado al cliente.");
    }

    private void cancelarOrden() throws SQLException {
        int orden = pedirOrdenEn(EstadoServicio.PENDIENTE);
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
        referenciaTecnicos();
        int id = consola.leerEntero("Id del técnico: ");
        String nombre = consola.leerTextoObligatorio("Nombre: ");
        String especialidad = consola.leerTexto("Especialidad: ");
        servicio.actualizarTecnico(id, nombre, especialidad);
        consola.mostrarExito("Técnico actualizado.");
    }

    private void cambiarActividad() throws SQLException {
        referenciaTecnicos();
        int id = consola.leerEntero("Id del técnico: ");
        boolean activo = consola.leerEntero("1 = activar, 2 = desactivar: ") == 1;
        servicio.cambiarActividadTecnico(id, activo);
        consola.mostrarExito("Técnico " + (activo ? "activado." : "desactivado."));
    }

    private void eliminarTecnico() throws SQLException {
        referenciaTecnicos();
        servicio.eliminarTecnico(consola.leerEntero("Id del técnico: "));
        consola.mostrarExito("Técnico eliminado.");
    }

    // ------------------------------------------------------------ Referencias antes de pedir un dato

    private TipoMantenimiento pedirTipoMantenimiento() throws SQLException {
        referencia("Tipos de mantenimiento", () -> vista.mostrarTiposMantenimiento());
        return TipoMantenimiento.desdeId(consola.leerEntero("Tipo de mantenimiento: "));
    }

    /** Muestra solo las órdenes que aún admiten técnicos y repuestos, y pide el número. */
    private int pedirOrdenAsignable() throws SQLException {
        referencia("Órdenes Pendientes o En Proceso", () -> vista.mostrarOrdenes(servicio.listarAsignables()));
        return consola.leerEntero("Número de orden: ");
    }

    /** Muestra solo las órdenes en el estado que la operación necesita, y pide el número. */
    private int pedirOrdenEn(EstadoServicio estado) throws SQLException {
        referencia("Órdenes en estado " + estado.getDescripcion(),
                () -> vista.mostrarOrdenes(servicio.listarPorEstado(estado)));
        return consola.leerEntero("Número de orden: ");
    }

    private void referenciaTecnicos() throws SQLException {
        referencia("Técnicos registrados", () -> vista.mostrarTecnicos(servicio.listarTecnicos()));
    }

    // Valor opcional tecleado al actualizar: si no es un número se informa sin cerrar la aplicación.
    private int entero(String texto) {
        try {
            // Visto en clase: conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
            return Integer.parseInt(texto.strip());
        } catch (NumberFormatException error) {
            throw new DatoInvalidoException("'" + texto + "' no es un número entero.");
        }
    }
}
