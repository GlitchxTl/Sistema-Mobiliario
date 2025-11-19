package Vista;

import Modelo.Ubicacion;
import Modelo.UbicacionDAO;
import Modelo.Usuario;
import Controlador.UbicacionControlador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PanelUbicacion extends javax.swing.JFrame {

    private final UbicacionDAO dao = new UbicacionDAO();
    private final UbicacionControlador controlador = new UbicacionControlador();
    private DefaultTableModel modeloTabla;
    private Usuario usuarioActual; 

    public interface UbicacionChangeListener {
        void onUbicacionesChanged();
    }
    private final List<UbicacionChangeListener> listeners = new ArrayList<>();

    public PanelUbicacion(Usuario usuario) {
        initComponents();
        this.usuarioActual = usuario;
        
        if (usuarioActual == null || !"Administrador".equalsIgnoreCase(usuarioActual.getRol())) {
            // Ocultar bDeshabilitar si no es Administrador
            bDeshabilitarUbicación.setVisible(false);
            bCrearUbicacion.setVisible(false);
            bModificarUbicacion.setVisible(false);
            
        }
        inicializar();
        cargarTabla(); // Asegúrate de llamar a cargarTabla aquí
    }
    
    public PanelUbicacion() {
        this(null);
    }
    
    private void inicializar() {
        // ⭐ CAMBIO 1: Añadir la columna "Deshabilitado" ⭐
        String[] headers = new String[] { 
            "ID", "Nombre", "Capacidad (m³)", "Capacidad Restante (m³)", "Descripción", "Deshabilitado" 
        };
        
        modeloTabla = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            // ⭐ CAMBIO 2: Definir la columna "Deshabilitado" como Boolean ⭐
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) {
                    return Boolean.class; 
                }
                return super.getColumnClass(columnIndex);
            }
        };
        jTablaUbicaciones.setModel(modeloTabla);

        // Ocultar columna ID visualmente
        try {
            jTablaUbicaciones.getColumnModel().getColumn(0).setMinWidth(0);
            jTablaUbicaciones.getColumnModel().getColumn(0).setMaxWidth(0);
            jTablaUbicaciones.getColumnModel().getColumn(0).setWidth(0);
            
            jTablaUbicaciones.getColumnModel().getColumn(5).setPreferredWidth(100);
        } catch (Exception ignored) {}

        // Doble click selecciona fila y llena campos (Llamando al nuevo método)
        jTablaUbicaciones.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = jTablaUbicaciones.getSelectedRow();
                    if (fila >= 0) {
                        cargarFormularioDesdeTabla(fila); // ⭐ Se usa el nuevo método
                    }
                }
            }
        });

        cargarTabla();
    }
    
private void cargarFormularioDesdeTabla(int fila) {
    try {
        // 1. Obtener el ID de la fila seleccionada (Columna 0, oculta)
        Object idObj = modeloTabla.getValueAt(fila, 0);
        if (idObj == null) return;
        
        int idUbicacion = Integer.parseInt(idObj.toString());

        // 2. Usar el controlador para obtener el objeto Ubicacion completo desde la BD.
        // REQUIERE que UbicacionControlador tenga: obtenerUbicacionPorId(int id)
        Ubicacion ubicacion = controlador.obtenerPorId(idUbicacion);

        if (ubicacion != null) {
            // 3. Cargar datos básicos y la descripción
            jFTNombre.setText(ubicacion.getNombre());
            jFTDescripcion.setText(ubicacion.getDescripcion());
            
            // 4. Cargar las DIMENSIONES DEL OBJETO UBICACION (Altura, Anchura, Profundidad)
            jFTAltura.setText(String.valueOf(ubicacion.getAltura()));
            jFTAnchura.setText(String.valueOf(ubicacion.getAnchura())); 
            jFTProfundidad.setText(String.valueOf(ubicacion.getProfundidad()));

            // NOTA: Se omitieron jFTCapacidadTotal y jFTCapacidadRestante 
            // porque indicaste que esos campos no existen en el formulario.

        } else {
             JOptionPane.showMessageDialog(this, "No se encontró la ubicación completa en la base de datos.", "Error de Datos", JOptionPane.WARNING_MESSAGE);
        }
        
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar datos del formulario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    


    private void cargarTabla() {
        try {
        modeloTabla.setRowCount(0);
        
        // ⭐ Corregido: Usar el método que existe en el controlador.
        List<Ubicacion> lista = controlador.obtenerTodasUbicaciones();
        
        DecimalFormat df = new DecimalFormat("#.###");
        
        for (Ubicacion u : lista) {
            modeloTabla.addRow(new Object[] {
                // 1. ID: Usar el getter correcto de tu modelo
                u.getId_ubicacion(), 
                
                // 2. Nombre
                u.getNombre(),
                
                // 3. Capacidad Total: Usar el getter correcto de tu modelo
                df.format(u.getCapacidad()), 
                
                // 4. Capacidad Restante: (Este es correcto)
                df.format(u.getCapacidadRestante()),
                
                // 5. Descripción: (Este es correcto)
                u.getDescripcion(),
                
                // 6. Deshabilitado: (Este es correcto y ya se agregó al modelo)
                u.isDeshabilitado() 
                });
            }
        } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error cargando ubicaciones: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- registro de listeners externos ----------
    public void addUbicacionChangeListener(UbicacionChangeListener l) {
        if (l == null) return;
        listeners.remove(l);
        listeners.add(l);
    }
    private void notifyChangeListeners() {
        for (UbicacionChangeListener l : new ArrayList<>(listeners)) {
            try { l.onUbicacionesChanged(); } catch (Exception ignored) {}
        }
    }
    
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
        jButton6 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jFTDescripcion = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        jFTProfundidad = new javax.swing.JFormattedTextField();
        jFTNombre = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablaUbicaciones = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        jFTAltura = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        jFTAnchura = new javax.swing.JFormattedTextField();
        jPanel5 = new javax.swing.JPanel();
        bCrearUbicacion = new javax.swing.JButton();
        bConsultarUbicacion = new javax.swing.JButton();
        bVerTodo = new javax.swing.JButton();
        bModificarUbicacion = new javax.swing.JButton();
        bDeshabilitarUbicación = new javax.swing.JButton();

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
        jLabel1.setText("Ubicaciones");
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

        jButton6.setBackground(new java.awt.Color(13, 51, 131));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Volver");
        jButton6.setBorder(null);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, 170, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 110));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Nombre:");
        jPanel4.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 210, 51));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Descripción:");
        jPanel4.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 120, 50));

        jFTDescripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTDescripcionActionPerformed(evt);
            }
        });
        jPanel4.add(jFTDescripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 280, 230, 50));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Profundidad");
        jPanel4.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 210, 51));

        jFTProfundidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTProfundidadActionPerformed(evt);
            }
        });
        jPanel4.add(jFTProfundidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 210, 140, 50));

        jFTNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTNombreActionPerformed(evt);
            }
        });
        jPanel4.add(jFTNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 140, 50));

        jTablaUbicaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nombre", "Capacidad (m³):", "Capacidad Restante", "Descripción", "Deshabilitado"
            }
        ));
        jScrollPane1.setViewportView(jTablaUbicaciones);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 0, 430, 330));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Altura:");
        jPanel4.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 210, 51));

        jFTAltura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTAlturaActionPerformed(evt);
            }
        });
        jPanel4.add(jFTAltura, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 80, 140, 50));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("Anchura:");
        jPanel4.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 210, 51));

        jFTAnchura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTAnchuraActionPerformed(evt);
            }
        });
        jPanel4.add(jFTAnchura, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 150, 140, 50));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 40, 5));

        bCrearUbicacion.setBackground(new java.awt.Color(13, 51, 131));
        bCrearUbicacion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bCrearUbicacion.setForeground(new java.awt.Color(255, 255, 255));
        bCrearUbicacion.setText("Registrar");
        bCrearUbicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCrearUbicacionActionPerformed(evt);
            }
        });
        jPanel5.add(bCrearUbicacion);

        bConsultarUbicacion.setBackground(new java.awt.Color(13, 51, 131));
        bConsultarUbicacion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bConsultarUbicacion.setForeground(new java.awt.Color(255, 255, 255));
        bConsultarUbicacion.setText("Consultar");
        bConsultarUbicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConsultarUbicacionActionPerformed(evt);
            }
        });
        jPanel5.add(bConsultarUbicacion);

        bVerTodo.setBackground(new java.awt.Color(13, 51, 131));
        bVerTodo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bVerTodo.setForeground(new java.awt.Color(255, 255, 255));
        bVerTodo.setText("Ver Todo");
        bVerTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bVerTodoActionPerformed(evt);
            }
        });
        jPanel5.add(bVerTodo);

        bModificarUbicacion.setBackground(new java.awt.Color(13, 51, 131));
        bModificarUbicacion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bModificarUbicacion.setForeground(new java.awt.Color(255, 255, 255));
        bModificarUbicacion.setText("Modificar");
        bModificarUbicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bModificarUbicacionActionPerformed(evt);
            }
        });
        jPanel5.add(bModificarUbicacion);

        bDeshabilitarUbicación.setBackground(new java.awt.Color(13, 51, 131));
        bDeshabilitarUbicación.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bDeshabilitarUbicación.setForeground(new java.awt.Color(255, 255, 255));
        bDeshabilitarUbicación.setText("Deshilitar");
        bDeshabilitarUbicación.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDeshabilitarUbicaciónActionPerformed(evt);
            }
        });
        jPanel5.add(bDeshabilitarUbicación);

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 340, 800, 50));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 800, 390));

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

    private void jFTDescripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTDescripcionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTDescripcionActionPerformed

    private void jFTProfundidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTProfundidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTProfundidadActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        this.dispose();
        // Pasa el usuario de vuelta al menú principal
        new PrincipalVista(this.usuarioActual).setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jFTNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTNombreActionPerformed

    private void bModificarUbicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bModificarUbicacionActionPerformed
        int fila = jTablaUbicaciones.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = parseIntSafe(String.valueOf(modeloTabla.getValueAt(fila, 0)));
            String nombre = safeGet(jFTNombre);
            double altura = parseDoubleSafe(jFTAltura);
            double anchura = parseDoubleSafe(jFTAnchura);
            double profundidad = parseDoubleSafe(jFTProfundidad);
            String descripcion = safeGet(jFTDescripcion);

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Ubicacion u = new Ubicacion(id, nombre, altura, anchura, profundidad, descripcion);
            boolean ok = dao.actualizar(u);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Ubicación actualizada.", "OK", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarTabla();
                notifyChangeListeners();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar la ubicación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al modificar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bModificarUbicacionActionPerformed

    private void bCrearUbicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCrearUbicacionActionPerformed
        try {
            String nombre = safeGet(jFTNombre);
            double altura = parseDoubleSafe(jFTAltura);
            double anchura = parseDoubleSafe(jFTAnchura);
            double profundidad = parseDoubleSafe(jFTProfundidad);
            String descripcion = safeGet(jFTDescripcion);

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (altura <= 0 || anchura <= 0 || profundidad <= 0) {
                JOptionPane.showMessageDialog(this, "Las dimensiones deben ser positivas.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Ubicacion u = new Ubicacion(nombre, altura, anchura, profundidad, descripcion);
            boolean ok = dao.crear(u);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Ubicación creada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarTabla();
                notifyChangeListeners();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo crear la ubicación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al crear ubicación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bCrearUbicacionActionPerformed

    private void bDeshabilitarUbicaciónActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeshabilitarUbicaciónActionPerformed
    int fila = jTablaUbicaciones.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una ubicación para cambiar su estado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtener datos de la fila
        int id = parseIntSafe(String.valueOf(modeloTabla.getValueAt(fila, 0)));
        String nombre = String.valueOf(modeloTabla.getValueAt(fila, 1));
        boolean estadoActual = (boolean) modeloTabla.getValueAt(fila, 5);
        
        // Invertir el estado
        boolean nuevoEstado = !estadoActual;
        String accion = nuevoEstado ? "DESHABILITAR" : "HABILITAR";
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea " + accion + " la ubicación \"" + nombre + "\"?",
                "Confirmar Acción",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // Llamar al controlador para actualizar el estado
            if (controlador.actualizarEstadoDeshabilitado(id, nuevoEstado)) {
                // Actualizar la vista (modeloTabla) si la DB fue exitosa
                modeloTabla.setValueAt(nuevoEstado, fila, 5);
                JOptionPane.showMessageDialog(this, "Ubicación " + accion.toLowerCase() + " correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo " + accion.toLowerCase() + " la ubicación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al " + accion.toLowerCase() + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bDeshabilitarUbicaciónActionPerformed

    private void jFTAlturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTAlturaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTAlturaActionPerformed

    private void jFTAnchuraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTAnchuraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTAnchuraActionPerformed

    private void bConsultarUbicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bConsultarUbicacionActionPerformed
        // TODO add your handling code here:
        try {
    // 1. Obtener el nombre y limpiar la tabla
    String nombre = safeGet(jFTNombre); // Asumo que safeGet() hace .getText().trim()
    modeloTabla.setRowCount(0);

    List<Ubicacion> lista;
    
    // 2. Decidir si buscar o listar todo (Esto ya lo tenías bien)
    if (nombre.isEmpty()) {
        // Si el campo está vacío, "consultar" significa "mostrar todos"
        lista = controlador.obtenerTodasUbicaciones(); // Llama al controlador para obtener todo
    } else {
        // Si hay texto, "consultar" significa "buscar"
        lista = controlador.buscarPorNombre(nombre); // Llama al controlador para buscar
    }

    // 3. --- LÓGICA DE MENSAJE MEJORADA ---
    if (lista == null || lista.isEmpty()) {
        // Solo mostrar el popup si el usuario INTENTÓ buscar algo
        if (!nombre.isEmpty()) { 
            JOptionPane.showMessageDialog(this, 
                "No se encontraron ubicaciones con ese nombre.", 
                "Sin resultados", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        // Si 'nombre' estaba vacío (quería ver todos) y la lista está vacía,
        // simplemente se muestra la tabla vacía. No se necesita un popup.
        return; 
    }

    // 4. Si hay resultados, llenar la tabla
    DecimalFormat df = new DecimalFormat("#.###"); // Es mejor crear esto fuera del bucle
    for (Ubicacion u : lista) {
        modeloTabla.addRow(new Object[]{
            u.getId_ubicacion(),
            u.getNombre(),
            df.format(u.getCapacidad()),
            df.format(u.getCapacidadRestante()),
            u.getDescripcion()
        });
    }
    
} catch (Exception ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(this, "Error al consultar ubicaciones: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_bConsultarUbicacionActionPerformed

    private void bVerTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bVerTodoActionPerformed
        // TODO add your handling code here:
        try {
        // 1. Limpiar el campo de texto de filtro (jFTNombre)
        // Se asume que jFTNombre es el campo usado para el filtro.
        jFTNombre.setText(""); 
        
        // 2. Limpiar los campos del formulario de registro/modificación 
        // y resetear el ID seleccionado si aplica (buena práctica).
        limpiarCampos(); 
        
        // 3. Recargar la tabla con todos los registros (sin filtros).
        // Se asume que 'cargarTabla()' llama a 'dao.listar()'
        cargarTabla();
        
        // Opcional: Notificación al usuario
        // JOptionPane.showMessageDialog(this, "Tabla de ubicaciones actualizada, filtros limpiados.", "Información", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al actualizar la tabla de ubicaciones: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_bVerTodoActionPerformed

private void limpiarCampos() {
        jFTNombre.setText("");
        jFTAltura.setText("");
        jFTAnchura.setText("");
        jFTProfundidad.setText("");
        jFTDescripcion.setText("");
    }

    private String safeGet(JFormattedTextField f) {
        try { return (f.getText() == null) ? "" : f.getText().trim(); } catch (Exception e) { return ""; }
    }

    private int parseIntSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0 : Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

    private double parseDoubleSafe(JFormattedTextField f) {
        return parseDoubleSafe(safeGet(f));
    }
    private double parseDoubleSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0.0 : Double.parseDouble(s.trim().replace(",", ".")); }
        catch (Exception e) { return 0.0; }
    }

    // ---------- getters para otros paneles ----------
    public java.util.List<String> getNombresUbicaciones() {
        try {
            List<String> nombres = new ArrayList<>();
            for (Ubicacion u : dao.listar()) nombres.add(u.getNombre());
            return nombres;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bConsultarUbicacion;
    private javax.swing.JButton bCrearUbicacion;
    private javax.swing.JButton bDeshabilitarUbicación;
    private javax.swing.JButton bModificarUbicacion;
    private javax.swing.JButton bVerTodo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JFormattedTextField jFTAltura;
    private javax.swing.JFormattedTextField jFTAnchura;
    private javax.swing.JFormattedTextField jFTDescripcion;
    private javax.swing.JFormattedTextField jFTNombre;
    private javax.swing.JFormattedTextField jFTProfundidad;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTablaUbicaciones;
    // End of variables declaration//GEN-END:variables
}
