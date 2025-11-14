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

public class PanelTraslado extends javax.swing.JFrame {

    private final ArticuloControlador articuloControl = new ArticuloControlador();
    private final MovimientoControlador movimientoControl = new MovimientoControlador();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();

    public PanelTraslado() {
        initComponents();
        cargarCombos();
        cargarTablaTraslados();

        bRegistrarTraslado.addActionListener(e -> onRegistrarTraslado());
    }

    /** Cargar combos de artículos y ubicaciones */
    private void cargarCombos() {
        try {
            // Artículos
            DefaultComboBoxModel<Articulo> modeloArticulos = new DefaultComboBoxModel<>();
            List<Articulo> articulos = articuloControl.obtenerTodosArticulosc();
            for (Articulo a : articulos) {
                modeloArticulos.addElement(a);
            }
            jCBArticuloTraslado.setModel(modeloArticulos);

            // Ubicaciones
            DefaultComboBoxModel<Ubicacion> modeloUbic = new DefaultComboBoxModel<>();
            List<Ubicacion> ubicaciones = ubicacionDAO.listar();
            for (Ubicacion u : ubicaciones) {
                modeloUbic.addElement(u);
            }
            jCBUbicacionOrigen.setModel(modeloUbic);
            jCBUbicacionDestino.setModel(modeloUbic);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Acción principal: registrar traslado */
    private void onRegistrarTraslado() {
            try {
        Articulo art = (Articulo) jCBArticuloTraslado.getSelectedItem();
        Ubicacion origen = (Ubicacion) jCBUbicacionOrigen.getSelectedItem();
        Ubicacion destino = (Ubicacion) jCBUbicacionDestino.getSelectedItem();
        int cantidad = parseIntSafe(jFTCantidad.getText());
        String entregadoA = jFTEntregadoA.getText().trim();

        if (art == null || origen == null || destino == null) {
            JOptionPane.showMessageDialog(this, "Selecciona artículo, ubicación origen y destino.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida (>0).",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- Verificar capacidad disponible ---
        Ubicacion uDestino = ubicacionDAO.obtenerPorId(destino.getId_ubicacion());
        double volumenArticulo = art.getAltura() * art.getAnchura() * art.getProfundidad();
        double volumenTotal = volumenArticulo * cantidad;

        if (uDestino.getCapacidadRestante() < volumenTotal) {
            JOptionPane.showMessageDialog(this,
                    "No hay suficiente espacio en la ubicación destino.\nCapacidad restante: "
                            + uDestino.getCapacidadRestante() + " m³",
                    "Capacidad insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- Crear movimiento manualmente para incluir entregadoA ---
        boolean ok = movimientoControl.registrarTraslado(
        art.getIdArticulo(),
        cantidad,
        origen.getId_ubicacion(),
        destino.getId_ubicacion(),
        jFTEntregadoA.getText().trim()
        );


        if (ok) {
            JOptionPane.showMessageDialog(this, "Traslado registrado correctamente.",
                    "OK", JOptionPane.INFORMATION_MESSAGE);
            jFTCantidad.setText("");
            jFTEntregadoA.setText("");
            cargarTablaTraslados();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el traslado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error SQL: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

    /** Cargar tabla de traslados recientes */
    private void cargarTablaTraslados() {
        try {
            List<Movimiento> traslados = movimientoControl.obtenerTraslados(); // necesitas agregar este método
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            for (Movimiento m : traslados) {
            model.addRow(new Object[]{
            m.getNombreArticulo(),
            m.getCantidad(),
            m.getNombreUbicacionOrigen(),
            m.getNombreUbicacionDestino(),
            m.getEntregado(), // 👈 aquí
            m.getFechaHora()
        });

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int parseIntSafe(String s) {
        try {
            return (s == null || s.trim().isEmpty()) ? 0 : Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
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
        jArticulo = new javax.swing.JLabel();
        jCBUbicacionDestino = new javax.swing.JComboBox<>();
        jEntregadoA = new javax.swing.JLabel();
        jUbicacionDestino = new javax.swing.JLabel();
        jFTEntregadoA = new javax.swing.JFormattedTextField();
        bRegistrarTraslado = new javax.swing.JButton();
        jCBArticuloTraslado = new javax.swing.JComboBox<>();
        jUbicacionOrigen1 = new javax.swing.JLabel();
        jCBUbicacionOrigen = new javax.swing.JComboBox<>();
        jCantidad1 = new javax.swing.JLabel();
        jFTCantidad = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

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
        jLabel1.setText("Traslado");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 0, 792, 80));

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
        jPanel2.add(bVolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, 170, 70));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 80));

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Traslado");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 350, 57));

        jArticulo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jArticulo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jArticulo.setText("Artículo:");
        jPanel4.add(jArticulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 160, 51));

        jCBUbicacionDestino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBUbicacionDestinoActionPerformed(evt);
            }
        });
        jPanel4.add(jCBUbicacionDestino, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 270, 170, 48));

        jEntregadoA.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jEntregadoA.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jEntregadoA.setText("Entragado a:");
        jPanel4.add(jEntregadoA, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 160, 51));

        jUbicacionDestino.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jUbicacionDestino.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jUbicacionDestino.setText("Ubicación Destino:");
        jPanel4.add(jUbicacionDestino, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 160, 51));

        jFTEntregadoA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTEntregadoAActionPerformed(evt);
            }
        });
        jPanel4.add(jFTEntregadoA, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 340, 170, 50));

        bRegistrarTraslado.setBackground(new java.awt.Color(13, 51, 131));
        bRegistrarTraslado.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bRegistrarTraslado.setForeground(new java.awt.Color(255, 255, 255));
        bRegistrarTraslado.setText("Registrar");
        bRegistrarTraslado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRegistrarTrasladoActionPerformed(evt);
            }
        });
        jPanel4.add(bRegistrarTraslado, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 410, 170, 40));

        jPanel4.add(jCBArticuloTraslado, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, 170, 48));

        jUbicacionOrigen1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jUbicacionOrigen1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jUbicacionOrigen1.setText("Ubicación Origen:");
        jPanel4.add(jUbicacionOrigen1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 160, 51));

        jCBUbicacionOrigen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBUbicacionOrigenActionPerformed(evt);
            }
        });
        jPanel4.add(jCBUbicacionOrigen, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 190, 170, 48));

        jCantidad1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jCantidad1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCantidad1.setText("Cantidad:");
        jPanel4.add(jCantidad1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 160, 51));

        jFTCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTCantidadActionPerformed(evt);
            }
        });
        jPanel4.add(jFTCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 110, 170, 50));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 350, 460));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Articulo", "Cantidad", "Origen", "Destino", "Entregado a", "Fecha"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 80, -1, 460));

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

    private void jFTEntregadoAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTEntregadoAActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTEntregadoAActionPerformed

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

    private void jCBUbicacionDestinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBUbicacionDestinoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBUbicacionDestinoActionPerformed

    private void bRegistrarTrasladoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRegistrarTrasladoActionPerformed
        
    }//GEN-LAST:event_bRegistrarTrasladoActionPerformed

    private void jCBUbicacionOrigenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBUbicacionOrigenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBUbicacionOrigenActionPerformed

    private void jFTCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTCantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTCantidadActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bRegistrarTraslado;
    private javax.swing.JButton bVolver;
    private javax.swing.JLabel jArticulo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<Modelo.Articulo> jCBArticuloTraslado;
    private javax.swing.JComboBox<Modelo.Ubicacion> jCBUbicacionDestino;
    private javax.swing.JComboBox<Modelo.Ubicacion> jCBUbicacionOrigen;
    private javax.swing.JLabel jCantidad1;
    private javax.swing.JLabel jEntregadoA;
    private javax.swing.JFormattedTextField jFTCantidad;
    private javax.swing.JFormattedTextField jFTEntregadoA;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jUbicacionDestino;
    private javax.swing.JLabel jUbicacionOrigen1;
    // End of variables declaration//GEN-END:variables
}
