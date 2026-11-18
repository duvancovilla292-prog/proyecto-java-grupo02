/*
 * ============================================================
 * Archivo: ReporteAlertasInventario.java
 * Propósito: presentar las alertas de inventario: repuestos bajo stock mínimo y lotes próximos a caducar.
 * Conceptos clave: PATRÓN TEMPLATE METHOD (subclase de ReporteGerencial,
 *            S14Demo05), Stream.map + toList para convertir datos en líneas.
 * Justificación: solo da FORMATO; los cálculos llegan hechos desde la capa
 *            de servicio como objetos del modelo (SRP / separación MVC).
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - herencia extends / super(...) -> S07Demo00 Estudiante extends Usuario (Sesión 07)
 *   - @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
 *   - modificador protected -> S07Demo00 Usuario.describirIdentidad (Sesión 07)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - List.of / List.copyOf -> S19Demo03ReglasOrdenMain List.of y S20Demo02 Orden List.copyOf (Sesiones 19 y 20)
 *   - PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 *   - referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
 *   - Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
 * ============================================================
 */
package com.carmotors.vista.reporte;

import com.carmotors.modelo.Lote;
import com.carmotors.modelo.Repuesto;
import com.carmotors.vista.Formato;

import java.util.ArrayList;
import java.util.List;

public class ReporteAlertasInventario extends ReporteGerencial {
    private final List<Repuesto> bajoMinimo;
    private final List<Lote> porVencer;
    private final List<Repuesto> catalogo;  // para traducir repuestoId -> nombre
    private final int diasAnticipacion;

    public ReporteAlertasInventario(List<Repuesto> bajoMinimo, List<Lote> porVencer, List<Repuesto> catalogo,
                                    int diasAnticipacion) {
        this.bajoMinimo = List.copyOf(bajoMinimo);
        this.porVencer = List.copyOf(porVencer);
        this.catalogo = List.copyOf(catalogo);
        this.diasAnticipacion = diasAnticipacion;
    }

    @Override
    protected String titulo() {
        return "Alertas de inventario";
    }

    @Override
    protected List<String> cuerpo() {
        List<String> lineas = new ArrayList<>();
        if (bajoMinimo.isEmpty() && porVencer.isEmpty()) {
            return lineas;
        }
        lineas.add("Repuestos por debajo del stock mínimo (" + bajoMinimo.size() + "):");
        lineas.addAll(bajoMinimo.stream()
                .map(r -> "  [#" + r.getId() + "] " + Formato.ajustar(r.getNombre(), 30)
                        + " disponible " + r.getDisponible() + " (stock " + r.getCantidadStock()
                        + ", reservado " + r.getCantidadReservada() + ") | mínimo " + r.getStockMinimo())
                .toList());
        lineas.add("");
        lineas.add("Lotes que caducan en los próximos " + diasAnticipacion + " días (" + porVencer.size() + "):");
        lineas.addAll(porVencer.stream()
                .map(l -> "  " + l.getCodigo() + " | " + Formato.ajustar(nombreRepuesto(l.getRepuestoId()), 28)
                        + " | caduca " + Formato.fecha(l.getFechaCaducidad()) + " | " + l.getCantidad() + " und.")
                .toList());
        return lineas;
    }

    @Override
    protected String mensajeSinDatos() {
        return "Sin alertas: el inventario está en orden.";
    }

    private String nombreRepuesto(int repuestoId) {
        List<String> nombres = catalogo.stream()
                .filter(r -> r.getId() == repuestoId)
                .map(Repuesto::getNombre)
                .toList();
        return nombres.isEmpty() ? "Repuesto #" + repuestoId : nombres.get(0);
    }
}
