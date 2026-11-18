/*
 * ============================================================
 * Archivo: ReporteRecordatorios.java
 * Propósito: presentar los clientes a notificar por su próximo mantenimiento preventivo.
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
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - List.of / List.copyOf -> S19Demo03ReglasOrdenMain List.of y S20Demo02 Orden List.copyOf (Sesiones 19 y 20)
 *   - PATRÓN TEMPLATE METHOD (método final + hooks) -> S14Demo05 ProcesoReporte (Sesión 14)
 *   - expresiones lambda -> S19Demo00LambdaMain (Sesión 19)
 *   - Stream: stream, filter, map, toList -> S20Demo00PrimerStreamMain y S20Demo02OrdenesMain (Sesión 20)
 * ============================================================
 */
package com.carmotors.vista.reporte;

import com.carmotors.modelo.RecordatorioMantenimiento;
import com.carmotors.vista.Formato;

import java.util.List;

public class ReporteRecordatorios extends ReporteGerencial {
    private final List<RecordatorioMantenimiento> recordatorios;
    private final int meses;

    public ReporteRecordatorios(List<RecordatorioMantenimiento> recordatorios, int meses) {
        this.recordatorios = List.copyOf(recordatorios);
        this.meses = meses;
    }

    @Override
    protected String titulo() {
        return "Recordatorios de mantenimiento preventivo (cada " + meses + " meses)";
    }

    @Override
    protected List<String> cuerpo() {
        return recordatorios.stream()
                .map(r -> (r.isVencido() ? "[VENCIDO] " : "[PRÓXIMO] ")
                        + Formato.ajustar(r.getCliente().getNombre(), 20)
                        + " | " + r.getCliente().getTelefono() + " | " + r.getCliente().getEmail()
                        + " | " + r.getVehiculo().getPlaca()
                        + " | último " + Formato.fecha(r.getUltimoPreventivo())
                        + " | sugerido " + Formato.fecha(r.getProximoPreventivo()))
                .toList();
    }

    @Override
    protected String mensajeSinDatos() {
        return "No hay clientes por notificar en este periodo.";
    }
}
