/*
 * ============================================================
 * Archivo: FabricaEstadoOrden.java
 * Propósito: convertir el EstadoServicio guardado en la BD en el objeto
 *            State correspondiente.
 * Conceptos clave: fábrica simple con if / else if (S02DemoDecisiones) y
 *            comparación de enum con == (S10Demo02),
 *            clase utilitaria con constructor privado.
 * Justificación: la BD guarda un id; el comportamiento vive en objetos.
 *            Un único punto de creación evita repetir la decisión en cada
 *            servicio que necesite el estado (DRY) y oculta las clases concretas.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.servicio.estado;

import com.carmotors.excepcion.DatoInvalidoException;
import com.carmotors.modelo.EstadoServicio;

// Visto en clase: PATRÓN STATE -> S14Demo04 EstadoPedido (Sesión 14)
public final class FabricaEstadoOrden {
    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    private FabricaEstadoOrden() {
    }

    // Visto en clase: métodos y miembros static -> S05Demo02ConversorTemperatura (Sesión 05)
    public static EstadoOrden crear(EstadoServicio estado) {
        // Comparación de constantes enum con == (S10Demo02 DiaSemana).
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        if (estado == EstadoServicio.PENDIENTE) {
            return new EstadoPendiente();
        } else if (estado == EstadoServicio.EN_PROCESO) {
            return new EstadoEnProceso();
        } else if (estado == EstadoServicio.COMPLETADO) {
            return new EstadoCompletado();
        } else if (estado == EstadoServicio.FACTURADO) {
            return new EstadoFacturado();
        } else if (estado == EstadoServicio.ENTREGADO) {
            return new EstadoEntregado();
        }
        // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
        throw new DatoInvalidoException("Estado de servicio desconocido: " + estado);
    }
}
