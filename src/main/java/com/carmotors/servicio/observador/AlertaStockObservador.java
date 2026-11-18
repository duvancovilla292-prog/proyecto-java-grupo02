/*
 * ============================================================
 * Archivo: AlertaStockObservador.java
 * Propósito: observador concreto que convierte la alerta en un mensaje de texto.
 * Conceptos clave: PATRÓN OBSERVER (observador concreto), Consumer<String>
 *            (S19Demo01) recibido por constructor.
 * Justificación: el observador NO imprime por sí mismo (eso es tarea de la
 *            vista); recibe un Consumer, por ejemplo vista::mostrarAlerta, y
 *            le entrega el texto. Así la capa de servicio no depende de la consola.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
 *   - interfaces e implements -> S07Demo04 Notificable (Sesión 07)
 *   - Objects.requireNonNull -> S15Demo00 VerificacionPedido (Sesión 15) y S20Demo02 Orden (Sesión 20)
 *   - PATRÓN OBSERVER -> S14Demo01 PublicadorPedidos (Sesión 14)
 *   - Consumer<T> -> S19Demo01AccionProveedorMain (Sesión 19)
 * ============================================================
 */
package com.carmotors.servicio.observador;

import com.carmotors.modelo.Repuesto;

import java.util.Objects;
import java.util.function.Consumer;

public class AlertaStockObservador implements ObservadorInventario {
    private final Consumer<String> salida;  // destino del mensaje (inyectado)

    public AlertaStockObservador(Consumer<String> salida) {
        this.salida = Objects.requireNonNull(salida);
    }

    @Override
    public void stockBajo(Repuesto repuesto) {
        salida.accept("Stock bajo: " + repuesto.getNombre() + " | disponible " + repuesto.getDisponible()
                + " | mínimo " + repuesto.getStockMinimo() + ". Programe una compra.");
    }
}
