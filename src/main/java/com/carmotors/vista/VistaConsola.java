/*
 * ============================================================
 * Archivo: VistaConsola.java
 * Propósito: única puerta de entrada/salida con el usuario por consola:
 *            mostrar mensajes, menús y listas, y leer datos tecleados.
 * Conceptos clave: MVC - Vista (S18Demo01 VistaConsola), Scanner (sesión 03),
 *            hasNextLine para fin de entrada, Integer.parseInt
 *            con NumberFormatException (sesiones 04 y 10), Map para menús.
 * Justificación: la vista convierte texto en tipos básicos y muestra
 *            resultados, pero NO decide reglas de negocio. Un error de formato
 *            se traduce a DatoInvalidoException para que el controlador lo
 *            muestre con el mismo mecanismo que los demás errores.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - métodos y miembros static -> S05Demo02ConversorTemperatura (Sesión 05)
 *   - constantes final / static final -> Ticket (final IVA, Sesión 01) y S12Demo04 ConfiguracionSistema (Sesión 12)
 *   - encapsulamiento: private + getters + validación -> S06Demo00Billetera y S06Demo01PerfilUsuario (Sesión 06)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - Scanner y hasNextLine -> S03Scanner (Sesión 03) y S18Demo01 VistaConsola (Sesión 18)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - toUpperCase / toLowerCase / contains -> S10Demo00SetMain normalizar y S18Demo00 correo.contains (Sesiones 10 y 18)
 *   - conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
 *   - arreglos y split -> S08Demo00ArreglosMain (Sesión 08) y S17Demo02LecturaMain split (Sesión 17)
 *   - List / ArrayList / genéricos <T> -> S08Demo01ArrayListMain y S08Demo03 Caja<T> (Sesión 08)
 *   - Map / HashMap / LinkedHashMap / Map.Entry -> S10Demo01MapMain (Sesión 10)
 *   - try / catch / throw / throws -> S10Demo03ExcepcionesMain (Sesión 10)
 *   - multi-catch y catch (RuntimeException) -> S16Demo02 EjecutorPrueba (Sesión 16)
 *   - LocalDate.of -> S12Demo02BuilderMain y S14Demo07 Mediator (Sesiones 12 y 14)
 *   - MVC: vista / controlador -> S18Demo01 VistaConsola y UsuarioController (Sesión 18)
 * ============================================================
 */
package com.carmotors.vista;

import com.carmotors.excepcion.DatoInvalidoException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class VistaConsola {
    private final Scanner entrada;

    public VistaConsola(Scanner entrada) {
        this.entrada = entrada;
    }

    // ------------------------------------------------------------ Salida

    public void mostrar(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarTitulo(String titulo) {
        System.out.println();
        System.out.println("=== " + titulo.toUpperCase() + " ===");
    }

    public void mostrarExito(String mensaje) {
        System.out.println("[OK] " + mensaje);
    }

    public void mostrarError(String mensaje) {
        System.out.println("[ERROR] " + mensaje);
    }

    /** Usado por el observador de inventario (vista::mostrarAlerta). */
    public void mostrarAlerta(String mensaje) {
        System.out.println("[ALERTA] " + mensaje);
    }

    /** Imprime cada línea; se usa para reportes y facturas (for-each, sesión 03). */
    public void mostrarLineas(List<String> lineas) {
        for (String linea : lineas) {
            System.out.println(linea);
        }
    }

    // ------------------------------------------------------------ Menús

    /** Convierte una lista de textos en opciones numeradas desde 1 (orden conservado). */
    public static Map<String, String> numerar(List<String> textos) {
        Map<String, String> opciones = new LinkedHashMap<>();
        int numero = 1;
        for (String texto : textos) {
            opciones.put(String.valueOf(numero), texto);
            numero++;
        }
        return opciones;
    }

    /**
     * Muestra un menú numerado y devuelve la opción elegida.
     * Si la entrada termina (Ctrl+D) devuelve "0" para salir limpiamente.
     */
    public String pedirOpcion(String titulo, Map<String, String> opciones, String textoSalida) {
        mostrarTitulo(titulo);
        for (Map.Entry<String, String> opcion : opciones.entrySet()) {
            System.out.println(" " + opcion.getKey() + ". " + opcion.getValue());
        }
        System.out.println(" 0. " + textoSalida);
        System.out.print("Opción: ");
        if (!entrada.hasNextLine()) {
            return "0";
        }
        return entrada.nextLine().strip();
    }

    // ------------------------------------------------------------ Entrada

    /** Lee una línea (puede quedar vacía). */
    public String leerTexto(String etiqueta) {
        System.out.print(etiqueta);
        if (!entrada.hasNextLine()) {
            throw new DatoInvalidoException("Entrada finalizada.");
        }
        return entrada.nextLine().strip();
    }

    /** Lee una línea que no puede quedar vacía. */
    public String leerTextoObligatorio(String etiqueta) {
        String texto = leerTexto(etiqueta);
        if (texto.isBlank()) {
            throw new DatoInvalidoException("El dato es obligatorio.");
        }
        return texto;
    }

    public int leerEntero(String etiqueta) {
        String texto = leerTexto(etiqueta);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException error) {
            throw new DatoInvalidoException("'" + texto + "' no es un número entero.");
        }
    }

    /** Si el usuario presiona Enter sin escribir, devuelve el valor por defecto. */
    public int leerEnteroConDefecto(String etiqueta, int defecto) {
        String texto = leerTexto(etiqueta + " [" + defecto + "]: ");
        if (texto.isEmpty()) {
            return defecto;
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException error) {
            throw new DatoInvalidoException("'" + texto + "' no es un número entero.");
        }
    }

    /** Monto en pesos, sin puntos ni decimales (ej. 120000). */
    public int leerMonto(String etiqueta) {
        String texto = leerTexto(etiqueta);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException error) {
            throw new DatoInvalidoException("'" + texto + "' no es un monto válido: escriba solo números, sin puntos.");
        }
    }

    /**
     * Fecha opcional en formato AAAA-MM-DD; vacío devuelve null.
     * Se valida construyendo un LocalDate.of(año, mes, día) (S12Demo02) y se
     * devuelve como texto, que es como viaja a MySQL.
     */
    public String leerFechaOpcional(String etiqueta) {
        String texto = leerTexto(etiqueta);
        if (texto.isEmpty()) {
            return null;
        }
        String[] partes = texto.split("-");
        if (partes.length != 3) {
            throw new DatoInvalidoException("Use el formato AAAA-MM-DD.");
        }
        try {
            return String.valueOf(LocalDate.of(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]),
                    Integer.parseInt(partes[2])));
        } catch (RuntimeException error) { // NumberFormatException o fecha imposible (ej. 2026-02-30)
            throw new DatoInvalidoException("Fecha inválida: " + texto + ".");
        }
    }

    /** Confirmación explícita, como "Escribe SI para eliminar" de S18Demo00. */
    public boolean confirmar(String pregunta) {
        return leerTexto(pregunta + " (escriba SI para confirmar): ").equalsIgnoreCase("SI");
    }
}
