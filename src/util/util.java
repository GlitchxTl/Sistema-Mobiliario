/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author Dell
 */
public class util {
    public static String capitalizar (String texto) {
    if (texto == null || texto.isBlank()) return texto;
    texto = texto.trim();
    return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
        }
}
