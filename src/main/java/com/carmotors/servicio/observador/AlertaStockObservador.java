/*
 * ============================================================
 * Archivo: AlertaStockObservador.java
 * Propósito: observador concreto que convierte la alerta en un mensaje de texto.
 * Conceptos clave: PATRÓN OBSERVER (observador concreto), Consumer<String>
 *            (S19Demo01) recibido por constructor.
 * Justificación: el observador NO imprime por sí mismo (eso es tarea de la
 *            vista); recibe un Consumer, por ejemplo vista::mostrarAlerta, y
 *            le entrega el texto. Así la capa de servicio no depende de la consola.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.servicio.observador;

import com.carmotors.modelo.Repuesto;

import java.util.Objects;
import java.util.function.Consumer;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: PATRÓN OBSERVER -> S14Demo01 PublicadorPedidos (Sesión 14)
public class AlertaStockObservador implements ObservadorInventario {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: Consumer<T> -> S19Demo01AccionProveedorMain (Sesión 19)
    private final Consumer<String> salida;  // destino del mensaje (inyectado)

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public AlertaStockObservador(Consumer<String> salida) {
        // Visto en clase: Objects.requireNonNull -> S15Demo00 VerificacionPedido (Sesión 15) y S20Demo02 Orden (Sesión 20)
        this.salida = Objects.requireNonNull(salida);
    }

    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    @Override
    public void stockBajo(Repuesto repuesto) {
        salida.accept("Stock bajo: " + repuesto.getNombre() + " | disponible " + repuesto.getDisponible()
                + " | mínimo " + repuesto.getStockMinimo() + ". Programe una compra.");
    }
}
