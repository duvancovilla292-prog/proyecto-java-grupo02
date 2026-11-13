/*
 * MAIN PROVISIONAL - DÍA 1 (NO HACER COMMIT)
 * Prueba: conexión a MySQL (Singleton), modelo de dominio, excepciones y vista base.
 * Copiar a src/main/java/com/carmotors/Main.java después de integrar el día 1.
 */
package com.carmotors;

import com.carmotors.config.ConexionFactory;
import com.carmotors.excepcion.CarMotorsException;
import com.carmotors.excepcion.DatoInvalidoException;
import com.carmotors.modelo.Cliente;
import com.carmotors.modelo.EstadoServicio;
import com.carmotors.modelo.OrdenServicio;
import com.carmotors.modelo.OrdenServicioBuilder;
import com.carmotors.modelo.TipoMantenimiento;
import com.carmotors.vista.Formato;
import com.carmotors.vista.MenuPrincipal;
import com.carmotors.vista.VistaConsola;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        VistaConsola consola = new VistaConsola(new Scanner(System.in));
        MenuPrincipal menu = new MenuPrincipal(consola);
        menu.mostrarInicio();

        // 1) Config: Singleton + conexión
        try {
            ConexionFactory conexion = ConexionFactory.getInstancia();
            consola.mostrar("Singleton: misma instancia = " + (conexion == ConexionFactory.getInstancia()));
            if (conexion.probarConexion()) {
                consola.mostrarExito("Conexión a MySQL OK.");
            }
        } catch (Exception e) {
            consola.mostrarError("Conexión falló: " + e.getMessage());
            menu.mostrarAyudaConexion();
        }

        // 2) Modelo de dominio
        Cliente cliente = new Cliente(0, "Ana Pérez", "1001", "3001234567", "ANA@MAIL.COM");
        consola.mostrar("Cliente creado: " + cliente.getNombre() + " / " + cliente.getEmail());

        OrdenServicio orden = new OrdenServicioBuilder()
                .vehiculoId(1)
                .tipoMantenimiento(TipoMantenimiento.PREVENTIVO)
                .descripcionProblema("Cambio de aceite")
                .costoManoObra(80000)
                .build();
        consola.mostrar("Orden (Builder): " + orden.getTipoMantenimiento() + " - " + orden.getEstado()
                + " - " + Formato.pesos(orden.getCostoManoObra()));
        for (EstadoServicio e : EstadoServicio.values()) {
            consola.mostrar("  Estado " + e.getId() + ": " + e.getDescripcion());
        }

        // 3) Excepciones: un dato inválido debe lanzar DatoInvalidoException
        try {
            new Cliente(0, "", "1002", "", "sin-arroba");
            consola.mostrarError("Debió lanzar DatoInvalidoException.");
        } catch (DatoInvalidoException e) {
            consola.mostrarExito("Validación OK -> " + e.getMessage()
                    + " (es CarMotorsException: " + (e instanceof CarMotorsException) + ")");
        }

        // 4) Vista base
        consola.mostrarAlerta("Esto es una alerta de prueba.");
        menu.mostrarDespedida();
    }
}
