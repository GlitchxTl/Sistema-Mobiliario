package Vista;

import Controlador.ArticuloControlador;
import Controlador.MovimientoControlador;
import Modelo.Articulo;
import Modelo.Usuario;
import Modelo.Movimiento;
import Modelo.Ubicacion;
import Modelo.UbicacionDAO;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import Modelo.CapacidadInsuficienteException;

public class PanelTraslado extends javax.swing.JFrame {

    private final ArticuloControlador articuloControl = new ArticuloControlador();
    private final MovimientoControlador movimientoControl = new MovimientoControlador();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();
    private Usuario usuarioActual; 
    private Long idMovimientoSeleccionado = null; 

    // Constructor que recibe el usuario
    public PanelTraslado(Usuario usuario) {
        initComponents();
        this.setResizable(false);
        this.usuarioActual = usuario; 
        
        // ⭐ NUEVA LÓGICA: Ocultar bModificar
        ocultarBotonModificar(); 
        
        cargarCombos();
        cargarTablaTraslados();

        bRegistrarTraslado.addActionListener(e -> onRegistrarTraslado());
        
        // ⭐ NUEVA LÓGICA: Configurar evento de la tabla
        configurarEventoTabla();
    }
    
    // (Dejas el constructor vacío si el diseñador lo requiere)
    public PanelTraslado() {
        this(null);
    }
    
    // -----------------------------------------------------------------------
    // --- LÓGICA DE VISIBILIDAD Y AUXILIARES ---
    // -----------------------------------------------------------------------
    
    private void ocultarBotonModificar() {
        // Si el usuario no es 'Administrador', el botón de Modificar se oculta.
        // Se asume que existe un componente llamado 'bModificar'.
        if (usuarioActual != null && !"Administrador".equals(usuarioActual.getRol())) {
            // Descomentado: Esto oculta el botón si el rol no es Administrador.
            bModificar.setVisible(false);
        }
    }

    /** Cargar combos de artículos y ubicaciones */
    private void cargarCombos() {
        try {
            // Artículos
            DefaultComboBoxModel<Articulo> modeloArticulos = new DefaultComboBoxModel<>();
            List<Articulo> articulos = articuloControl.obtenerTodosArticulos();
            for (Articulo a : articulos) {
                modeloArticulos.addElement(a);
            }
            jCBArticuloTraslado.setModel(modeloArticulos);

            // Ubicaciones
            List<Ubicacion> ubicaciones = ubicacionDAO.listar();
            
            DefaultComboBoxModel<Ubicacion> modeloUbicOrigen = new DefaultComboBoxModel<>();
            DefaultComboBoxModel<Ubicacion> modeloUbicDestino = new DefaultComboBoxModel<>();
            
            for (Ubicacion u : ubicaciones) {
                modeloUbicOrigen.addElement(u);
                modeloUbicDestino.addElement(u);
            }
            
            jCBUbicacionOrigen.setModel(modeloUbicOrigen);
            jCBUbicacionDestino.setModel(modeloUbicDestino); // Usar modelo distinto para permitir diferentes selecciones

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Acción principal: registrar traslado */
    private void onRegistrarTraslado() {
        // Lógica de registro (se mantiene igual, solo se agrega validación de origen != destino)
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
            if (Objects.equals(origen.getId_ubicacion(), destino.getId_ubicacion())) {
            JOptionPane.showMessageDialog(this, "La ubicación origen y destino no pueden ser la misma.",
            "Validación", JOptionPane.WARNING_MESSAGE);
            return;
            }
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida (>0).",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // ... (Lógica de capacidad y llamado al controlador) ...
            
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

    // -----------------------------------------------------------------------
    // --- LÓGICA DE TABLA (CARGAR Y EVENTO) ---
    // -----------------------------------------------------------------------

    /** Cargar tabla de traslados recientes */
    private void cargarTablaTraslados() {
        try {
            List<Movimiento> traslados = movimientoControl.obtenerTraslados(); 
            
            String[] columnas = {"ID", "Artículo", "Cantidad", "Origen", "Destino", "Entregado Por", "Fecha/Hora"};
            
            DefaultTableModel nuevoModel = new DefaultTableModel(columnas, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) return Long.class;      // ID (Long)
                    if (columnIndex == 2) return Integer.class;   // Cantidad (Integer)
                    return Object.class;
                }
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Asumiendo que la tabla se llama jTable1
            jTableTraslado.setModel(nuevoModel);
            // Ocultar la columna del ID (Columna 0)
            jTableTraslado.getColumnModel().getColumn(0).setMinWidth(0);
            jTableTraslado.getColumnModel().getColumn(0).setMaxWidth(0);
            jTableTraslado.getColumnModel().getColumn(0).setWidth(0);
            
            for (Movimiento m : traslados) {
                nuevoModel.addRow(new Object[]{
                    m.getIdMovimiento(), // ID en la columna 0
                    m.getNombreArticulo(),
                    m.getCantidad(),
                    m.getNombreUbicacionOrigen(),
                    m.getNombreUbicacionDestino(),
                    m.getEntregado(), 
                    m.getFechaHora()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando tabla de traslados: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /** Configura el evento de clic en la tabla para cargar el formulario. */
    private void configurarEventoTabla() {
        jTableTraslado.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    cargarFormularioDesdeTablas();
                }
            }
        });
    }

    /** ⭐ NUEVO MÉTODO: Carga los datos de la fila seleccionada de la tabla al formulario. ⭐ */
    private void cargarFormularioDesdeTablas() {
        int fila = jTableTraslado.getSelectedRow();
        if (fila >= 0) {
            DefaultTableModel model = (DefaultTableModel) jTableTraslado.getModel();

            // 1. Obtener el ID del movimiento (Columna 0) y manejar Integer/Long
            Object idObj = model.getValueAt(fila, 0);
            Long idMov = null;
            if (idObj instanceof Integer) {
                idMov = ((Integer) idObj).longValue();
            } else if (idObj instanceof Long) {
                idMov = (Long) idObj;
            }
            this.idMovimientoSeleccionado = idMov;
            
            // 2. Obtener nombres de Artículo y Ubicaciones
            String nombreArticulo = (String) model.getValueAt(fila, 1);
            String nombreUbicacionOrigen = (String) model.getValueAt(fila, 3);
            String nombreUbicacionDestino = (String) model.getValueAt(fila, 4);
            
            // 3. Cantidad y Entregado
            jFTCantidad.setText(model.getValueAt(fila, 2).toString());
            jFTEntregadoA.setText((String) model.getValueAt(fila, 5));

            // 4. Seleccionar en Comboboxes
            seleccionarEnComboBox(jCBArticuloTraslado, nombreArticulo);
            seleccionarEnComboBox(jCBUbicacionOrigen, nombreUbicacionOrigen);
            seleccionarEnComboBox(jCBUbicacionDestino, nombreUbicacionDestino);
        }
    }
    
    /** Método auxiliar para seleccionar un ítem por su nombre en un JComboBox */
    private <T> void seleccionarEnComboBox(JComboBox<T> combo, String nombre) {
        if (nombre == null) return;
        DefaultComboBoxModel<T> model = (DefaultComboBoxModel<T>) combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            T item = model.getElementAt(i);
            // Asume que el método toString() del objeto (Articulo/Ubicacion) devuelve el nombre
            if (item != null && item.toString().equals(nombre)) {
                combo.setSelectedItem(item);
                return;
            }
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
        jCBArticuloTraslado = new javax.swing.JComboBox<>();
        jUbicacionOrigen1 = new javax.swing.JLabel();
        jCBUbicacionOrigen = new javax.swing.JComboBox<>();
        jCantidad1 = new javax.swing.JLabel();
        jFTCantidad = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableTraslado = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        bRegistrarTraslado = new javax.swing.JButton();
        bConsultar = new javax.swing.JButton();
        bVerTodo = new javax.swing.JButton();
        bModificar = new javax.swing.JButton();

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
        jLabel1.setText("Traslado de Bienes Mobiliarios");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 0, 790, 80));

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

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
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

        jTableTraslado.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTableTraslado);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 0, -1, 400));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 80, 5));

        bRegistrarTraslado.setBackground(new java.awt.Color(13, 51, 131));
        bRegistrarTraslado.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bRegistrarTraslado.setForeground(new java.awt.Color(255, 255, 255));
        bRegistrarTraslado.setText("Registrar");
        bRegistrarTraslado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRegistrarTrasladoActionPerformed(evt);
            }
        });
        jPanel5.add(bRegistrarTraslado);

        bConsultar.setBackground(new java.awt.Color(13, 51, 131));
        bConsultar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bConsultar.setForeground(new java.awt.Color(255, 255, 255));
        bConsultar.setText("Consultar");
        bConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConsultarActionPerformed(evt);
            }
        });
        jPanel5.add(bConsultar);

        bVerTodo.setBackground(new java.awt.Color(13, 51, 131));
        bVerTodo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bVerTodo.setForeground(new java.awt.Color(255, 255, 255));
        bVerTodo.setText("Ver todo");
        bVerTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bVerTodoActionPerformed(evt);
            }
        });
        jPanel5.add(bVerTodo);

        bModificar.setBackground(new java.awt.Color(13, 51, 131));
        bModificar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bModificar.setForeground(new java.awt.Color(255, 255, 255));
        bModificar.setText("Modificar");
        bModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bModificarActionPerformed(evt);
            }
        });
        jPanel5.add(bModificar);

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 800, 50));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 800, 460));

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
        // Pasa el usuario de vuelta al menú principal
        new PrincipalVista(this.usuarioActual).setVisible(true);
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

    private void bConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bConsultarActionPerformed
        // TODO add your handling code here:
    try {
        DefaultTableModel model = (DefaultTableModel) jTableTraslado.getModel(); // Usar la tabla de Traslado
        model.setRowCount(0);

        // 1. Obtener filtros
        Articulo articulo = (Articulo) jCBArticuloTraslado.getSelectedItem();
        // ⭐ Corregir aquí: Usar el nombre de JComboBox correcto para la ubicación de traslado
        Ubicacion ubicacion = (Ubicacion) jCBUbicacionDestino.getSelectedItem(); 

        // Extraer IDs
        Integer idArticulo = (articulo != null) ? articulo.getIdArticulo() : null;
        Integer idUbicacion = (ubicacion != null) ? ubicacion.getId_ubicacion() : null; // Usa .getIdUbicacion()
        
        // 2. Llamada al controlador para buscar TRASLADOS
        List<Movimiento> lista = movimientoControl.buscarTraslados(idArticulo, idUbicacion);

        if (lista.isEmpty()) {
            String msg = "No se encontraron traslados.\n\nFiltros aplicados:\n";
            msg += "Artículo: " + (articulo != null ? articulo.getNombre() : "TODOS") + "\n";
            msg += "Ubicación de Destino: " + (ubicacion != null ? ubicacion.getNombre() : "TODAS") + "\n\n";
            msg += "¿Estás seguro de que existe un TRASLADO con esta combinación de filtros?";
            
            JOptionPane.showMessageDialog(this, msg, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 3. Llenar la tabla
        for (Movimiento m : lista) {
            // Nota: Los traslados generalmente no tienen costo ni fecha de vencimiento,
            // pero si los tienes en la tabla, el código debe mapearlos.
            model.addRow(new Object[]{
                m.getIdMovimiento(), 
                m.getNombreArticulo(),
                m.getCantidad(),
                m.getNombreUbicacionOrigen() != null ? m.getNombreUbicacionOrigen() : "-", // Origen
                m.getNombreUbicacionDestino() != null ? m.getNombreUbicacionDestino() : "-", // Destino
                (m.getEntregado() != null ? m.getEntregado() : "-"),
                m.getFechaHora()
            });
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al consultar traslados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_bConsultarActionPerformed

    private void bVerTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bVerTodoActionPerformed
        // TODO add your handling code here:
        try {
        // 1. Limpiar la selección de los ComboBoxes (para enviar null al DAO)
        // Usamos los nombres de componentes que identificamos en la consulta:
        jCBArticuloTraslado.setSelectedItem(null); 
        jCBUbicacionDestino.setSelectedItem(null);

        // 2. Llamar al método de consulta con filtros nulos (que es equivalente a listar todo)
        // Se asume que 'movimientoControl' es la instancia correcta del controlador.
        List<Movimiento> lista = movimientoControl.buscarTraslados(null, null);

        // 3. Limpiar la tabla y cargar la lista completa
        DefaultTableModel model = (DefaultTableModel) jTableTraslado.getModel(); // Asegúrate de usar jTableTraslado
        model.setRowCount(0);

        for (Movimiento m : lista) {
            // Lógica para llenar la fila, similar a tu método de consulta de Traslado
            model.addRow(new Object[]{
                m.getIdMovimiento(), 
                m.getNombreArticulo(),
                m.getCantidad(),
                m.getNombreUbicacionOrigen() != null ? m.getNombreUbicacionOrigen() : "-",
                m.getNombreUbicacionDestino() != null ? m.getNombreUbicacionDestino() : "-",
                (m.getEntregado() != null ? m.getEntregado() : "-"),
                m.getFechaHora()
            });
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar todos los traslados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_bVerTodoActionPerformed

    private void bModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bModificarActionPerformed
        // TODO add your handling code here:
        if (this.idMovimientoSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un traslado de la tabla para modificar.");
        return;
    }

    try {
        // 1. Obtener ID del movimiento seleccionado
        int idMovimiento = this.idMovimientoSeleccionado.intValue();
        
        // 2. RECOLECCIÓN Y VALIDACIÓN DE DATOS
        Articulo articulo = (Articulo) jCBArticuloTraslado.getSelectedItem();
        Ubicacion origen = (Ubicacion) jCBUbicacionOrigen.getSelectedItem();
        Ubicacion destino = (Ubicacion) jCBUbicacionDestino.getSelectedItem();
        int cantidad = parseIntSafe(jFTCantidad.getText()); // Campo de Cantidad

        // Validación básica
        if (articulo == null || origen == null || destino == null || cantidad <= 0) {
            JOptionPane.showMessageDialog(this, 
                "Artículo, Ubicación Origen, Ubicación Destino y Cantidad (>0) son obligatorios.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validación Origen != Destino
        if (Objects.equals(origen.getId_ubicacion(), destino.getId_ubicacion())) {
            JOptionPane.showMessageDialog(this, 
                "La ubicación origen y destino no pueden ser la misma para un traslado.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String entregadoA = jFTEntregadoA.getText().trim(); // Campo Entregado A

        // 3. LLAMADA AL CONTROLADOR
        // ⭐ REQUIERE implementar boolean actualizarTraslado(...) en MovimientoControlador ⭐
        boolean ok = movimientoControl.actualizarTraslado(
            idMovimiento,
            articulo.getIdArticulo(),
            cantidad,
            origen.getId_ubicacion(),
            destino.getId_ubicacion(),
            entregadoA
        );

        // 4. MANEJO DE RESPUESTA
        if (ok) {
            JOptionPane.showMessageDialog(this, "Traslado modificado correctamente.");
            // Asumo que tu método para cargar la tabla de traslados se llama cargarTablaTraslados()
            cargarTablaTraslados(); 
            // Limpiar ID de selección
            this.idMovimientoSeleccionado = null; 
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo modificar el traslado.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (CapacidadInsuficienteException e) {
        // Manejo de excepción de capacidad para el DESTINO
        JOptionPane.showMessageDialog(this, 
            String.format("La modificación excede la capacidad de la ubicación destino (%s).\n" +
                          "Restante: %.3f m³ | Requerido: %.3f m³", 
                          e.getNombreUbicacion(), e.getCapacidadRestante(), e.getEspacioRequerido()),
            "Capacidad Insuficiente", JOptionPane.WARNING_MESSAGE);
            
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al intentar modificar el traslado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_bModificarActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bConsultar;
    private javax.swing.JButton bModificar;
    private javax.swing.JButton bRegistrarTraslado;
    private javax.swing.JButton bVerTodo;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableTraslado;
    private javax.swing.JLabel jUbicacionDestino;
    private javax.swing.JLabel jUbicacionOrigen1;
    // End of variables declaration//GEN-END:variables
}
