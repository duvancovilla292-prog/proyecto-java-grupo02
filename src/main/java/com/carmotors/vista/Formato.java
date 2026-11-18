/*
 * ============================================================
 * Archivo: Formato.java
 * Propósito: dar formato legible a montos, fechas y columnas de texto
 *            para facturas, reportes y vistas.
 * Conceptos clave: clase utilitaria final con constructor privado,
 *            métodos static (S05Demo02 ConversorTemperatura), manejo de
 *            cadenas con length/substring/charAt (sesión 04), ciclos (sesión 03).
 * Justificación: dar formato es responsabilidad de PRESENTACIÓN, por eso
 *            vive en la capa vista: los servicios devuelven números y fechas y
 *            solo las vistas los convierten en texto. Centralizarlo evita
 *            duplicación (DRY) sin depender de librerías externas.
 * Visto en clase (demos del curso):
 *   - clases, constructores y this -> S05Demo01Vehiculo (Sesión 05)
 *   - métodos y miembros static -> S05Demo02ConversorTemperatura (Sesión 05)
 *   - operadores aritméticos y lógicos -> S02DemoOperadores y S02DemoDecisiones (Sesión 02)
 *   - if / else if / else -> S02DemoDecisiones (Sesión 02)
 *   - operador ternario ? : -> S02DemoOpciones (Sesión 02)
 *   - ciclos for / for-each / while -> S03DemoFor y S03DemoWhile (Sesión 03)
 *   - String: strip, isBlank, length, charAt, substring, equals -> S04DemoCadenas y S04DemoComparacion (Sesión 04)
 *   - conversiones: Integer.parseInt, String.valueOf, cast (int)/(double) -> S04DemoConversiones (Sesión 04) y S08Demo00 cast (double) (Sesión 08)
 * ============================================================
 */
package com.carmotors.vista;


public final class Formato {
    private Formato() {
    }

    /** 1234567 -> "$1.234.567" (separador de miles colombiano). */
    public static String pesos(int valor) {
        String digitos = String.valueOf(valor < 0 ? -valor : valor);  // valor absoluto con ternario
        String resultado = "";
        int contador = 0;
        // Recorre de derecha a izquierda insertando un punto cada 3 dígitos.
        for (int i = digitos.length() - 1; i >= 0; i--) {
            resultado = digitos.charAt(i) + resultado;
            contador++;
            if (contador % 3 == 0 && i > 0) {
                resultado = "." + resultado;
            }
        }
        return (valor < 0 ? "-$" : "$") + resultado;
    }

    /** "2026-03-10 08:30:00" -> "2026-03-10 08:30" (quita los segundos con substring, sesión 04). */
    public static String fechaHora(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return "-";
        }
        return fecha.length() > 16 ? fecha.substring(0, 16) : fecha;
    }

    /** Fecha "AAAA-MM-DD" o "-" si es null (por ejemplo, un lote sin caducidad). */
    public static String fecha(String fecha) {
        return fecha == null || fecha.isBlank() ? "-" : fecha;
    }

    /** Completa con espacios a la derecha (o recorta) para alinear columnas. */
    public static String ajustar(String texto, int ancho) {
        String valor = texto == null ? "" : texto;
        if (valor.length() >= ancho) {
            return valor.substring(0, ancho);
        }
        String relleno = valor;
        while (relleno.length() < ancho) {
            relleno += " ";
        }
        return relleno;
    }

    /** Completa con espacios a la IZQUIERDA para alinear montos a la derecha. */
    public static String derecha(String texto, int ancho) {
        String valor = texto == null ? "" : texto;
        while (valor.length() < ancho) {
            valor = " " + valor;
        }
        return valor;
    }

    /** Redondea un decimal a una cifra: 4.333 -> 4.3. */
    public static double unDecimal(double valor) {
        // Cast a int (S04): trunca; sumar 0.5 antes redondea al decimal más cercano.
        return (int) (valor * 10 + 0.5) / 10.0;
    }
}
