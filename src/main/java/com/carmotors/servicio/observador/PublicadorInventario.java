
package com.carmotors.servicio.observador;

import com.carmotors.modelo.Repuesto;

import java.util.ArrayList;
import java.util.List;

public class PublicadorInventario {
    private final List<ObservadorInventario> observadores = new ArrayList<>();

    /** Agrega un observador; ignora null y duplicados. */
    public void suscribir(ObservadorInventario observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void retirar(ObservadorInventario observador) {
        observadores.remove(observador);
    }

    /** Notifica a todos; devuelve cuántos recibieron el aviso sin fallar. */
    public int notificarStockBajo(Repuesto repuesto) {
        int entregas = 0;
        for (ObservadorInventario observador : observadores) {
            try {
                observador.stockBajo(repuesto);
                entregas++;
            } catch (RuntimeException error) {
                // Se aísla el fallo: el siguiente observador igual recibe el aviso.
            }
        }
        return entregas;
    }
}
