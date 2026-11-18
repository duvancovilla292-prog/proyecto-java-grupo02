/*
 * ============================================================
 * Archivo: Main.java
 * Propósito: punto de entrada (main) y RAÍZ DE COMPOSICIÓN: crea y conecta
 *            todas las piezas (Singleton de conexión -> DAO -> servicios ->
 *            vistas -> controladores) y arranca el menú principal.
 * Conceptos clave: inyección de dependencias manual por constructor
 *            (como el Main de S18Demo01, que "solo ensambla"), PATRÓN
 *            SINGLETON (ConexionFactory), OBSERVER (suscripción con vista::mostrarAlerta),
 *            STRATEGY (se elige ImpuestoIva(19)), FACADE (TallerFacade), try-with-resources sobre Scanner.
 * Justificación: es el ÚNICO lugar que conoce las clases concretas
 *            (XxxDAOJdbc, ImpuestoIva...). El resto del sistema depende de
 *            interfaces (DIP): cambiar MySQL por otro almacenamiento o la
 *            política de impuestos se hace aquí, sin tocar servicios ni vistas.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - métodos y miembros static -> S05Demo02ConversorTemperatura (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - Scanner y hasNextLine -> S03Scanner (Sesión 03) y S18Demo01 VistaConsola (Sesión 18)
 *   - arreglos y split -> S08Demo00ArreglosMain (Sesión 08) y S17Demo02LecturaMain split (Sesión 17)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN SINGLETON -> S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - PATRÓN FACADE -> S13Demo01 CheckoutFacade (Sesión 13)
 *   - PATRÓN OBSERVER -> S14Demo01 PublicadorPedidos (Sesión 14)
 *   - Path / Files / BufferedWriter / StandardOpenOption -> S17Demo00RutasMain, S17Demo01EscrituraMain y S17Demo03GestionMain (Sesión 17)
 *   - PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 *   - referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
 * ============================================================
 */
package com.carmotors;

import com.carmotors.config.ConexionFactory;
import com.carmotors.controlador.ClienteController;
import com.carmotors.controlador.FacturacionController;
import com.carmotors.controlador.InventarioController;
import com.carmotors.controlador.MenuPrincipalController;
import com.carmotors.controlador.OrdenServicioController;
import com.carmotors.controlador.ProveedorController;
import com.carmotors.controlador.ReporteController;
import com.carmotors.dao.AsignacionTecnicoDAO;
import com.carmotors.dao.ClienteDAO;
import com.carmotors.dao.DetalleRepuestoDAO;
import com.carmotors.dao.LoteDAO;
import com.carmotors.dao.OrdenServicioDAO;
import com.carmotors.dao.ProveedorDAO;
import com.carmotors.dao.RepuestoDAO;
import com.carmotors.dao.TecnicoDAO;
import com.carmotors.dao.VehiculoDAO;
import com.carmotors.dao.jdbc.AsignacionTecnicoDAOJdbc;
import com.carmotors.dao.jdbc.CatalogoProveedorDAOJdbc;
import com.carmotors.dao.jdbc.ClienteDAOJdbc;
import com.carmotors.dao.jdbc.DetalleRepuestoDAOJdbc;
import com.carmotors.dao.jdbc.EvaluacionProveedorDAOJdbc;
import com.carmotors.dao.jdbc.FacturaDAOJdbc;
import com.carmotors.dao.jdbc.LoteDAOJdbc;
import com.carmotors.dao.jdbc.OrdenServicioDAOJdbc;
import com.carmotors.dao.jdbc.ProveedorDAOJdbc;
import com.carmotors.dao.jdbc.ReporteDAOJdbc;
import com.carmotors.dao.jdbc.RepuestoDAOJdbc;
import com.carmotors.dao.jdbc.TecnicoDAOJdbc;
import com.carmotors.dao.jdbc.TipoRepuestoDAOJdbc;
import com.carmotors.dao.jdbc.VehiculoDAOJdbc;
import com.carmotors.excepcion.ConfiguracionException;
import com.carmotors.servicio.ClienteServicio;
import com.carmotors.servicio.FacturacionServicio;
import com.carmotors.servicio.InventarioServicio;
import com.carmotors.servicio.OrdenServicioServicio;
import com.carmotors.servicio.ProveedorServicio;
import com.carmotors.servicio.ReporteServicio;
import com.carmotors.servicio.TallerFacade;
import com.carmotors.servicio.impuesto.ImpuestoIva;
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

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final int IVA_COLOMBIA = 19;                         // porcentaje
    private static final Path CARPETA_FACTURAS = Path.of("facturas");  // relativa a la raíz del proyecto

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            VistaConsola consola = new VistaConsola(entrada);
            MenuPrincipal menuPrincipal = new MenuPrincipal(consola);
            menuPrincipal.mostrarInicio();

            // 1) Singleton: una sola configuración de conexión para toda la aplicación.
            ConexionFactory conexion;
            try {
                conexion = ConexionFactory.getInstancia();
                if (!conexion.probarConexion()) {
                    consola.mostrarError("La conexión a MySQL no es válida.");
                    return;
                }
            } catch (ConfiguracionException error) {
                consola.mostrarError(error.getMessage());
                menuPrincipal.mostrarAyudaConexion();
                return;
            } catch (SQLException error) {
                consola.mostrarError("No fue posible conectar a MySQL (SQLState " + error.getSQLState() + "): "
                        + error.getMessage());
                menuPrincipal.mostrarAyudaConexion();
                return;
            }
            consola.mostrarExito("Conexión con la base de datos comprobada.");

            // 2) DAO (implementaciones JDBC detrás de interfaces).
            ClienteDAO clienteDAO = new ClienteDAOJdbc(conexion);
            VehiculoDAO vehiculoDAO = new VehiculoDAOJdbc(conexion);
            ProveedorDAO proveedorDAO = new ProveedorDAOJdbc(conexion);
            RepuestoDAO repuestoDAO = new RepuestoDAOJdbc(conexion);
            TecnicoDAO tecnicoDAO = new TecnicoDAOJdbc(conexion);
            OrdenServicioDAO ordenDAO = new OrdenServicioDAOJdbc(conexion);
            AsignacionTecnicoDAO asignacionDAO = new AsignacionTecnicoDAOJdbc(conexion);
            DetalleRepuestoDAO detalleDAO = new DetalleRepuestoDAOJdbc(conexion);

            // 3) Observer: las alertas de stock bajo se muestran automáticamente en consola.
            PublicadorInventario publicador = new PublicadorInventario();
            publicador.suscribir(new AlertaStockObservador(consola::mostrarAlerta));

            // 4) Servicios (lógica de negocio).
            LoteDAO loteDAO = new LoteDAOJdbc(conexion);
            InventarioServicio inventario = new InventarioServicio(repuestoDAO, new TipoRepuestoDAOJdbc(conexion),
                    loteDAO, proveedorDAO, publicador);
            ClienteServicio clientes = new ClienteServicio(clienteDAO, vehiculoDAO, ordenDAO);
            OrdenServicioServicio ordenes = new OrdenServicioServicio(ordenDAO, vehiculoDAO, tecnicoDAO,
                    asignacionDAO, detalleDAO, inventario);
            ProveedorServicio proveedores = new ProveedorServicio(proveedorDAO, new CatalogoProveedorDAOJdbc(conexion),
                    new EvaluacionProveedorDAOJdbc(conexion), repuestoDAO, loteDAO);
            FacturacionServicio facturacion = new FacturacionServicio(new FacturaDAOJdbc(conexion), clienteDAO,
                    ordenes, new ImpuestoIva(IVA_COLOMBIA), CARPETA_FACTURAS);  // Strategy elegida
            ReporteServicio reportes = new ReporteServicio(new ReporteDAOJdbc(conexion));

            // 5) Controladores con sus vistas, y menú principal.
            MenuPrincipalController menu = new MenuPrincipalController(consola, menuPrincipal,
                    new InventarioController(consola, new InventarioVista(consola), inventario),
                    new OrdenServicioController(consola, new OrdenServicioVista(consola), ordenes,
                            new TallerFacade(clientes, ordenes)),  // Facade
                    new ClienteController(consola, new ClienteVista(consola), clientes),
                    new FacturacionController(consola, new FacturacionVista(consola), facturacion),
                    new ProveedorController(consola, new ProveedorVista(consola), proveedores),
                    new ReporteController(consola, new ReporteVista(consola), reportes));
            menu.iniciar();  // bienvenida -> menú -> despedida (Template Method con hooks)
        }
    }
}
