package Vista;

import Controlador.ArticuloControlador;
import Controlador.MovimientoControlador;
import Modelo.Articulo;
import Modelo.Movimiento;
import Modelo.Ubicacion;
import Modelo.UbicacionDAO;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelSalida extends javax.swing.JFrame {

    private final ArticuloControlador articuloControl = new ArticuloControlador();
    private final MovimientoControlador movimientoControl = new MovimientoControlador();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();

    public PanelSalida() {
        initComponents();
        cargarCombos();
        cargarTablaSalidas(); 
        bRegistrarSalida.addActionListener(e -> onRegistrarSalida());
    }

    /** Cargar combos de artículos y ubicaciones */
    private void cargarCombos() {
        try {
            // Artículos
            DefaultComboBoxModel<Articulo> modeloArticulos = new DefaultComboBoxModel<>();
            List<Articulo> articulos = articuloControl.obtenerTodosArticulosc();
            for (Articulo a : articulos) modeloArticulos.addElement(a);
            jCBArticuloSalida.setModel(modeloArticulos);

            // Ubicaciones
            DefaultComboBoxModel<Ubicacion> modeloUbicaciones = new DefaultComboBoxModel<>();
            List<Ubicacion> ubicaciones = ubicacionDAO.listar();
            for (Ubicacion u : ubicaciones) modeloUbicaciones.addElement(u);
            jCBUbicacionOrigen.setModel(modeloUbicaciones);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Registrar salida */
    private void onRegistrarSalida() {
        try {
            Articulo art = (Articulo) jCBArticuloSalida.getSelectedItem();
            Ubicacion ubicOrigen = (Ubicacion) jCBUbicacionOrigen.getSelectedItem();
            int cantidad = parseIntSafe(jFTCantidadSalida.getText());
            String motivo = jFTMotivo.getText().trim();
            // String entregado = jFTEntregadoA.getText().trim(); <--- ELIMINADO

            if (art == null || ubicOrigen == null) {
                JOptionPane.showMessageDialog(this, "Selecciona artículo y ubicación origen.",
                                "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida (>0).",
                                "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes indicar un motivo de salida.",
                                "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Registrar salida con 4 argumentos: idArticulo, cantidad, idUbicacionOrigen, motivo
            boolean ok = movimientoControl.registrarSalida(
                    art.getIdArticulo(),
                    cantidad,
                    ubicOrigen.getId_ubicacion(),
                    motivo
            );

            if (ok) {
                JOptionPane.showMessageDialog(this, "Salida registrada correctamente.",
                                "OK", JOptionPane.INFORMATION_MESSAGE);
                jFTCantidadSalida.setText("");
                jFTMotivo.setText("");
                // jFTEntregadoA.setText(""); <--- ELIMINADO
                cargarTablaSalidas(); 
            } else {
                JOptionPane.showMessageDialog(this,
                                "No se pudo registrar la salida (verifica stock o error interno).",
                                "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error SQL: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    
    /** Carga los movimientos de tipo SALIDA en la tabla jTable1 */
    private void cargarTablaSalidas() {
        try {
            DefaultTableModel model = (DefaultTableModel) jTablaSalida.getModel();
            model.setRowCount(0); // Limpia filas anteriores

            List<Movimiento> listaSalidas = movimientoControl.obtenerSalidas();

            for (Movimiento m : listaSalidas) {
                model.addRow(new Object[]{
                    m.getNombreArticulo(),
                    m.getCantidad(),
                    m.getNombreUbicacionOrigen() != null ? m.getNombreUbicacionOrigen() : "-",
                    m.getMotivo() != null ? m.getMotivo() : "-",
                    // m.getEntregado() ya no se muestra si no existe el campo en el formulario, 
                    // pero si la columna está en la tabla, se mantiene la referencia:
                    m.getEntregado() != null ? m.getEntregado() : "-", 
                    m.getFechaHora()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar la tabla de salidas: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private int parseIntSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0 : Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        bVolver = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jFTMotivo = new javax.swing.JFormattedTextField();
        bRegistrarSalida = new javax.swing.JButton();
        jCBUbicacionOrigen = new javax.swing.JComboBox<>();
        jFTCantidadSalida = new javax.swing.JFormattedTextField();
        jCBArticuloSalida = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablaSalida = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableEntrada = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(13, 51, 131));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(13, 51, 131));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 116, 1367, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Salida");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 0, 792, 110));

        jButton1.setBackground(new java.awt.Color(13, 51, 131));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Registrar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, 170, 40));

        jButton4.setBackground(new java.awt.Color(13, 51, 131));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Registrar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, 170, 40));

        jButton5.setBackground(new java.awt.Color(13, 51, 131));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Registrar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 330, 170, 40));

        bVolver.setBackground(new java.awt.Color(13, 51, 131));
        bVolver.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bVolver.setForeground(new java.awt.Color(255, 255, 255));
        bVolver.setText("Volver");
        bVolver.setBorder(null);
        bVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bVolverActionPerformed(evt);
            }
        });
        jPanel2.add(bVolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, 170, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 110));

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Salida");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 350, 57));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Artículo:");
        jPanel4.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 150, 51));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Ubicacion Origen:");
        jPanel4.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 150, 51));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Motivo:");
        jPanel4.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 140, 51));

        jFTMotivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTMotivoActionPerformed(evt);
            }
        });
        jPanel4.add(jFTMotivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 310, 180, 50));

        bRegistrarSalida.setBackground(new java.awt.Color(13, 51, 131));
        bRegistrarSalida.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bRegistrarSalida.setForeground(new java.awt.Color(255, 255, 255));
        bRegistrarSalida.setText("Confirmar");
        bRegistrarSalida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRegistrarSalidaActionPerformed(evt);
            }
        });
        jPanel4.add(bRegistrarSalida, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 380, 170, 40));

        jPanel4.add(jCBUbicacionOrigen, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 230, 180, 48));

        jFTCantidadSalida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTCantidadSalidaActionPerformed(evt);
            }
        });
        jPanel4.add(jFTCantidadSalida, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, 180, 50));

        jPanel4.add(jCBArticuloSalida, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 70, 180, 48));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Cantidad:");
        jPanel4.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 150, 51));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 350, 430));

        jTablaSalida.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Articulo", "Cantidad", "Motivo", "Fecha"
            }
        ));
        jScrollPane1.setViewportView(jTablaSalida);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 110, -1, 220));

        jTableEntrada.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Articulo", "Cantidad", "Ubicación", "Costo", "Vencimiento", "Entregado", "Fecha"
            }
        ));
        jScrollPane2.setViewportView(jTableEntrada);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 330, -1, 210));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jFTMotivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTMotivoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTMotivoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void bVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bVolverActionPerformed
        // TODO add your handling code here:
        this.dispose();
        PrincipalVista principalvista = new PrincipalVista();
        principalvista.setVisible(true);
    }//GEN-LAST:event_bVolverActionPerformed

    private void bRegistrarSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRegistrarSalidaActionPerformed

    }//GEN-LAST:event_bRegistrarSalidaActionPerformed

    private void jFTCantidadSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTCantidadSalidaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTCantidadSalidaActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bRegistrarSalida;
    private javax.swing.JButton bVolver;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<Modelo.Articulo> jCBArticuloSalida;
    private javax.swing.JComboBox<Modelo.Ubicacion> jCBUbicacionOrigen;
    private javax.swing.JFormattedTextField jFTCantidadSalida;
    private javax.swing.JFormattedTextField jFTMotivo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTablaSalida;
    private javax.swing.JTable jTableEntrada;
    // End of variables declaration//GEN-END:variables
}
