package Vista;

import Modelo.Articulo;
import Controlador.ArticuloControlador;
import Modelo.Usuario; 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PanelArticulos extends javax.swing.JFrame {

    private final ArticuloControlador controlador;
    private DefaultTableModel modeloTabla;
    private Usuario usuarioActual; 

    public PanelArticulos(Usuario usuario) {
        initComponents();
        this.usuarioActual = usuario; 
        controlador = new ArticuloControlador();

        String[] headers = new String[]{
            "ID", "Nombre", "Código Bien Nacional", "Categoría", "Espacio Unitario (m³)", "Deshabilitado"
        };

        modeloTabla = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) {
                    return Boolean.class; 
                }
                return super.getColumnClass(columnIndex);
            }
        };
        jTable1.setModel(modeloTabla);

        if (jTable1.getColumnCount() > 0) {
            try {
                jTable1.getColumnModel().getColumn(0).setMinWidth(0);
                jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
                jTable1.getColumnModel().getColumn(0).setWidth(0);
                
                jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
            } catch (Exception ignored) {}
        }

        if (bCrear.getActionListeners().length == 0)
            bCrear.addActionListener(this::bCrearActionPerformed);
        if (bModificar.getActionListeners().length == 0)
            bModificar.addActionListener(this::bModificarActionPerformed);
        if (bDeshabilitar.getActionListeners().length == 0)
            bDeshabilitar.addActionListener(this::bDeshabilitarActionPerformed);
        if (bSalir.getActionListeners().length == 0)
            bSalir.addActionListener(this::bSalirActionPerformed);

        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = jTable1.getSelectedRow();
                if (fila >= 0) cargarFormularioDesdeTabla(fila);
            }
        });
        
        // ----------------------------------------------------
        // ⭐ CAMBIO REQUERIDO: Lógica de visibilidad y ajuste ⭐
        // ----------------------------------------------------
        if (usuarioActual == null || !"Administrador".equalsIgnoreCase(usuarioActual.getRol())) {
            // Ocultar bDeshabilitar si no es Administrador
            bDeshabilitar.setVisible(false);
            bCrear.setVisible(false);
            bModificar.setVisible(false);
            
        }

        cargarTabla();
    }
    
    public PanelArticulos() {
        this(null);
    }

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
                    espacio,
                    art.isDeshabilitado() 
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
        jPanelFlow = new javax.swing.JPanel();
        bCrear = new javax.swing.JButton();
        bModificar = new javax.swing.JButton();
        bConsultar = new javax.swing.JButton();
        bDeshabilitar = new javax.swing.JButton();
        bVerTodo = new javax.swing.JButton();
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

        jPanelFlow.setBackground(new java.awt.Color(255, 255, 255));
        jPanelFlow.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 40, 5));

        bCrear.setBackground(new java.awt.Color(13, 51, 131));
        bCrear.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bCrear.setForeground(new java.awt.Color(255, 255, 255));
        bCrear.setText("Registrar");
        bCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCrearActionPerformed(evt);
            }
        });
        jPanelFlow.add(bCrear);

        bModificar.setBackground(new java.awt.Color(13, 51, 131));
        bModificar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bModificar.setForeground(new java.awt.Color(255, 255, 255));
        bModificar.setText("Modificar");
        bModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bModificarActionPerformed(evt);
            }
        });
        jPanelFlow.add(bModificar);

        bConsultar.setBackground(new java.awt.Color(13, 51, 131));
        bConsultar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bConsultar.setForeground(new java.awt.Color(255, 255, 255));
        bConsultar.setText("Consultar");
        bConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConsultarActionPerformed(evt);
            }
        });
        jPanelFlow.add(bConsultar);

        bDeshabilitar.setBackground(new java.awt.Color(13, 51, 131));
        bDeshabilitar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bDeshabilitar.setForeground(new java.awt.Color(255, 255, 255));
        bDeshabilitar.setText("Deshabilitar");
        bDeshabilitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDeshabilitarActionPerformed(evt);
            }
        });
        jPanelFlow.add(bDeshabilitar);

        bVerTodo.setBackground(new java.awt.Color(13, 51, 131));
        bVerTodo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bVerTodo.setForeground(new java.awt.Color(255, 255, 255));
        bVerTodo.setText("Ver Todo");
        bVerTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bVerTodoActionPerformed(evt);
            }
        });
        jPanelFlow.add(bVerTodo);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelFlow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jPanelFlow, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nombre", "Código Bien Nacional", "Categoría", "Espacio", "Deshabilitado"
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
    // 1. Obtener ID de la tabla (Esto es correcto)
    int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
    
    // 2. Leer los datos de los campos de texto (ya cargados por cargarFormularioDesdeTabla)
    String nombre = jFTextNombre.getText().trim();
    String codigo = jFTextCodigoBienNacional.getText().trim();
    String categoria = (String) jCBoxCategoria.getSelectedItem();
    
    // Lectura de dimensiones (Asegúrate que los nombres coincidan con tu diseño)
    double altura = parseDoubleSafe(jFTextAltura.getText());
    double anchura = parseDoubleSafe(jFAnchura.getText());  // Usamos el nombre consistente
    double profundidad = parseDoubleSafe(jFTextProfundidad.getText()); 

    // 3. Validación
    if (nombre.isEmpty() || codigo.isEmpty() || altura <= 0 || anchura <= 0 || profundidad <= 0) {
         JOptionPane.showMessageDialog(this, "Todos los campos (Nombre, Código y Dimensiones) son obligatorios y deben ser válidos.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
         return;
    }

    // 4. Crear y asignar el objeto
    Articulo art = new Articulo();
    art.setIdArticulo(id);
    art.setNombre(nombre);
    art.setCodigoBienNacional(codigo);
    art.setCategoria(categoria);
    art.setAltura(altura);
    art.setAnchura(anchura);
    art.setProfundidad(profundidad);
    art.setEspacioUnitario(altura * anchura * profundidad); 

    // 5. Ejecutar actualización
    if (controlador.actualizarArticulo(art)) {
        JOptionPane.showMessageDialog(this, "Artículo modificado correctamente.");
        cargarTabla();
    } else {
        JOptionPane.showMessageDialog(this, "No se pudo modificar el artículo. Verifique si el artículo existe o si el controlador falló.");
    }

} catch (Exception e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(this, "Error al modificar el artículo: " + e.getMessage() + ". Revise el formato numérico.", "Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_bModificarActionPerformed

    private void bDeshabilitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeshabilitarActionPerformed
        int fila = jTable1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un artículo para cambiar su estado.");
            return;
        }
        
        // Obtenemos los datos de la fila seleccionada
        int idArticulo = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = modeloTabla.getValueAt(fila, 1).toString();
        boolean estadoActual = (boolean) modeloTabla.getValueAt(fila, 5);
        
        // Invertimos el estado para el mensaje y la acción
        boolean nuevoEstado = !estadoActual;
        String accion = nuevoEstado ? "DESHABILITAR" : "HABILITAR";
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea " + accion + " el artículo: " + nombre + "?",
                "Confirmar Acción", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;

        // Llamamos al controlador para actualizar el estado en la base de datos
        // NOTA: Debes crear este método en ArticuloControlador
        if (controlador.actualizarEstadoDeshabilitado(idArticulo, nuevoEstado)) {
            // Si la operación es exitosa en la DB, actualizamos la tabla en la vista
            modeloTabla.setValueAt(nuevoEstado, fila, 5);
            JOptionPane.showMessageDialog(this, "Artículo " + accion.toLowerCase() + " correctamente.");
        } else {
            JOptionPane.showMessageDialog(this, "Error al " + accion.toLowerCase() + " el artículo.");
        }
    }//GEN-LAST:event_bDeshabilitarActionPerformed

    private void bSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSalirActionPerformed
        // TODO add your handling code here:
    this.dispose();
    new PrincipalVista(this.usuarioActual).setVisible(true); // Pasa el usuario de vuelta
    }//GEN-LAST:event_bSalirActionPerformed

    private void jFTextCodigoBienNacionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTextCodigoBienNacionalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTextCodigoBienNacionalActionPerformed

    private void bConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bConsultarActionPerformed
        // TODO add your handling code here:
        try {
    // 1. Obtener los valores de los tres campos
    String nombre = safeGet(jFTextNombre);
    String codigoBien = safeGet(jFTextCodigoBienNacional);
    
    String categoria = "";
    if (jCBoxCategoria.getSelectedItem() != null) {
        categoria = jCBoxCategoria.getSelectedItem().toString().trim();
    }

    // 2. Limpiar la tabla
    modeloTabla.setRowCount(0);

    // 3. Llamar al método del controlador
    List<Articulo> lista = controlador.buscarArticulosCombinado(nombre, codigoBien, categoria);

    // 4. Manejar resultados y llenar la tabla
    if (lista.isEmpty()) {
        if (!nombre.isEmpty() || !codigoBien.isEmpty() || (!categoria.isEmpty() && !categoria.equalsIgnoreCase("Todas") && !categoria.startsWith("Seleccione"))) {
            JOptionPane.showMessageDialog(this, "No se encontraron artículos con esos criterios.",
                                              "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // 5. Llenar la tabla (¡CORREGIDO!)
    for (Articulo art : lista) {
        double espacio = art.getAltura() * art.getAnchura() * art.getProfundidad(); // Calculamos el espacio para la Col 5
        
        modeloTabla.addRow(new Object[] {
            art.getIdArticulo(),
            art.getNombre(),
            art.getCodigoBienNacional(),
            art.getCategoria(),
            espacio,                   // Columna 5: Espacio Unitario (calculado)
            art.isDeshabilitado()      // Columna 6: Deshabilitado (Booleano)
        });
    }
    
} catch (Exception ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(this, "Error al consultar artículos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_bConsultarActionPerformed

    private void bVerTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bVerTodoActionPerformed
        // TODO add your handling code here:
        try {
        // 1. Limpiar los campos de filtro principales
        jFTextNombre.setText(""); 
        jFTextCodigoBienNacional.setText("");
        
        // 2. Limpiar el JComboBox de Categoria (se asume que null o el primer 
        // elemento permite ver "TODAS" las categorías)
        jCBoxCategoria.setSelectedItem(null); 
        
        // 3. Limpiar los campos de dimensiones para evitar confusiones al crear un nuevo artículo.
        jFTextAltura.setText("");
        jFAnchura.setText("");
        jFTextProfundidad.setText("");
        
        // 4. Recargar la tabla con todos los artículos (sin filtros aplicados)
        cargarTabla();
        
        // Opcional: Mensaje de confirmación
        JOptionPane.showMessageDialog(this, "Tabla de artículos actualizada. Filtros limpiados.", "Información", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al actualizar la tabla de artículos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_bVerTodoActionPerformed
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
    try {
        // 1. Obtener el ID del artículo seleccionado (Columna 0, oculta)
        int idArticulo = (Integer) modeloTabla.getValueAt(fila, 0);

        // 2. Usar el controlador para buscar el objeto Articulo completo
        // ⭐ DEBES ASEGURARTE DE QUE ArticuloControlador TENGA este método:
        Articulo art = controlador.obtenerArticuloPorId(idArticulo); 
        
        if (art != null) {
            // 3. Cargar datos básicos (estos estaban bien)
            jFTextNombre.setText(art.getNombre());
            jFTextCodigoBienNacional.setText(art.getCodigoBienNacional());
            jCBoxCategoria.setSelectedItem(art.getCategoria());
            
            // 4. Cargar las DIMENSIONES DEL OBJETO ARTICULO (que vienen de la BD)
            // Esto reemplaza la lectura incorrecta de la tabla:
            jFTextAltura.setText(String.valueOf(art.getAltura()));
            // Ajusta el nombre del campo de Anchura si es jFAnchura en tu diseño:
            jFAnchura.setText(String.valueOf(art.getAnchura())); 
            jFTextProfundidad.setText(String.valueOf(art.getProfundidad()));
            
            // Si tu diseño tiene un campo para el ID (oculto), también podrías cargarlo aquí.
        } else {
             JOptionPane.showMessageDialog(this, "No se encontró el artículo completo en la base de datos.", "Error de Datos", JOptionPane.WARNING_MESSAGE);
        }
        
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar datos del formulario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bConsultar;
    private javax.swing.JButton bCrear;
    private javax.swing.JButton bDeshabilitar;
    private javax.swing.JButton bModificar;
    private javax.swing.JButton bSalir;
    private javax.swing.JButton bVerTodo;
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
    private javax.swing.JPanel jPanelFlow;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
