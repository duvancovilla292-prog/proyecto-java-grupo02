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
 *            el usuario entra a él (cada controlador crea su vista y su
 *            servicio, y cada servicio sus DAO), así Main no instancia nada
 *            de las capas inferiores.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
 *   - @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
 *   - modificador protected -> S07Demo00 Usuario.describirIdentidad (Sesión 07)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN SINGLETON -> S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - PATRÓN OBSERVER -> S14Demo01 PublicadorPedidos (Sesión 14)
 *   - PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
 *   - referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 * ============================================================
 */
package com.carmotors.controlador;

import com.carmotors.config.ConexionFactory;
import com.carmotors.excepcion.ConfiguracionException;
import com.carmotors.servicio.observador.AlertaStockObservador;
import com.carmotors.servicio.observador.PublicadorInventario;
import com.carmotors.vista.MenuPrincipal;
import com.carmotors.vista.VistaConsola;

import java.sql.SQLException;

public class MenuPrincipalController extends ControladorBase {
    private final MenuPrincipal vista;

    public MenuPrincipalController(VistaConsola consola, MenuPrincipal vista) {
        super(consola, vista);
        this.vista = vista;
        // Observer: las alertas de stock bajo se muestran automáticamente en la consola.
        PublicadorInventario.getInstancia().suscribir(new AlertaStockObservador(consola::mostrarAlerta));
        // Command: cada opción crea el módulo en el momento en que se necesita.
        registrar("1", () -> new InventarioController(consola).iniciar());
        registrar("2", () -> new OrdenServicioController(consola).iniciar());
        registrar("3", () -> new ClienteController(consola).iniciar());
        registrar("4", () -> new FacturacionController(consola).iniciar());
        registrar("5", () -> new ProveedorController(consola).iniciar());
        registrar("6", () -> new ReporteController(consola).iniciar());
    }

    /** Comprueba la configuración y la conexión con MySQL; false si no se puede continuar. */
    public boolean conectar() {
        vista.mostrarInicio();
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

    @Override
    protected void alIniciar() {
        vista.mostrarBienvenida();
    }

    @Override
    protected void alSalir() {
        vista.mostrarDespedida();
    }
}
