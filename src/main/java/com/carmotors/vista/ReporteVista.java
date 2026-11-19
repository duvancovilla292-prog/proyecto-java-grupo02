/*
 * ============================================================
 * Archivo: ReporteVista.java
 * Propósito: menú y presentación del Módulo de Reportes: servicios más
 *            solicitados, productividad por técnico, consumo de repuestos,
 *            alertas de inventario, recordatorios preventivos y desempeño
 *            de proveedores.
 * Conceptos clave: MVC - Vista, implementa VistaMenu, composición con
 *            VistaConsola, Streams (map + toList, reduce) para convertir los
 *            datos en líneas de texto.
 * Justificación: la vista SOLO da formato: los datos llegan ya calculados
 *            desde ReporteServicio como objetos de modelo.reporte, y el flujo
 *            (qué pedir, qué reporte generar) lo decide ReporteController.
 *            Todos los reportes comparten el mismo marco (separador, título,
 *            cuerpo o mensaje sin datos), escrito una sola vez en imprimir().
 * Visto en clase: cada concepto de los demos del curso se señala con un
 *   comentario "// Visto en clase" junto al código donde se usa.
 * ============================================================
 */
package com.carmotors.vista;

import com.carmotors.modelo.reporte.AlertaStock;
import com.carmotors.modelo.reporte.ConsumoRepuesto;
import com.carmotors.modelo.reporte.DesempenoProveedor;
import com.carmotors.modelo.reporte.LotePorVencer;
import com.carmotors.modelo.reporte.ProductividadTecnico;
import com.carmotors.modelo.reporte.RecordatorioMantenimiento;
import com.carmotors.modelo.reporte.ResumenDescripcion;
import com.carmotors.modelo.reporte.ResumenTipoServicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Visto en clase: interfaces e implements -> S07Demo04 Notificable (Sesión 07)
// Visto en clase: MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
public class ReporteVista implements VistaMenu {
    // Visto en clase: constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
    // Visto en clase: encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
    private static final String SEPARADOR = "==============================================================";
    private static final String SIN_DATOS = "No hay datos para este reporte.";
    // Visto en clase: List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
    // Visto en clase: List.of / List.copyOf -> S19Demo03ReglasOrdenMain List.of y S20Demo02 Orden List.copyOf (Sesiones 19 y 20)
    private static final List<String> OPCIONES = List.of(
            "Servicios más solicitados",
            "Productividad por técnico",
            "Consumo de repuestos",
            "Alertas de inventario (stock mínimo y caducidad)",
            "Recordatorios de mantenimiento preventivo",
            "Desempeño de proveedores");

    private final VistaConsola consola;

    // Visto en clase: clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
    public ReporteVista(VistaConsola consola) {
        this.consola = consola;
    }

    // Visto en clase: @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
    @Override
    public String titulo() {
        return "Reportes gerenciales";
    }

    // Visto en clase: Map / HashMap / LinkedHashMap / Map.Entry -> S10Demo01MapMain (Sesión 10)
    @Override
    public Map<String, String> opciones() {
        return VistaConsola.numerar(OPCIONES);
    }

    @Override
    public String textoSalida() {
        return "Volver al menú principal";
    }

    // ------------------------------------------------------------ Servicios más solicitados

    public void mostrarServiciosSolicitados(List<ResumenTipoServicio> porTipo, List<ResumenDescripcion> porDescripcion) {
        List<String> lineas = new ArrayList<>();
        // Visto en clase: if / else if / else -> S02DemoDecisiones (Sesión 02)
        // Visto en clase: referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
        // Visto en clase: Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
        // Visto en clase: Stream: distinct / sorted / mapToInt+sum / reduce(0, Integer::sum) -> S20Demo02OrdenesMain (Sesión 20)
        if (porTipo.stream().map(ResumenTipoServicio::getSolicitudes).reduce(0, Integer::sum) > 0) {
            lineas.add("Por tipo de mantenimiento (ingresos de órdenes con trabajo terminado):");
            // Visto en clase: expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
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
        }
        imprimir("Servicios más solicitados", lineas, SIN_DATOS);
    }

    // ------------------------------------------------------------ Productividad por técnico

    public void mostrarProductividad(List<ProductividadTecnico> filas) {
        // Visto en clase: operador ternario ? : -> S02DemoOpciones (Sesión 02)
        imprimir("Productividad por técnico", filas.stream()
                .map(p -> Formato.ajustar(p.getTecnico().getNombre() + (p.getTecnico().isActivo() ? "" : " (inactivo)"), 26)
                        + " terminadas: " + Formato.derecha(String.valueOf(p.getTerminadas()), 2)
                        + " | en curso: " + Formato.derecha(String.valueOf(p.getEnCurso()), 2)
                        + " | mano de obra: " + Formato.derecha(Formato.pesos(p.getManoObra()), 11))
                .toList(), SIN_DATOS);
    }

    // ------------------------------------------------------------ Consumo de repuestos

    public void mostrarConsumo(List<ConsumoRepuesto> filas) {
        List<String> lineas = new ArrayList<>();
        if (!filas.isEmpty()) {
            lineas.add(Formato.ajustar("Repuesto", 30) + Formato.derecha("Usadas", 7) + Formato.derecha("Reserv.", 8)
                    + Formato.derecha("Ingreso", 13) + Formato.derecha("Margen", 12));
            lineas.addAll(filas.stream()
                    .map(c -> Formato.ajustar(c.getNombreRepuesto(), 30)
                            + Formato.derecha(String.valueOf(c.getUsadas()), 7)
                            + Formato.derecha(String.valueOf(c.getReservadas()), 8)
                            + Formato.derecha(Formato.pesos(c.getIngreso()), 13)
                            + Formato.derecha(Formato.pesos(c.margen()), 12))
                    .toList());
            lineas.add("");
            lineas.add("Ingreso total por repuestos: "
                    + Formato.pesos(filas.stream().map(ConsumoRepuesto::getIngreso).reduce(0, Integer::sum))
                    + " | Margen total: " + Formato.pesos(filas.stream().map(ConsumoRepuesto::margen).reduce(0, Integer::sum)));
        }
        imprimir("Análisis de consumo de repuestos", lineas, SIN_DATOS);
    }

    // ------------------------------------------------------------ Alertas de inventario

    public void mostrarAlertas(List<AlertaStock> bajoMinimo, List<LotePorVencer> porVencer, int dias) {
        List<String> lineas = new ArrayList<>();
        if (!bajoMinimo.isEmpty() || !porVencer.isEmpty()) {
            lineas.add("Repuestos por debajo del stock mínimo (" + bajoMinimo.size() + "):");
            lineas.addAll(bajoMinimo.stream()
                    .map(a -> "  [#" + a.getRepuestoId() + "] " + Formato.ajustar(a.getNombre(), 30)
                            + " disponible " + a.disponible() + " (stock " + a.getStock()
                            + ", reservado " + a.getReservado() + ") | mínimo " + a.getStockMinimo())
                    .toList());
            lineas.add("");
            lineas.add("Lotes que caducan en los próximos " + dias + " días (" + porVencer.size() + "):");
            lineas.addAll(porVencer.stream()
                    .map(l -> "  " + l.getCodigo() + " | " + Formato.ajustar(l.getNombreRepuesto(), 28)
                            + " | caduca " + Formato.fecha(l.getFechaCaducidad()) + " | " + l.getCantidad() + " und.")
                    .toList());
        }
        imprimir("Alertas de inventario", lineas, "Sin alertas: el inventario está en orden.");
    }

    // ------------------------------------------------------------ Recordatorios preventivos

    public void mostrarRecordatorios(List<RecordatorioMantenimiento> recordatorios, int meses) {
        imprimir("Recordatorios de mantenimiento preventivo (cada " + meses + " meses)", recordatorios.stream()
                .map(r -> (r.isVencido() ? "[VENCIDO] " : "[PRÓXIMO] ")
                        + Formato.ajustar(r.getCliente().getNombre(), 20)
                        + " | " + r.getCliente().getTelefono() + " | " + r.getCliente().getEmail()
                        + " | " + r.getVehiculo().getPlaca()
                        + " | último " + Formato.fecha(r.getUltimoPreventivo())
                        + " | sugerido " + Formato.fecha(r.getProximoPreventivo()))
                .toList(), "No hay clientes por notificar en este periodo.");
    }

    // ------------------------------------------------------------ Desempeño de proveedores

    public void mostrarDesempeno(List<DesempenoProveedor> ranking) {
        imprimir("Desempeño de proveedores (escala 1 a 5)", ranking.stream()
                .map(d -> Formato.ajustar(d.getProveedor().getNombre(), 26)
                        + " puntaje: " + Formato.unDecimal(d.puntajeGeneral())
                        + " | calidad: " + Formato.unDecimal(d.getPromedioCalidad())
                        + " | puntualidad: " + Formato.unDecimal(d.getPromedioPuntualidad())
                        + " | evaluaciones: " + d.getEvaluaciones())
                .toList(), SIN_DATOS);
    }

    // Marco común de todos los reportes: separador, título, cuerpo (o mensaje sin datos), separador.
    private void imprimir(String titulo, List<String> cuerpo, String mensajeSinDatos) {
        List<String> lineas = new ArrayList<>();
        lineas.add(SEPARADOR);
        lineas.add(titulo.toUpperCase());
        lineas.add(SEPARADOR);
        if (cuerpo.isEmpty()) {
            lineas.add(mensajeSinDatos);
        } else {
            lineas.addAll(cuerpo);
        }
        lineas.add(SEPARADOR);
        consola.mostrarLineas(lineas);
    }
}
