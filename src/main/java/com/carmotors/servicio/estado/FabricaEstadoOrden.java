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
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - métodos y miembros static -> S05Demo02ConversorTemperatura (Sesión 05)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - PATRÓN STATE -> S14Demo04 EstadoPedido (Sesión 14)
 * ============================================================
 */
package com.carmotors.servicio.estado;

import com.carmotors.excepcion.DatoInvalidoException;
import com.carmotors.modelo.EstadoServicio;

public final class FabricaEstadoOrden {
    private FabricaEstadoOrden() {
    }

    public static EstadoOrden crear(EstadoServicio estado) {
        // Comparación de constantes enum con == (S10Demo02 DiaSemana).
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
        throw new DatoInvalidoException("Estado de servicio desconocido: " + estado);
    }
}
