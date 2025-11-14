package Vista;

import Modelo.Articulo;
import Controlador.ArticuloControlador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PanelArticulos extends javax.swing.JFrame {

    private final ArticuloControlador controlador;
    private DefaultTableModel modeloTabla;

public PanelArticulos() {
        initComponents();
        controlador = new ArticuloControlador();

        // 👇 Nueva cabecera: reemplazamos Altura, Anchura, Profundidad por Espacio Unitario
        String[] headers = new String[]{
                "ID", "Nombre", "Código Bien Nacional", "Categoría", "Espacio Unitario (m³)"
        };

        modeloTabla = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(modeloTabla);

        // Ocultamos la columna ID visualmente
        if (jTable1.getColumnCount() > 0) {
            try {
                jTable1.getColumnModel().getColumn(0).setMinWidth(0);
                jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
                jTable1.getColumnModel().getColumn(0).setWidth(0);
            } catch (Exception ignored) {}
        }

        if (bCrear.getActionListeners().length == 0)
            bCrear.addActionListener(this::bCrearActionPerformed);
        if (bModificar.getActionListeners().length == 0)
            bModificar.addActionListener(this::bModificarActionPerformed);
        if (bEliminar.getActionListeners().length == 0)
            bEliminar.addActionListener(this::bEliminarActionPerformed);
        if (bSalir.getActionListeners().length == 0)
            bSalir.addActionListener(this::bSalirActionPerformed);

        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = jTable1.getSelectedRow();
                if (fila >= 0) cargarFormularioDesdeTabla(fila);
            }
        });

        cargarTabla();
    }

    // 🔹 Ahora solo muestra Espacio Unitario en la tabla
    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Articulo> lista = controlador.obtenerTodosArticulosc();
            if (lista == null) return;

            for (Articulo art : lista) {
                double espacio = art.getAltura() * art.getAnchura() * art.getProfundidad();
                modeloTabla.addRow(new Object[]{
                        art.getIdArticulo(),
                        art.getNombre(),
                        art.getCodigoBienNacional(),
                        art.getCategoria(),
                        espacio
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar artículos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        bSalir = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        bModificar = new javax.swing.JButton();
        bCrear = new javax.swing.JButton();
        bEliminar = new javax.swing.JButton();
        bConsultar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jFTextNombre = new javax.swing.JFormattedTextField();
        jFTextCodigoBienNacional = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        jFAnchura = new javax.swing.JFormattedTextField();
        jCBoxCategoria = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jFTextProfundidad = new javax.swing.JFormattedTextField();
        jLabel25 = new javax.swing.JLabel();
        jFTextAltura = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(13, 51, 131));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Gestión de Artículos");

        bSalir.setBackground(new java.awt.Color(13, 51, 131));
        bSalir.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        bSalir.setForeground(new java.awt.Color(255, 255, 255));
        bSalir.setText("Volver");
        bSalir.setBorder(null);
        bSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bSalir, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 60));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        bModificar.setBackground(new java.awt.Color(13, 51, 131));
        bModificar.setForeground(new java.awt.Color(255, 255, 255));
        bModificar.setText("Modificar");
        bModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bModificarActionPerformed(evt);
            }
        });

        bCrear.setBackground(new java.awt.Color(13, 51, 131));
        bCrear.setForeground(new java.awt.Color(255, 255, 255));
        bCrear.setText("Registrar");
        bCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCrearActionPerformed(evt);
            }
        });

        bEliminar.setBackground(new java.awt.Color(13, 51, 131));
        bEliminar.setForeground(new java.awt.Color(255, 255, 255));
        bEliminar.setText("Deshabilitar");
        bEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bEliminarActionPerformed(evt);
            }
        });

        bConsultar.setBackground(new java.awt.Color(13, 51, 131));
        bConsultar.setForeground(new java.awt.Color(255, 255, 255));
        bConsultar.setText("Consultar");
        bConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConsultarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                .addComponent(bConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(bModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(83, 83, 83)
                .addComponent(bEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 420, 820, 80));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(13, 51, 131));

        jLabel10.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Gestión de Artículos");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 60));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 820, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 440, 820, 60));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBackground(new java.awt.Color(13, 51, 131));

        jLabel19.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Gestión de Artículos");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 60));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel7.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 440, 820, 60));

        jFTextNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTextNombreActionPerformed(evt);
            }
        });
        jPanel7.add(jFTextNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 70, 130, 30));

        jFTextCodigoBienNacional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTextCodigoBienNacionalActionPerformed(evt);
            }
        });
        jPanel7.add(jFTextCodigoBienNacional, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 130, 130, 30));

        jLabel13.setText("Categoría");
        jPanel7.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 160, 30));

        jLabel3.setText("Código De Bien Nacional:");
        jPanel7.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 150, 30));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nombre", "Código Bien Nacional", "Categoría", "Espacio"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanel7.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, 500, 360));

        jLabel18.setText("Nombre:");
        jPanel7.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 70, 30));
        jPanel7.add(jFAnchura, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 320, 130, 30));

        jCBoxCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mobiliario", "Papelería", "Tecnología", "Limpieza", "Electrodomésticos", "Otros" }));
        jPanel7.add(jCBoxCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 190, 130, 30));

        jLabel24.setText("Profundidad:");
        jPanel7.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, 170, 30));

        jLabel22.setText("Anchura:");
        jPanel7.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 160, 30));
        jPanel7.add(jFTextProfundidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 390, 130, 30));

        jLabel25.setText("Altura:");
        jPanel7.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 160, 30));
        jPanel7.add(jFTextAltura, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 250, 130, 30));

        jPanel4.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel9.setText("Codigo de Bien Nacional:");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 310, 140, 30));

        jLabel2.setText("Categoria:");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 130, 30));

        jLabel8.setText("Vida Útil:");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 60, 30));

        jLabel4.setText("Descripción:");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 70, 30));

        jLabel5.setText("Nombre:");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 70, 30));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
   
    
    private void jFTextNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTextNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTextNombreActionPerformed

    private void bCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCrearActionPerformed
       try {
            String nombre = jFTextNombre.getText().trim();
            String codigo = jFTextCodigoBienNacional.getText().trim();
            String categoria = (String) jCBoxCategoria.getSelectedItem();
            double altura = parseDoubleSafe(jFTextAltura.getText());
            double anchura = parseDoubleSafe(jFAnchura.getText());
            double profundidad = parseDoubleSafe(jFTextProfundidad.getText());

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Articulo art = new Articulo();
            art.setNombre(nombre);
            art.setCodigoBienNacional(codigo);
            art.setCategoria(categoria);
            art.setAltura(altura);
            art.setAnchura(anchura);
            art.setProfundidad(profundidad);
            art.setEspacioUnitario(altura * anchura * profundidad);

            boolean ok = controlador.crearArticulo(art);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Artículo registrado correctamente.");
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el artículo.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_bCrearActionPerformed

    private void bModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bModificarActionPerformed
        int fila = jTable1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un artículo para modificar.");
            return;
        }
        try {
            int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
            String nombre = jFTextNombre.getText().trim();
            String codigo = jFTextCodigoBienNacional.getText().trim();
            String categoria = (String) jCBoxCategoria.getSelectedItem();
            double altura = parseDoubleSafe(jFTextAltura.getText());
            double anchura = parseDoubleSafe(jFAnchura.getText());
            double profundidad = parseDoubleSafe(jFTextProfundidad.getText());

            Articulo art = new Articulo();
            art.setIdArticulo(id);
            art.setNombre(nombre);
            art.setCodigoBienNacional(codigo);
            art.setCategoria(categoria);
            art.setAltura(altura);
            art.setAnchura(anchura);
            art.setProfundidad(profundidad);
            art.setEspacioUnitario(altura * anchura * profundidad);

            if (controlador.actualizarArticulo(art)) {
                JOptionPane.showMessageDialog(this, "Artículo modificado correctamente.");
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo modificar el artículo.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_bModificarActionPerformed

    private void bEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bEliminarActionPerformed
        int fila = jTable1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un artículo para eliminar.");
            return;
        }
        String codigo = modeloTabla.getValueAt(fila, 2).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el artículo con código “" + codigo + "”?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (controlador.eliminarArticulo(codigo)) {
            modeloTabla.removeRow(fila);
            JOptionPane.showMessageDialog(this, "Artículo eliminado.");
        } else {
            JOptionPane.showMessageDialog(this, "Error al eliminar el artículo.");
        }
    }//GEN-LAST:event_bEliminarActionPerformed

    private void bSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSalirActionPerformed
        // TODO add your handling code here:
    this.dispose();
    new PrincipalVista().setVisible(true);
    }//GEN-LAST:event_bSalirActionPerformed

    private void jFTextCodigoBienNacionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTextCodigoBienNacionalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTextCodigoBienNacionalActionPerformed

    private void bConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bConsultarActionPerformed
        // TODO add your handling code here:
        try {
    String nombre = safeGet(jFTextNombre);
    String codigoBien = safeGet(jFTextCodigoBienNacional);
    String categoria = (String) jCBoxCategoria.getSelectedItem();

    modeloTabla.setRowCount(0);
    List<Articulo> lista;

    if (!nombre.isEmpty() || !codigoBien.isEmpty()) {
        lista = controlador.buscarArticulos(nombre, codigoBien);
    } else if (categoria != null && !categoria.trim().isEmpty()) {
        lista = controlador.buscarPorCategoria(categoria);
    } else {
        lista = controlador.obtenerTodosArticulosc();
    }

    if (lista == null || lista.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No se encontraron artículos con esos criterios.",
                                      "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    for (Articulo art : lista) {
        modeloTabla.addRow(new Object[] {
            art.getIdArticulo(),
            art.getNombre(),
            art.getCodigoBienNacional(),
            art.getCategoria(),
            art.getAltura(),
            art.getAnchura(),
            art.getProfundidad()
        });
    }
} catch (Exception ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(this, "Error al consultar artículos: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_bConsultarActionPerformed
private double calcularEspacio() {
        double altura = parseDoubleSafe(jFTextAltura.getText());
        double anchura = parseDoubleSafe(jFAnchura.getText());
        double profundidad = parseDoubleSafe(jFTextProfundidad.getText());
        return altura * anchura * profundidad;
    }

    private String safeGet(JFormattedTextField f) {
        try { return (f.getText() == null) ? "" : f.getText().trim(); } catch (Exception e) { return ""; }
    }

    private int parseIntSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0 : Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

    private double parseDoubleSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0.0 : Double.parseDouble(s.trim().replace(",", ".")); }
        catch (Exception e) { return 0.0; }
    }

    private double custoDouble(String s) {
        try {
            if (s == null) return 0.0;
            String t = s.trim();
            if (t.isEmpty()) return 0.0;
            return Double.parseDouble(t.replace(",", "."));
        } catch (Exception e) { return 0.0; }
    }

    private void limpiarFormulario() {
        jFTextNombre.setText("");
        jFTextCodigoBienNacional.setText("");
        jCBoxCategoria.setSelectedIndex(0);
        jFTextAltura.setText("");
        jFAnchura.setText("");
        jFTextProfundidad.setText("");    }

private void cargarFormularioDesdeTabla(int fila) {
        jFTextNombre.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
        jFTextCodigoBienNacional.setText(String.valueOf(modeloTabla.getValueAt(fila, 2)));
        jCBoxCategoria.setSelectedItem(String.valueOf(modeloTabla.getValueAt(fila, 3)));
        jFTextAltura.setText(String.valueOf(modeloTabla.getValueAt(fila, 4)));
        jFAnchura.setText(String.valueOf(modeloTabla.getValueAt(fila, 5)));
        jFTextProfundidad.setText(String.valueOf(modeloTabla.getValueAt(fila, 6)));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bConsultar;
    private javax.swing.JButton bCrear;
    private javax.swing.JButton bEliminar;
    private javax.swing.JButton bModificar;
    private javax.swing.JButton bSalir;
    private javax.swing.JComboBox<String> jCBoxCategoria;
    private javax.swing.JFormattedTextField jFAnchura;
    private javax.swing.JFormattedTextField jFTextAltura;
    private javax.swing.JFormattedTextField jFTextCodigoBienNacional;
    private javax.swing.JFormattedTextField jFTextNombre;
    private javax.swing.JFormattedTextField jFTextProfundidad;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
