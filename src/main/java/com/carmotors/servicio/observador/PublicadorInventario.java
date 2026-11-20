/*
 * ============================================================
 * Archivo: PublicadorInventario.java
 * Propósito: mantener la lista de observadores y avisarles de alertas de stock.
 * Conceptos clave: PATRÓN OBSERVER (sujeto) + SINGLETON con
 *            private static final INSTANCIA como en S12Demo04 (una sola lista
 *            de observadores para toda la aplicación), List/ArrayList (sesión 08),
 *            for-each (sesión 03), try/catch por observador.
 * Justificación: igual que en S14Demo01, un observador que falla no debe
 *            impedir que los demás reciban el aviso ni romper la operación
 *            de inventario que ya se guardó.
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.servicio.observador;

import com.carmotors.modelo.Repuesto;

import java.util.ArrayList;
import java.util.List;

// Visto en clase: PATRÓN OBSERVER -> S14Demo01 PublicadorPedidos (Sesión 14)
public class PublicadorInventario {
    // Única instancia: todos los InventarioServicio avisan a los mismos observadores.
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: PATRÓN SINGLETON (private static final INSTANCIA) -> S12Demo04 ConfiguracionSistema (Sesión 12)
    private static final PublicadorInventario INSTANCIA = new PublicadorInventario();

    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    private final List<ObservadorInventario> observadores = new ArrayList<>();

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    private PublicadorInventario() {
    }

    /** Devuelve siempre el mismo objeto. */
    public static PublicadorInventario getInstancia() {
        return INSTANCIA;
    }

    /** Agrega un observador; ignora null y duplicados. */
    public void suscribir(ObservadorInventario observador) {
        // Visto en clase: operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        // Visto en clase: toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
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
        // Visto en clase: ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
        for (ObservadorInventario observador : observadores) {
            // Visto en clase: try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
            try {
                observador.stockBajo(repuesto);
                entregas++;
            // Visto en clase: multi-catch y catch (RuntimeException) -> S16Demo02 EjecutorPrueba (Sesión 16)
            } catch (RuntimeException error) {
                // Se aísla el fallo: el siguiente observador igual recibe el aviso.
            }
        }
        return entregas;
    }
}
