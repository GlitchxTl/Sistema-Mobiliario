package Vista;

import Modelo.Ubicacion;
import Modelo.UbicacionDAO;
import Controlador.UbicacionControlador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * PanelUbicacion — UI con crear / modificar / eliminar.
 * Ahora trabaja directamente con UbicacionDAO (BD) y calcula volumen (m³) a partir de altura, anchura y profundidad.
 */
public class PanelUbicacion extends javax.swing.JFrame {

    private final UbicacionDAO dao = new UbicacionDAO();
    private final UbicacionControlador controlador = new UbicacionControlador();
    private DefaultTableModel modeloTabla;

    // Observers para notificar cambios de ubicaciones
    public interface UbicacionChangeListener {
        void onUbicacionesChanged();
    }
    private final List<UbicacionChangeListener> listeners = new ArrayList<>();

    public PanelUbicacion() {
        initComponents();
        inicializar();
    }

    private void inicializar() {
        // configurar tabla
        String[] headers = new String[] { "ID", "Nombre", "Capacidad (m³)", "Capacidad Restante (m³)", "Descripción" };
        modeloTabla = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        jTablaUbicaciones.setModel(modeloTabla);

        // ocultar columna ID visualmente
        try {
            jTablaUbicaciones.getColumnModel().getColumn(0).setMinWidth(0);
            jTablaUbicaciones.getColumnModel().getColumn(0).setMaxWidth(0);
            jTablaUbicaciones.getColumnModel().getColumn(0).setWidth(0);
        } catch (Exception ignored) {}

        // doble click selecciona fila y llena campos
        jTablaUbicaciones.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = jTablaUbicaciones.getSelectedRow();
                    if (fila >= 0) {
                        jFTNombre.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
                        jFTDescripcion.setText(String.valueOf(modeloTabla.getValueAt(fila, 4)));
                    }
                }
            }
        });

        cargarTabla();
    }

    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Ubicacion> lista = dao.listar();
            DecimalFormat df = new DecimalFormat("#.###");
            for (Ubicacion u : lista) {
                modeloTabla.addRow(new Object[] {
                        u.getId_ubicacion(),
                        u.getNombre(),
                        df.format(u.getCapacidad()),
                        df.format(u.getCapacidadRestante()),
                        u.getDescripcion()
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
        bEliminarUbicacion = new javax.swing.JButton();
        jFTNombre = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablaUbicaciones = new javax.swing.JTable();
        bCrearUbicacion = new javax.swing.JButton();
        bModificarUbicacion = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jFTAltura = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        jFTAnchura = new javax.swing.JFormattedTextField();
        bConsultarUbicacion = new javax.swing.JButton();

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

        bEliminarUbicacion.setBackground(new java.awt.Color(13, 51, 131));
        bEliminarUbicacion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bEliminarUbicacion.setForeground(new java.awt.Color(255, 255, 255));
        bEliminarUbicacion.setText("Eliminar");
        bEliminarUbicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bEliminarUbicacionActionPerformed(evt);
            }
        });
        jPanel4.add(bEliminarUbicacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 340, 170, 40));

        jFTNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFTNombreActionPerformed(evt);
            }
        });
        jPanel4.add(jFTNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 140, 50));

        jTablaUbicaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nombre", "Capacidad (m³):", "Capacidad Restante", "Descripción"
            }
        ));
        jScrollPane1.setViewportView(jTablaUbicaciones);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 0, 430, 310));

        bCrearUbicacion.setBackground(new java.awt.Color(13, 51, 131));
        bCrearUbicacion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bCrearUbicacion.setForeground(new java.awt.Color(255, 255, 255));
        bCrearUbicacion.setText("Crear");
        bCrearUbicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCrearUbicacionActionPerformed(evt);
            }
        });
        jPanel4.add(bCrearUbicacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 170, 40));

        bModificarUbicacion.setBackground(new java.awt.Color(13, 51, 131));
        bModificarUbicacion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bModificarUbicacion.setForeground(new java.awt.Color(255, 255, 255));
        bModificarUbicacion.setText("Modificar");
        bModificarUbicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bModificarUbicacionActionPerformed(evt);
            }
        });
        jPanel4.add(bModificarUbicacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 340, 170, 40));

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

        bConsultarUbicacion.setBackground(new java.awt.Color(13, 51, 131));
        bConsultarUbicacion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bConsultarUbicacion.setForeground(new java.awt.Color(255, 255, 255));
        bConsultarUbicacion.setText("Consultar");
        bConsultarUbicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConsultarUbicacionActionPerformed(evt);
            }
        });
        jPanel4.add(bConsultarUbicacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 340, 170, 40));

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
        PrincipalVista principalvista = new PrincipalVista();
        principalvista.setVisible(true);
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

    private void bEliminarUbicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bEliminarUbicacionActionPerformed
    int fila = jTablaUbicaciones.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = parseIntSafe(String.valueOf(modeloTabla.getValueAt(fila, 0)));
        String nombre = String.valueOf(modeloTabla.getValueAt(fila, 1));
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar ubicación \"" + nombre + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = dao.eliminar(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Ubicación eliminada.", "OK", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarTabla();
                notifyChangeListeners();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la ubicación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bEliminarUbicacionActionPerformed

    private void jFTAlturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTAlturaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTAlturaActionPerformed

    private void jFTAnchuraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTAnchuraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTAnchuraActionPerformed

    private void bConsultarUbicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bConsultarUbicacionActionPerformed
        // TODO add your handling code here:
         try {
        String nombre = safeGet(jFTNombre);
        modeloTabla.setRowCount(0);

        List<Ubicacion> lista;
        if (nombre.isEmpty()) {
            lista = controlador.listarTodas();
        } else {
            lista = controlador.buscarPorNombre(nombre);
        }

        if (lista == null || lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron ubicaciones.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DecimalFormat df = new DecimalFormat("#.###");
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
    private javax.swing.JButton bEliminarUbicacion;
    private javax.swing.JButton bModificarUbicacion;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTablaUbicaciones;
    // End of variables declaration//GEN-END:variables
}
