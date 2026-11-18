/*
 * ============================================================
 * Archivo: FacturacionVista.java
 * Propósito: menú y presentación del Módulo de Facturación:
 *            listado de facturas y TEXTO de la factura detallada.
 * Conceptos clave: MVC - Vista, implementa VistaMenu, Streams (map + toList,
 *            reduce).
 * Justificación: armar las líneas de la factura es PRESENTACIÓN; por eso se
 *            hace aquí. El controlador entrega esas líneas a
 *            FacturacionServicio, que es quien las guarda en el archivo .txt.
 *            Así la vista no escribe en disco y el servicio no da formato.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - @Override y polimorfismo -> S07Demo01 EnvioExpress y S07Demo02PolimorfismoMain (Sesión 07)
 *   - interfaces e implements -> S07Demo04 Notificable (Sesión 07)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - List.of / List.copyOf -> S19Demo03ReglasOrdenMain List.of y S20Demo02 Orden List.copyOf (Sesiones 19 y 20)
 *   - Map / HashMap / LinkedHashMap / Map.Entry -> S10Demo01MapMain (Sesión 10)
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 *   - referencias a método (objeto::metodo, Clase::metodo) -> S19Demo01 System.out::println y S20Demo02 Item::getNombre (Sesiones 19 y 20)
 *   - Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
 *   - Stream: distinct / sorted / mapToInt+sum / reduce(0, Integer::sum) -> S20Demo02OrdenesMain (Sesión 20)
 * ============================================================
 */
package com.carmotors.vista;

import com.carmotors.modelo.Factura;
import com.carmotors.modelo.FacturaDetallada;
import com.carmotors.modelo.Tecnico;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FacturacionVista implements VistaMenu {
    private static final String LINEA_DOBLE = "==============================================================";
    private static final String LINEA_SIMPLE = "--------------------------------------------------------------";
    private static final List<String> OPCIONES = List.of(
            "Facturar orden completada",
            "Listar facturas",
            "Ver factura de una orden",
            "Exportar factura a archivo .txt");

    private final VistaConsola consola;

    public FacturacionVista(VistaConsola consola) {
        this.consola = consola;
    }

    @Override
    public String titulo() {
        return "Facturación";
    }

    @Override
    public Map<String, String> opciones() {
        return VistaConsola.numerar(OPCIONES);
    }

    @Override
    public String textoSalida() {
        return "Volver al menú principal";
    }

    public void mostrarFacturas(List<Factura> facturas) {
        if (facturas.isEmpty()) {
            consola.mostrar("No hay facturas emitidas.");
            return;
        }
        consola.mostrar(Formato.ajustar("Factura", 9) + Formato.ajustar("Orden", 7) + Formato.ajustar("Emisión", 18)
                + Formato.derecha("Repuestos", 12) + Formato.derecha("Mano obra", 12)
                + Formato.derecha("Impuestos", 12) + Formato.derecha("Total", 12));
        for (Factura f : facturas) {
            consola.mostrar(Formato.ajustar("#" + f.getId(), 9) + Formato.ajustar("#" + f.getServicioId(), 7)
                    + Formato.ajustar(Formato.fechaHora(f.getFechaEmision()), 18)
                    + Formato.derecha(Formato.pesos(f.getTotalRepuestos()), 12)
                    + Formato.derecha(Formato.pesos(f.getTotalManoObra()), 12)
                    + Formato.derecha(Formato.pesos(f.getImpuestos()), 12)
                    + Formato.derecha(Formato.pesos(f.getTotalFinal()), 12));
        }
        int facturado = facturas.stream().map(Factura::getTotalFinal).reduce(0, Integer::sum);
        consola.mostrar("Total facturado: " + Formato.pesos(facturado) + " en " + facturas.size() + " facturas.");
    }

    /**
     * Construye el texto plano de la factura: datos del cliente y del vehículo,
     * técnicos, desglose de repuestos, mano de obra, impuesto y total.
     */
    public List<String> lineasFactura(FacturaDetallada d) {
        Factura f = d.getFactura();
        List<String> tecnicos = d.getTecnicos().stream().map(Tecnico::getNombre).toList();
        List<String> lineas = new ArrayList<>();
        lineas.add(LINEA_DOBLE);
        lineas.add("               TALLER AUTOMOTRIZ CARMOTORS");
        lineas.add("               Factura de venta " + d.numero());
        lineas.add(LINEA_DOBLE);
        lineas.add("Fecha de emisión : " + Formato.fechaHora(f.getFechaEmision()));
        lineas.add("Orden de servicio: #" + d.getOrden().getId() + " ("
                + d.getOrden().getTipoMantenimiento().getDescripcion() + ")");
        lineas.add("Recepción        : " + Formato.fechaHora(d.getOrden().getFechaRecepcion()));
        lineas.add(LINEA_SIMPLE);
        lineas.add("CLIENTE");
        lineas.add("  Nombre    : " + d.getCliente().getNombre());
        lineas.add("  Documento : " + d.getCliente().getDocumento());
        lineas.add("  Teléfono  : " + d.getCliente().getTelefono());
        lineas.add("  Email     : " + d.getCliente().getEmail());
        lineas.add("VEHÍCULO");
        lineas.add("  Placa     : " + d.getVehiculo().getPlaca());
        lineas.add("  Marca     : " + d.getVehiculo().getMarca());
        lineas.add("  Modelo    : " + d.getVehiculo().getModelo());
        lineas.add("TRABAJO REALIZADO");
        lineas.add("  Problema  : " + d.getOrden().getDescripcionProblema());
        lineas.add("  Técnicos  : " + (tecnicos.isEmpty() ? "-" : tecnicos.toString()));
        lineas.add(LINEA_SIMPLE);
        lineas.add("REPUESTOS UTILIZADOS");
        lineas.add("  " + Formato.ajustar("Descripción", 28) + Formato.derecha("Cant.", 6)
                + Formato.derecha("Precio unit.", 13) + Formato.derecha("Subtotal", 13));
        if (d.getRepuestos().isEmpty()) {
            lineas.add("  (Sin repuestos)");
        }
        lineas.addAll(d.getRepuestos().stream()
                .map(r -> "  " + Formato.ajustar(r.getNombreRepuesto(), 28)
                        + Formato.derecha(String.valueOf(r.getCantidad()), 6)
                        + Formato.derecha(Formato.pesos(r.getPrecioUnitario()), 13)
                        + Formato.derecha(Formato.pesos(r.subtotal()), 13))
                .toList());
        lineas.add(LINEA_SIMPLE);
        lineas.add(fila("Total repuestos", f.getTotalRepuestos()));
        lineas.add(fila("Mano de obra", f.getTotalManoObra()));
        lineas.add(fila("Subtotal", f.getTotalRepuestos() + f.getTotalManoObra()));
        lineas.add(fila(d.getDescripcionImpuesto(), f.getImpuestos()));
        lineas.add(fila("TOTAL A PAGAR", f.getTotalFinal()));
        lineas.add(LINEA_DOBLE);
        lineas.add("Gracias por confiar en CarMotors. Garantía de 3 meses en mano de obra.");
        return lineas;
    }

    public void mostrarFactura(List<String> lineas) {
        consola.mostrarLineas(lineas);
    }

    private String fila(String etiqueta, int valor) {
        return "  " + Formato.ajustar(etiqueta, 47) + Formato.derecha(Formato.pesos(valor), 13);
    }
}
