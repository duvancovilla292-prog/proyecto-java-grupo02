/*
 * ============================================================
 * Archivo: MenuPrincipalController.java
 * Propósito: menú raíz que enruta a cada módulo del sistema; además
 *            comprueba la conexión al arrancar y conecta las alertas de stock
 *            (Observer) con la consola.
 * Conceptos clave: MVC - Controlador, COMMAND (cada opción es el método
 *            iniciar() de un submódulo: referencia a método de otro objeto),
 *            hooks del TEMPLATE METHOD (alIniciar/alSalir), polimorfismo.
 * Justificación: no conoce la lógica de ningún módulo ni los textos del menú
 *            (los da la vista MenuPrincipal); agregar un módulo nuevo es
 *            registrar una línea más (OCP). Cada módulo se crea SOLO cuando
 *            el usuario entra a él: aquí se crea su vista y se le pasa al
 *            controlador (como S18Demo01UsuariosMain con UsuarioController);
 *            el controlador crea su servicio y cada servicio sus DAO, así Main
 *            no instancia nada de las capas inferiores.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.config.ConexionFactory;
import com.carmotors.excepcion.ConfiguracionException;
import com.carmotors.servicio.observador.AlertaStockObservador;
import com.carmotors.servicio.observador.PublicadorInventario;
import com.carmotors.vista.ClienteVista;
import com.carmotors.vista.FacturacionVista;
import com.carmotors.vista.InventarioVista;
import com.carmotors.vista.MenuPrincipal;
import com.carmotors.vista.OrdenServicioVista;
import com.carmotors.vista.ProveedorVista;
import com.carmotors.vista.ReporteVista;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;

// Visto en clase: herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
// Visto en clase: PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public class MenuPrincipalController extends ControladorBase {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private final MenuPrincipal vista;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public MenuPrincipalController(VistaConsola consola, MenuPrincipal vista) {
        super(consola, vista);
        this.vista = vista;
        // Observer: las alertas de stock bajo se muestran automáticamente en la consola.
        // Visto en clase: PATRÓN SINGLETON -> S12Demo04 ConfiguracionSistema (Sesión 12)
        // Visto en clase: PATRÓN OBSERVER -> S14Demo01 PublicadorPedidos (Sesión 14)
        // Visto en clase: referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
        PublicadorInventario.getInstancia().suscribir(new AlertaStockObservador(consola::mostrarAlerta));
        // Command: cada opción crea el módulo en el momento en que se necesita.
        // Visto en clase: expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
        registrar("1", () -> new InventarioController(consola, new InventarioVista(consola)).iniciar());
        registrar("2", () -> new OrdenServicioController(consola, new OrdenServicioVista(consola)).iniciar());
        registrar("3", () -> new ClienteController(consola, new ClienteVista(consola)).iniciar());
        registrar("4", () -> new FacturacionController(consola, new FacturacionVista(consola)).iniciar());
        registrar("5", () -> new ProveedorController(consola, new ProveedorVista(consola)).iniciar());
        registrar("6", () -> new ReporteController(consola, new ReporteVista(consola)).iniciar());
    }

    /** Comprueba la configuración y la conexión con MySQL; false si no se puede continuar. */
    public boolean conectar() {
        vista.mostrarInicio();
        // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
        try {
            if (!ConexionFactory.getInstancia().probarConexion()) {
                consola.mostrarError("La conexión a MySQL no es válida.");
                return false;
            }
        } catch (ConfiguracionException error) {
            consola.mostrarError(error.getMessage());
            vista.mostrarAyudaConexion();
            return false;
        } catch (SQLException error) {
            consola.mostrarError("No fue posible conectar a MySQL (SQLState " + error.getSQLState() + "): "
                    + error.getMessage());
            vista.mostrarAyudaConexion();
            return false;
        }
        consola.mostrarExito("Conexión con la base de datos comprobada.");
        return true;
    }

    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    // Visto en clase: modificador protected -> S07Demo00 Usuario.describirIdentidad (Sesión 07)
    @Override
    protected void alIniciar() {
        vista.mostrarBienvenida();
    }

    @Override
    protected void alSalir() {
        vista.mostrarDespedida();
    }
}
