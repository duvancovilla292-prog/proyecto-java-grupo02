
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
