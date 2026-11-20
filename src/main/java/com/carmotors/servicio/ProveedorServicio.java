/*
 * ============================================================
 * Archivo: ProveedorServicio.java
 * Propósito: lógica del Módulo de Gestión de Proveedores: registro,
 *            catálogo de productos con costos y evaluación de desempeño
 *            (el ranking de desempeño está en ReporteServicio).
 * Conceptos clave: Streams (filter, mapToInt, sum, count, sorted, anyMatch),
 *            Comparator declarado como constante y Set (HashSet) para evitar
 *            duplicados (sesiones 10 y 19),
 *            Comparator con desempate (S19Demo03), división decimal.
 * Justificación: registra las evaluaciones individuales que luego resume
 *            el Módulo de Reportes para responder "¿qué proveedor ofrece
 *            mejores productos y precios?" sin lógica en la vista.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.servicio;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.jdbc.CatalogoProveedorDAOJdbc;
import com.carmotors.dao.jdbc.EvaluacionProveedorDAOJdbc;
import com.carmotors.dao.jdbc.LoteDAOJdbc;
import com.carmotors.dao.jdbc.ProveedorDAOJdbc;
import com.carmotors.dao.jdbc.RepuestoDAOJdbc;
import com.carmotors.dao.CatalogoProveedorDAO;
import com.carmotors.dao.EvaluacionProveedorDAO;
import com.carmotors.dao.LoteDAO;
import com.carmotors.dao.ProveedorDAO;
import com.carmotors.dao.RepuestoDAO;
import com.carmotors.excepcion.RecursoNoEncontradoException;
import com.carmotors.excepcion.ReglaNegocioException;
import com.carmotors.modelo.EvaluacionProveedor;
import com.carmotors.modelo.ProductoCatalogo;
import com.carmotors.modelo.Proveedor;
import com.carmotors.modelo.Repuesto;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProveedorServicio {
    // Ofertas de la más barata a la más cara.
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    // Visto en clase: expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
    // Visto en clase: Predicate<T> / Comparator<T> / compareTo -> S19Demo03ReglasOrdenMain (Sesión 19)
    private static final Comparator<ProductoCatalogo> POR_COSTO = (a, b) -> Integer.compare(a.getCosto(), b.getCosto());

    // Visto en clase: PATRÓN DAO (interfaz + implementación) -> S18Demo01 UsuarioDAO (Sesión 18)
    private final ProveedorDAO proveedorDAO;
    private final CatalogoProveedorDAO catalogoDAO;
    private final EvaluacionProveedorDAO evaluacionDAO;
    private final RepuestoDAO repuestoDAO;
    private final LoteDAO loteDAO;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    /** Crea sus propios DAO JDBC con la conexión del Singleton. */
    public ProveedorServicio() {
        this.proveedorDAO = new ProveedorDAOJdbc(ConexionFactory.getInstancia());
        this.catalogoDAO = new CatalogoProveedorDAOJdbc(ConexionFactory.getInstancia());
        this.evaluacionDAO = new EvaluacionProveedorDAOJdbc(ConexionFactory.getInstancia());
        this.repuestoDAO = new RepuestoDAOJdbc(ConexionFactory.getInstancia());
        this.loteDAO = new LoteDAOJdbc(ConexionFactory.getInstancia());
    }

    // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
    public int registrarProveedor(String nombre, String contacto, String telefono, String email) throws SQLException {
        return proveedorDAO.insertar(new Proveedor(0, nombre, contacto, telefono, email));
    }

    public void actualizarProveedor(int id, String nombre, String contacto, String telefono, String email)
            throws SQLException {
        obtenerProveedor(id);
        proveedorDAO.actualizar(new Proveedor(id, nombre, contacto, telefono, email));
    }

    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    public List<Proveedor> listarProveedores() throws SQLException {
        return proveedorDAO.listar();
    }

    /** Inventario: referencia al pedir el id del repuesto. */
    public List<Repuesto> listarRepuestos() throws SQLException {
        return repuestoDAO.listar();
    }

    /** Todas las evaluaciones: referencia al pedir el id de una evaluación. */
    public List<EvaluacionProveedor> listarEvaluaciones() throws SQLException {
        return evaluacionDAO.listar();
    }

    public Proveedor obtenerProveedor(int id) throws SQLException {
        Proveedor proveedor = proveedorDAO.buscarPorId(id);
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (proveedor == null) {
            throw new RecursoNoEncontradoException("No existe el proveedor " + id + ".");
        }
        return proveedor;
    }

    /** Agrega el repuesto al catálogo del proveedor o actualiza su costo. Devuelve true si actualizó. */
    public boolean registrarEnCatalogo(int proveedorId, int repuestoId, int costo) throws SQLException {
        obtenerProveedor(proveedorId);
        if (repuestoDAO.buscarPorId(repuestoId) == null) {
            throw new RecursoNoEncontradoException("No existe el repuesto " + repuestoId + ".");
        }
        ProductoCatalogo producto = new ProductoCatalogo(proveedorId, "", repuestoId, "", costo);
        // Set: repuestos que ya ofrece el proveedor, sin duplicados y con contains() directo.
        // Visto en clase: Set / HashSet -> S10Demo00SetMain (Sesión 10)
        // Visto en clase: Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
        // Visto en clase: referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
        Set<Integer> yaOfrecidos = new HashSet<>(catalogoDAO.listarPorProveedor(proveedorId).stream()
                .map(ProductoCatalogo::getRepuestoId)
                .toList());
        // Visto en clase: toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
        boolean existe = yaOfrecidos.contains(repuestoId);
        if (existe) {
            catalogoDAO.actualizarCosto(proveedorId, repuestoId, producto.getCosto());
        } else {
            catalogoDAO.insertar(producto);
        }
        return existe;
    }

    public List<ProductoCatalogo> catalogoDe(int proveedorId) throws SQLException {
        obtenerProveedor(proveedorId);
        return catalogoDAO.listarPorProveedor(proveedorId);
    }

    /** Ofertas del mismo repuesto entre proveedores, de la más barata a la más cara. */
    public List<ProductoCatalogo> compararPrecios(int repuestoId) throws SQLException {
        // Visto en clase: Stream: distinct / sorted / mapToInt+sum / reduce(0, Integer::sum) -> S20Demo02OrdenesMain (Sesión 20)
        return catalogoDAO.listarPorRepuesto(repuestoId).stream()
                .sorted(POR_COSTO)
                .toList();
    }

    public int evaluar(int proveedorId, int calidad, int puntualidad, String comentario) throws SQLException {
        obtenerProveedor(proveedorId);
        // Visto en clase: funciones de MySQL en el SQL (NOW, CURDATE, DATE_ADD, LAST_INSERT_ID) -> son SQL de MySQL, no Java; en Java solo se vio LocalDate.of (S12Demo02)
        return evaluacionDAO.insertar(new EvaluacionProveedor(0, proveedorId, calidad, puntualidad,
                null, comentario));  // la fecha la asigna MySQL (CURDATE())
    }

    /**
     * Elimina un proveedor sin repuestos asociados ni lotes entregados.
     * Antes borra su catálogo y sus evaluaciones (orquestación de varios DAO).
     */
    public void eliminarProveedor(int id) throws SQLException {
        obtenerProveedor(id);
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: Stream: count / anyMatch / Arrays.stream -> S20Demo01FuentesMain (Sesión 20)
        if (repuestoDAO.listar().stream().anyMatch(r -> r.getProveedorId() == id)
                || loteDAO.listar().stream().anyMatch(l -> l.getProveedorId() == id)) {
            throw new ReglaNegocioException("El proveedor tiene repuestos o lotes asociados; no se puede eliminar.");
        }
        // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
        for (ProductoCatalogo producto : catalogoDAO.listarPorProveedor(id)) {
            catalogoDAO.eliminar(id, producto.getRepuestoId());
        }
        for (EvaluacionProveedor evaluacion : evaluacionDAO.listarPorProveedor(id)) {
            evaluacionDAO.eliminar(evaluacion.getId());
        }
        proveedorDAO.eliminar(id);
    }

    public void quitarDelCatalogo(int proveedorId, int repuestoId) throws SQLException {
        if (catalogoDAO.eliminar(proveedorId, repuestoId) == 0) {
            throw new RecursoNoEncontradoException("Ese repuesto no está en el catálogo del proveedor.");
        }
    }

    public List<EvaluacionProveedor> evaluacionesDe(int proveedorId) throws SQLException {
        obtenerProveedor(proveedorId);
        return evaluacionDAO.listarPorProveedor(proveedorId);
    }

    public void corregirEvaluacion(int evaluacionId, int calidad, int puntualidad, String comentario)
            throws SQLException {
        EvaluacionProveedor actual = obtenerEvaluacion(evaluacionId);
        evaluacionDAO.actualizar(new EvaluacionProveedor(actual.getId(), actual.getProveedorId(), calidad, puntualidad,
                actual.getFecha(), comentario));
    }

    public void eliminarEvaluacion(int evaluacionId) throws SQLException {
        obtenerEvaluacion(evaluacionId);
        evaluacionDAO.eliminar(evaluacionId);
    }

    private EvaluacionProveedor obtenerEvaluacion(int id) throws SQLException {
        EvaluacionProveedor evaluacion = evaluacionDAO.buscarPorId(id);
        if (evaluacion == null) {
            throw new RecursoNoEncontradoException("No existe la evaluación " + id + ".");
        }
        return evaluacion;
    }
}
