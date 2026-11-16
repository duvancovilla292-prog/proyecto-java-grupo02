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
