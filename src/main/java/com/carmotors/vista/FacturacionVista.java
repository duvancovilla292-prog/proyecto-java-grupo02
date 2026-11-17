/*
 * ============================================================
 * Archivo: FacturacionVista.java
 * Propósito: menú y presentación del Módulo de Facturación y Reportes:
 *            listado de facturas, TEXTO de la factura detallada y reportes
 *            gerenciales.
 * Conceptos clave: MVC - Vista, implementa VistaMenu, Streams (map + toList,
 *            reduce), TEMPLATE METHOD (reportes en vista/reporte).
 * Justificación: armar las líneas de la factura es PRESENTACIÓN; por eso se
 *            hace aquí. El controlador entrega esas líneas a
 *            FacturacionServicio, que es quien las guarda en el archivo .txt.
 *            Así la vista no escribe en disco y el servicio no da formato.
 * ============================================================
 */
package com.carmotors.vista;

import com.carmotors.modelo.ConsumoRepuesto;
import com.carmotors.modelo.Factura;
import com.carmotors.modelo.FacturaDetallada;
import com.carmotors.modelo.ProductividadTecnico;
import com.carmotors.modelo.ResumenDescripcion;
import com.carmotors.modelo.ResumenTipoServicio;
import com.carmotors.modelo.Tecnico;
import com.carmotors.vista.reporte.ReporteConsumoRepuestos;
import com.carmotors.vista.reporte.ReporteProductividadTecnicos;
import com.carmotors.vista.reporte.ReporteServiciosSolicitados;

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
            "Exportar factura a archivo .txt",
            "Reporte: servicios más solicitados",
            "Reporte: productividad por técnico",
            "Reporte: consumo de repuestos");

    private final VistaConsola consola;

    public FacturacionVista(VistaConsola consola) {
        this.consola = consola;
    }

    @Override
    public String titulo() {
        return "Facturación y reportes";
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

    public void mostrarServiciosSolicitados(List<ResumenTipoServicio> porTipo, List<ResumenDescripcion> porDescripcion) {
        consola.mostrarLineas(new ReporteServiciosSolicitados(porTipo, porDescripcion).generar());
    }

    public void mostrarProductividad(List<ProductividadTecnico> filas) {
        consola.mostrarLineas(new ReporteProductividadTecnicos(filas).generar());
    }

    public void mostrarConsumo(List<ConsumoRepuesto> filas) {
        consola.mostrarLineas(new ReporteConsumoRepuestos(filas).generar());
    }

    private String fila(String etiqueta, int valor) {
        return "  " + Formato.ajustar(etiqueta, 47) + Formato.derecha(Formato.pesos(valor), 13);
    }
}
