/*
 * ============================================================
 * Archivo: ReporteConsumoRepuestos.java
 * Propósito: presentar el "Análisis de consumo de repuestos" con totales.
 * Conceptos clave: PATRÓN TEMPLATE METHOD (subclase de ReporteGerencial,
 *            S14Demo05), Stream.map + toList para convertir datos en líneas.
 * Justificación: solo da FORMATO; los cálculos llegan hechos desde la capa
 *            de servicio como objetos del modelo (SRP / separación MVC).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
 *   - @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
 *   - modificador protected -> S07Demo00 Usuario.describirIdentidad (Sesión 07)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - List.of / List.copyOf -> S19Demo03ReglasOrdenMain List.of y S20Demo02 Orden List.copyOf (Sesiones 19 y 20)
 *   - PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 *   - referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
 *   - Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
 *   - Stream: distinct / sorted / mapToInt+sum / reduce(0, Integer::sum) -> S20Demo02OrdenesMain (Sesión 20)
 * ============================================================
 */
package com.carmotors.vista.reporte;

import com.carmotors.modelo.ConsumoRepuesto;
import com.carmotors.vista.Formato;

import java.util.ArrayList;
import java.util.List;

public class ReporteConsumoRepuestos extends ReporteGerencial {
    private final List<ConsumoRepuesto> filas;

    public ReporteConsumoRepuestos(List<ConsumoRepuesto> filas) {
        this.filas = List.copyOf(filas);
    }

    @Override
    protected String titulo() {
        return "Análisis de consumo de repuestos";
    }

    @Override
    protected List<String> cuerpo() {
        List<String> lineas = new ArrayList<>();
        if (filas.isEmpty()) {
            return lineas;
        }
        lineas.add(Formato.ajustar("Repuesto", 30) + Formato.derecha("Usadas", 7) + Formato.derecha("Reserv.", 8)
                + Formato.derecha("Ingreso", 13) + Formato.derecha("Margen", 12));
        lineas.addAll(filas.stream()
                .map(c -> Formato.ajustar(c.getRepuesto().getNombre(), 30)
                        + Formato.derecha(String.valueOf(c.getUsadas()), 7)
                        + Formato.derecha(String.valueOf(c.getReservadas()), 8)
                        + Formato.derecha(Formato.pesos(c.getIngreso()), 13)
                        + Formato.derecha(Formato.pesos(c.getMargen()), 12))
                .toList());
        lineas.add("");
        lineas.add("Ingreso total por repuestos: "
                + Formato.pesos(filas.stream().map(ConsumoRepuesto::getIngreso).reduce(0, Integer::sum))
                + " | Margen total: " + Formato.pesos(filas.stream().map(ConsumoRepuesto::getMargen).reduce(0, Integer::sum)));
        return lineas;
    }
}
