/*
 * ============================================================
 * Archivo: ReporteProductividadTecnicos.java
 * Propósito: presentar "Productividad por técnico".
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
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - List.of / List.copyOf -> S19Demo03ReglasOrdenMain List.of y S20Demo02 Orden List.copyOf (Sesiones 19 y 20)
 *   - PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 *   - Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
 * ============================================================
 */
package com.carmotors.vista.reporte;

import com.carmotors.modelo.ProductividadTecnico;
import com.carmotors.vista.Formato;

import java.util.List;

public class ReporteProductividadTecnicos extends ReporteGerencial {
    private final List<ProductividadTecnico> filas;

    public ReporteProductividadTecnicos(List<ProductividadTecnico> filas) {
        this.filas = List.copyOf(filas);
    }

    @Override
    protected String titulo() {
        return "Productividad por técnico";
    }

    @Override
    protected List<String> cuerpo() {
        return filas.stream()
                .map(p -> Formato.ajustar(p.getTecnico().getNombre() + (p.getTecnico().isActivo() ? "" : " (inactivo)"), 26)
                        + " terminadas: " + Formato.derecha(String.valueOf(p.getTerminadas()), 2)
                        + " | en curso: " + Formato.derecha(String.valueOf(p.getEnCurso()), 2)
                        + " | mano de obra: " + Formato.derecha(Formato.pesos(p.getManoObra()), 11))
                .toList();
    }
}
