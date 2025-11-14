/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import Vista.LoginVista;
import Vista.PrincipalVista;
import Controlador.AuthControlador;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
/**
 *
 * @author Dell
*/
public class Main {
  
    
        public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        LoginVista login = new LoginVista();
        AuthControlador auth = new AuthControlador(login); // inyecta y conecta
        login.setVisible(true);
    });
        }
} 



