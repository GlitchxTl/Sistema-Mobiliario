/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import Vista.LoginVista;
import Controlador.AuthControlador;
import javax.swing.*;

import java.util.logging.Level;
import java.util.logging.Logger;
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
        
        
        public static void iniciarCargaBcv() {
        SwingWorker<Double, Void> worker = new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                Bcv bcv = new Bcv();
                return bcv.getRate();
            }

            @Override
            protected void done() {
                try {
                    Double rate = get();
                    GestorBcv.getInstance().setTasaActual(rate);

                } catch (Exception e) {
                    GestorBcv.getInstance().setTasaActual(-1.0);
                   
                }
            }
        };
        worker.execute();
    }
} 



