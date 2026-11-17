/*
 * ============================================================
 * Archivo: ProveedorServicio.java
 * Propósito: lógica del Módulo de Gestión de Proveedores: registro,
 *            catálogo de productos con costos, evaluación de desempeño
 *            y ranking.
 * Conceptos clave: Streams (filter, mapToInt, sum, count, sorted, anyMatch),
 *            Comparator con desempate (S19Demo03), división decimal.
 * Justificación: el ranking se calcula en memoria a partir de las
 *            evaluaciones individuales; así se responde "¿qué proveedor
 *            ofrece mejores productos y precios?" sin lógica en la vista.
 * ============================================================
 */
package com.carmotors.servicio;

import com.carmotors.dao.CatalogoProveedorDAO;
import com.carmotors.dao.EvaluacionProveedorDAO;
import com.carmotors.dao.LoteDAO;
import com.carmotors.dao.ProveedorDAO;
import com.carmotors.dao.RepuestoDAO;
import com.carmotors.excepcion.RecursoNoEncontradoException;
import com.carmotors.excepcion.ReglaNegocioException;
import com.carmotors.modelo.DesempenoProveedor;
import com.carmotors.modelo.EvaluacionProveedor;
import com.carmotors.modelo.ProductoCatalogo;
import com.carmotors.modelo.Proveedor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorServicio {
    private final ProveedorDAO proveedorDAO;
    private final CatalogoProveedorDAO catalogoDAO;
    private final EvaluacionProveedorDAO evaluacionDAO;
    private final RepuestoDAO repuestoDAO;
    private final LoteDAO loteDAO;

    public ProveedorServicio(ProveedorDAO proveedorDAO, CatalogoProveedorDAO catalogoDAO,
                             EvaluacionProveedorDAO evaluacionDAO, RepuestoDAO repuestoDAO, LoteDAO loteDAO) {
        this.proveedorDAO = proveedorDAO;
        this.catalogoDAO = catalogoDAO;
        this.evaluacionDAO = evaluacionDAO;
        this.repuestoDAO = repuestoDAO;
        this.loteDAO = loteDAO;
    }

    public int registrarProveedor(String nombre, String contacto, String telefono, String email) throws SQLException {
        return proveedorDAO.insertar(new Proveedor(0, nombre, contacto, telefono, email));
    }

    public void actualizarProveedor(int id, String nombre, String contacto, String telefono, String email)
            throws SQLException {
        obtenerProveedor(id);
        proveedorDAO.actualizar(new Proveedor(id, nombre, contacto, telefono, email));
    }

    public List<Proveedor> listarProveedores() throws SQLException {
        return proveedorDAO.listar();
    }

    public Proveedor obtenerProveedor(int id) throws SQLException {
        Proveedor proveedor = proveedorDAO.buscarPorId(id);
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
        boolean existe = catalogoDAO.listarPorProveedor(proveedorId).stream()
                .anyMatch(p -> p.getRepuestoId() == repuestoId);
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
        return catalogoDAO.listarPorRepuesto(repuestoId).stream()
                .sorted((a, b) -> Integer.compare(a.getCosto(), b.getCosto()))
                .toList();
    }

    public int evaluar(int proveedorId, int calidad, int puntualidad, String comentario) throws SQLException {
        obtenerProveedor(proveedorId);
        return evaluacionDAO.insertar(new EvaluacionProveedor(0, proveedorId, calidad, puntualidad,
                null, comentario));  // la fecha la asigna MySQL (CURDATE())
    }

    /** Ranking de desempeño: mayor puntaje primero; desempata por nombre. */
    public List<DesempenoProveedor> rankingDesempeno() throws SQLException {
        List<EvaluacionProveedor> evaluaciones = evaluacionDAO.listar();
        List<DesempenoProveedor> resumen = new ArrayList<>();
        for (Proveedor proveedor : proveedorDAO.listar()) {
            List<EvaluacionProveedor> propias = evaluaciones.stream()
                    .filter(e -> e.getProveedorId() == proveedor.getId())
                    .toList();
            int cantidad = propias.size();
            double calidad = cantidad == 0 ? 0 : (double) propias.stream().mapToInt(EvaluacionProveedor::getCalidad).sum() / cantidad;
            double puntualidad = cantidad == 0 ? 0 : (double) propias.stream().mapToInt(EvaluacionProveedor::getPuntualidad).sum() / cantidad;
            resumen.add(new DesempenoProveedor(proveedor, cantidad, calidad, puntualidad));
        }
        return resumen.stream()
                .sorted((a, b) -> {
                    // Puntaje con una cifra decimal convertido a entero (cast de S04) para comparar con Integer.compare.
                    int porPuntaje = Integer.compare((int) (b.puntajeGeneral() * 10), (int) (a.puntajeGeneral() * 10));
                    return porPuntaje != 0 ? porPuntaje
                            : a.getProveedor().getNombre().compareTo(b.getProveedor().getNombre());
                })
                .toList();
    }

    /**
     * Elimina un proveedor sin repuestos asociados ni lotes entregados.
     * Antes borra su catálogo y sus evaluaciones (orquestación de varios DAO).
     */
    public void eliminarProveedor(int id) throws SQLException {
        obtenerProveedor(id);
        if (repuestoDAO.listar().stream().anyMatch(r -> r.getProveedorId() == id)
                || loteDAO.listar().stream().anyMatch(l -> l.getProveedorId() == id)) {
            throw new ReglaNegocioException("El proveedor tiene repuestos o lotes asociados; no se puede eliminar.");
        }
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
