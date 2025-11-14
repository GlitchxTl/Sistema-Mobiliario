package Vista;

import Controlador.ArticuloControlador;
import Controlador.MovimientoControlador;
import Modelo.Articulo;
import Modelo.Movimiento;
import Modelo.Ubicacion;
import Modelo.UbicacionDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date; // Necesario para jDateVencimiento
import java.util.List;

public class PanelEntrada extends javax.swing.JFrame {

    private final ArticuloControlador articuloControl = new ArticuloControlador();
    private final MovimientoControlador movimientoControl = new MovimientoControlador();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();

    // NOTA IMPORTANTE: jDateVencimiento NO está definido en el código original.
    // Asumimos que es una instancia de JDateChooser o similar, que tiene un método getDate().
    // Aquí declaramos una simulación para que el código compile, si no está en initComponents.
    // private final com.toedter.calendar.JDateChooser jDateVencimiento = new com.toedter.calendar.JDateChooser();

    public PanelEntrada() {
        initComponents();
        cargarCombos();
        cargarTablaMovimientos();
        configurarEventosCheckboxes();
        bRegistrarEntrada.addActionListener(e -> onRegistrarEntrada());
    }

    /** Configura la lógica para mostrar/ocultar campos según checkboxes */
    private void configurarEventosCheckboxes() {
        jCheckBoxDonado.addActionListener(e -> {
            boolean donado = jCheckBoxDonado.isSelected();
            jLabelCosto.setVisible(!donado);
            jTFCosto.setVisible(!donado);
        });

        jCheckBoxVencimiento.addActionListener(e -> {
            boolean vencible = jCheckBoxVencimiento.isSelected();
            jLabelVencimiento.setVisible(vencible);
            // El componente jDateVencimiento debe estar visible/oculto
            jDateVencimiento.setVisible(vencible); 
            // jTFVencimiento1 ya no existe, usamos jDateVencimiento
        });

        // Inicialmente ocultar vencimiento
        jLabelVencimiento.setVisible(false);
        jDateVencimiento.setVisible(false);
        // jTFVencimiento1.setVisible(false); // Eliminado
    }

    /** Carga los artículos y ubicaciones existentes */
    private void cargarCombos() {
        try {
            // Artículos
            DefaultComboBoxModel<Articulo> mArticulos = new DefaultComboBoxModel<>();
            List<Articulo> articulos = articuloControl.obtenerTodosArticulosc();
            for (Articulo a : articulos) mArticulos.addElement(a);
            jCBArticuloEntrada.setModel(mArticulos);

            // Ubicaciones
            DefaultComboBoxModel<Ubicacion> mUbicaciones = new DefaultComboBoxModel<>();
            List<Ubicacion> ubicaciones = ubicacionDAO.listar();
            for (Ubicacion u : ubicaciones) mUbicaciones.addElement(u);
            jCBUbicacionEntrada.setModel(mUbicaciones);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                            "Error cargando datos: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Registrar entrada verificando stock y capacidad */
    private void onRegistrarEntrada() {
        try {
            Articulo articulo = (Articulo) jCBArticuloEntrada.getSelectedItem();
            Ubicacion ubicacion = (Ubicacion) jCBUbicacionEntrada.getSelectedItem();
            int cantidad = parseIntSafe(jFTCantidadEntrada.getText());

            if (articulo == null || ubicacion == null) {
                JOptionPane.showMessageDialog(this,
                                "Selecciona un artículo y una ubicación.",
                                "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this,
                                "La cantidad debe ser mayor que cero.",
                                "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // --- Verificación de capacidad ---
            Ubicacion ubicacionActualizada = ubicacionDAO.obtenerPorId(ubicacion.getId_ubicacion());
            double capacidadRestante = ubicacionActualizada.getCapacidadRestante();
            double espacioOcupado = articulo.getEspacioUnitario() * cantidad;

            if (espacioOcupado > capacidadRestante) {
                JOptionPane.showMessageDialog(this,
                                "La ubicación no tiene suficiente capacidad.\n" +
                                "Capacidad restante: " + String.format("%.3f", capacidadRestante) + " m³\n" +
                                "Espacio requerido: " + String.format("%.3f", espacioOcupado) + " m³",
                                "Capacidad insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // --- Obtener fecha de vencimiento ---
            Timestamp fechaVencimiento = null;
            if (jCheckBoxVencimiento.isSelected()) {
                Date selectedDate = jDateVencimiento.getDate();
                if (selectedDate == null) {
                     JOptionPane.showMessageDialog(this,
                            "Selecciona una fecha de vencimiento válida.",
                            "Validación de Fecha", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Convertir java.util.Date a java.sql.Timestamp
                fechaVencimiento = new Timestamp(selectedDate.getTime());
            }

            // --- Obtener Costo ---
            Double costo = null;
            if (!jCheckBoxDonado.isSelected()) {
                double costoVal = parseDoubleSafe(jTFCosto.getText());
                costo = costoVal > 0 ? costoVal : null;
            }

            // --- Registrar entrada con los valores obtenidos ---
            boolean ok = movimientoControl.registrarEntrada(
                articulo.getIdArticulo(),
                cantidad,
                ubicacion.getId_ubicacion(),
                jFTEntregadoA.getText().trim(),
                jCheckBoxDonado.isSelected(),
                costo, // Usamos la variable local ya validada
                fechaVencimiento // Usamos la variable local ya validada
            );
            
            // --- Bloque de Movimiento ya no es necesario, registrarEntrada lo hace
            /*
            Movimiento mov = new Movimiento();
            mov.setIdArticulo(articulo.getIdArticulo());
            mov.setTipo("ENTRADA");
            mov.setCantidad(cantidad);
            // ... otros setters
            mov.setCosto(costo);
            mov.setFechaVencimiento(fechaVencimiento);
            // ...
            */

            if (ok) {
                JOptionPane.showMessageDialog(this,
                                "Entrada registrada correctamente.",
                                "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Limpiar campos después de registrar
                jFTCantidadEntrada.setText("");
                jFTEntregadoA.setText("");
                jTFCosto.setText("");
                jDateVencimiento.setDate(null); // Limpiar el JDateChooser
                
                cargarTablaMovimientos();
            } else {
                JOptionPane.showMessageDialog(this,
                                "Error al registrar la entrada.",
                                "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException sqe) {
            JOptionPane.showMessageDialog(this,
                            "Error SQL: " + sqe.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                            "Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Carga los últimos movimientos de tipo ENTRADA en la tabla */
    private void cargarTablaMovimientos() {
        try {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            var lista = movimientoControl.obtenerEntradas();

            for (Movimiento m : lista) {
                model.addRow(new Object[]{
                    m.getNombreArticulo(),
                    m.getCantidad(),
                    m.getNombreUbicacionDestino() != null ? m.getNombreUbicacionDestino() : "-",
                    m.getCosto() != null ? m.getCosto() : "-",
                    (m.getFechaVencimiento() != null ? m.getFechaVencimiento() : "-"),
                    (m.getEntregado() != null ? m.getEntregado() : "-"),
                    m.getFechaHora()
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int parseIntSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0 : Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }
    
    private double parseDoubleSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0 : Double.parseDouble(s.trim()); }
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
        jCBUbicacionEntrada = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jFTCantidadEntrada = new javax.swing.JFormattedTextField();
        bRegistrarEntrada = new javax.swing.JButton();
        jCBArticuloEntrada = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jFTEntregadoA = new javax.swing.JFormattedTextField();
        jCheckBoxVencimiento = new javax.swing.JCheckBox();
        jCheckBoxDonado = new javax.swing.JCheckBox();
        jLabelCosto = new javax.swing.JLabel();
        jLabelVencimiento = new javax.swing.JLabel();
        jTFCosto = new javax.swing.JTextField();
        jDateVencimiento = new com.toedter.calendar.JDateChooser();
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
        jLabel1.setText("Entrada");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 0, 792, 60));

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
        jPanel2.add(bVolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, 170, 60));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 60));

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Entrada");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, -2, 350, 40));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Artículo:");
        jPanel4.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 170, 40));

        jCBUbicacionEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBUbicacionEntradaActionPerformed(evt);
            }
        });
        jPanel4.add(jCBUbicacionEntrada, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 150, 160, 40));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Cantidad:");
        jPanel4.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 170, 30));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Ubicación:");
        jPanel4.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 170, 40));

        jFTCantidadEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTCantidadEntradaActionPerformed(evt);
            }
        });
        jPanel4.add(jFTCantidadEntrada, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 160, 30));

        bRegistrarEntrada.setBackground(new java.awt.Color(13, 51, 131));
        bRegistrarEntrada.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bRegistrarEntrada.setForeground(new java.awt.Color(255, 255, 255));
        bRegistrarEntrada.setText("Registrar");
        bRegistrarEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRegistrarEntradaActionPerformed(evt);
            }
        });
        jPanel4.add(bRegistrarEntrada, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 440, 160, 30));

        jCBArticuloEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBArticuloEntradaActionPerformed(evt);
            }
        });
        jPanel4.add(jCBArticuloEntrada, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 160, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Entregado a:");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 170, 30));

        jFTEntregadoA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTEntregadoAActionPerformed(evt);
            }
        });
        jPanel4.add(jFTEntregadoA, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 220, 160, 30));

        jCheckBoxVencimiento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jCheckBoxVencimiento.setText("¿Se puede vencer?");
        jPanel4.add(jCheckBoxVencimiento, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 380, 170, 20));

        jCheckBoxDonado.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jCheckBoxDonado.setText("Marcar si es donado");
        jCheckBoxDonado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxDonadoActionPerformed(evt);
            }
        });
        jPanel4.add(jCheckBoxDonado, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, 150, 20));

        jLabelCosto.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabelCosto.setText("Costo:");
        jPanel4.add(jLabelCosto, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 170, 30));

        jLabelVencimiento.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabelVencimiento.setText("Fecha de Vencimiento:");
        jPanel4.add(jLabelVencimiento, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 400, 170, 30));
        jPanel4.add(jTFCosto, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 310, 160, 30));
        jPanel4.add(jDateVencimiento, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 400, 150, 30));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 350, 480));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, -1, 480));

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

    private void jFTCantidadEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTCantidadEntradaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTCantidadEntradaActionPerformed

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

    private void jCBUbicacionEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBUbicacionEntradaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBUbicacionEntradaActionPerformed

    private void bRegistrarEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRegistrarEntradaActionPerformed
        
    }//GEN-LAST:event_bRegistrarEntradaActionPerformed

    private void jCBArticuloEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBArticuloEntradaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBArticuloEntradaActionPerformed

    private void jFTEntregadoAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTEntregadoAActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTEntregadoAActionPerformed

    private void jCheckBoxDonadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxDonadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxDonadoActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bRegistrarEntrada;
    private javax.swing.JButton bVolver;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<Modelo.Articulo> jCBArticuloEntrada;
    private javax.swing.JComboBox<Modelo.Ubicacion> jCBUbicacionEntrada;
    private javax.swing.JCheckBox jCheckBoxDonado;
    private javax.swing.JCheckBox jCheckBoxVencimiento;
    private com.toedter.calendar.JDateChooser jDateVencimiento;
    private javax.swing.JFormattedTextField jFTCantidadEntrada;
    private javax.swing.JFormattedTextField jFTEntregadoA;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelCosto;
    private javax.swing.JLabel jLabelVencimiento;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTFCosto;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
