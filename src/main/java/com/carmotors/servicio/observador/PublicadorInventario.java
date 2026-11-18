/*
 * ============================================================
 * Archivo: PublicadorInventario.java
 * Propósito: mantener la lista de observadores y avisarles de alertas de stock.
 * Conceptos clave: PATRÓN OBSERVER (sujeto) + SINGLETON (una sola lista
 *            de observadores para toda la aplicación), List/ArrayList (sesión 08),
 *            for-each (sesión 03), try/catch por observador.
 * Justificación: igual que en S14Demo01, un observador que falla no debe
 *            impedir que los demás reciban el aviso ni romper la operación
 *            de inventario que ya se guardó.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - multi-catch y catch (RuntimeException) -> S16Demo02 EjecutorPrueba (Sesión 16)
 *   - PATRÓN OBSERVER -> S14Demo01 PublicadorPedidos (Sesión 14)
 * ============================================================
 */
package com.carmotors.servicio.observador;

import com.carmotors.modelo.Repuesto;

import java.util.ArrayList;
import java.util.List;

public class PublicadorInventario {
    // Única instancia: todos los InventarioServicio avisan a los mismos observadores.
    private static PublicadorInventario instancia;

    private final List<ObservadorInventario> observadores = new ArrayList<>();

    private PublicadorInventario() {
    }

    public static synchronized PublicadorInventario getInstancia() {
        if (instancia == null) {
            instancia = new PublicadorInventario();
        }
        return instancia;
    }

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
