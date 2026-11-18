/*
 * ============================================================
 * Archivo: ReporteServiciosSolicitados.java
 * Propósito: presentar "Servicios más solicitados" por tipo de mantenimiento y por descripción.
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

import com.carmotors.modelo.ResumenDescripcion;
import com.carmotors.modelo.ResumenTipoServicio;
import com.carmotors.vista.Formato;

import java.util.ArrayList;
import java.util.List;

public class ReporteServiciosSolicitados extends ReporteGerencial {
    private final List<ResumenTipoServicio> porTipo;
    private final List<ResumenDescripcion> porDescripcion;

    public ReporteServiciosSolicitados(List<ResumenTipoServicio> porTipo, List<ResumenDescripcion> porDescripcion) {
        this.porTipo = List.copyOf(porTipo);
        this.porDescripcion = List.copyOf(porDescripcion);
    }

    @Override
    protected String titulo() {
        return "Servicios más solicitados";
    }

    @Override
    protected List<String> cuerpo() {
        List<String> lineas = new ArrayList<>();
        if (porTipo.stream().map(ResumenTipoServicio::getSolicitudes).reduce(0, Integer::sum) == 0) {
            return lineas;
        }
        lineas.add("Por tipo de mantenimiento (ingresos de órdenes con trabajo terminado):");
        lineas.addAll(porTipo.stream()
                .map(r -> "  " + Formato.ajustar(r.getTipo().getDescripcion(), 12)
                        + " solicitudes: " + Formato.derecha(String.valueOf(r.getSolicitudes()), 3)
                        + " | mano de obra: " + Formato.derecha(Formato.pesos(r.getManoObra()), 12)
                        + " | repuestos: " + Formato.derecha(Formato.pesos(r.getVentaRepuestos()), 12)
                        + " | total: " + Formato.derecha(Formato.pesos(r.ingresoTotal()), 12))
                .toList());
        lineas.add("");
        lineas.add("Por descripción del trabajo (más frecuentes primero):");
        lineas.addAll(porDescripcion.stream()
                .map(r -> "  " + Formato.derecha(String.valueOf(r.getCantidad()), 3) + " x " + r.getDescripcion())
                .toList());
        return lineas;
    }
}
