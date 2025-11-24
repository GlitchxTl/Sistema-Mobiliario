package Vista;

import Controlador.ArticuloControlador;
import Controlador.MovimientoControlador;
import Modelo.Articulo;
import Modelo.Usuario;
import Modelo.Movimiento;
import Modelo.Ubicacion;
import Modelo.UbicacionDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import Modelo.CapacidadInsuficienteException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;

public class PanelEntrada extends javax.swing.JFrame {

    private final ArticuloControlador articuloControl = new ArticuloControlador();
    private final MovimientoControlador movimientoControl = new MovimientoControlador();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();
    private Usuario usuarioActual;
    private Long idMovimientoSeleccionado = null; 

    // Constructor que recibe el usuario
    public PanelEntrada(Usuario usuario) {
        initComponents();
        this.setResizable(false);
        this.usuarioActual = usuario;
        
        // ⭐ AJUSTE: Ocultar bModificar si no es Administrador ⭐
        ocultarBotones(); 
        
        cargarCombos();
        cargarTablaMovimientos();
        configurarEventosCheckboxes();
        bRegistrarEntrada.addActionListener(e -> onRegistrarEntrada());
        configurarEventoTabla();
    }
    
    // Constructor vacío
    public PanelEntrada() {
        this(null);
    }
    
    // -----------------------------------------------------------------------
    // --- LÓGICA DE VISIBILIDAD DEL BOTÓN MODIFICAR ---
    // -----------------------------------------------------------------------
    
    private void ocultarBotones() {
        // Si el usuario no es 'Administrador', el botón de Modificar se oculta.
        // Se asume que existe un componente llamado 'bModificar'.
        if (usuarioActual != null && !"Administrador".equals(usuarioActual.getRol())) {
            bModificar.setVisible(false);
            
        }
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
            jDateVencimiento.setVisible(vencible); 
        });

        jLabelVencimiento.setVisible(false);
        jDateVencimiento.setVisible(false);
    }
    
    // -----------------------------------------------------------------------
    // --- LÓGICA DE CARGA DE COMBOS Y REGISTRO ---
    // -----------------------------------------------------------------------

    /** Carga los artículos y ubicaciones existentes */
    private void cargarCombos() {
        try {
            // Artículos
            DefaultComboBoxModel<Articulo> mArticulos = new DefaultComboBoxModel<>();
            List<Articulo> articulos = articuloControl.obtenerTodosArticulos();
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

    // --- MÉTODO onRegistrarEntrada() (SE MANTIENE IGUAL) ---
    private void onRegistrarEntrada() {

        

        Articulo articulo = (Articulo) jCBArticuloEntrada.getSelectedItem();

        Ubicacion ubicacion = (Ubicacion) jCBUbicacionEntrada.getSelectedItem();

        int cantidad = parseIntSafe(jFTCantidadEntrada.getText());



        try {

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



            Timestamp fechaVencimiento = null;

            if (jCheckBoxVencimiento.isSelected()) {

                Date selectedDate = jDateVencimiento.getDate();

                if (selectedDate == null) {

                    JOptionPane.showMessageDialog(this,

                            "Selecciona una fecha de vencimiento válida.",

                            "Validación de Fecha", JOptionPane.WARNING_MESSAGE);

                    return;

                }

                

                fechaVencimiento = new Timestamp(selectedDate.getTime());

                

                // ⭐ INICIO DE LA SOLUCIÓN 3: Comparar solo por DÍA ⭐

                

                // 1. Poner la hora del vencimiento seleccionado a 00:00:00 (solo para la comparación)

                java.util.Calendar calVencimiento = java.util.Calendar.getInstance();

                calVencimiento.setTime(fechaVencimiento);

                calVencimiento.set(java.util.Calendar.HOUR_OF_DAY, 0);

                calVencimiento.set(java.util.Calendar.MINUTE, 0);

                calVencimiento.set(java.util.Calendar.SECOND, 0);

                calVencimiento.set(java.util.Calendar.MILLISECOND, 0);



                // 2. Obtener la hora actual y ponerla a 00:00:00 (para representar "Hoy")

                java.util.Calendar calHoy = java.util.Calendar.getInstance();

                calHoy.set(java.util.Calendar.HOUR_OF_DAY, 0);

                calHoy.set(java.util.Calendar.MINUTE, 0);

                calHoy.set(java.util.Calendar.SECOND, 0);

                calHoy.set(java.util.Calendar.MILLISECOND, 0);

                

                // 3. Comparar si el día de vencimiento es ESTRICTAMENTE anterior al día de hoy.

                // Usamos el Calendar modificado para la comparación.

                if (calVencimiento.before(calHoy)) { 

                    JOptionPane.showMessageDialog(this, 

                        "La fecha de vencimiento no puede ser anterior al día de hoy.\n" +

                        "El producto ya está vencido y no puede registrarse.", 

                        "Producto Vencido", JOptionPane.ERROR_MESSAGE);

                    return; 

                }
            }

            Double costo = null;
            if (!jCheckBoxDonado.isSelected()) {
                double costoVal = parseDoubleSafe(jTFCosto.getText());
                costo = costoVal > 0 ? costoVal : null;
            }

            boolean ok = movimientoControl.registrarEntrada(
                articulo.getIdArticulo(),
                cantidad,
                ubicacion.getId_ubicacion(),
                jFTEntregadoA.getText().trim(),
                jCheckBoxDonado.isSelected(),
                costo,
                fechaVencimiento
            );

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Entrada registrada correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                jFTCantidadEntrada.setText("");
                jFTEntregadoA.setText("");
                jTFCosto.setText("");
                jDateVencimiento.setDate(null);
                cargarTablaMovimientos(); 
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error desconocido al registrar la entrada.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (CapacidadInsuficienteException e) {
            
            String mensajeError = String.format(
                "La ubicación '%s' no tiene suficiente capacidad.\n" +
                "Capacidad restante: %.3f m³\n" +
                "Espacio requerido:  %.3f m³\n\n",
                e.getNombreUbicacion(),
                e.getCapacidadRestante(),
                e.getEspacioRequerido()
            );

            List<Ubicacion> sugerencias = e.getSugerencias();
            if (sugerencias.isEmpty()) {
                mensajeError += "No se encontraron otras ubicaciones con espacio suficiente.";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Sugerencias (con espacio disponible):\n");
                for (Ubicacion uSugerida : sugerencias) {
                    sb.append(String.format("- %s (Restante: %.3f m³)\n",
                        uSugerida.getNombre(),
                        uSugerida.getCapacidadRestante()
                    ));
                }
                mensajeError += sb.toString();
            }

            JOptionPane.showMessageDialog(this,
                    mensajeError,
                    "Capacidad Insuficiente",
                    JOptionPane.WARNING_MESSAGE);

        } catch (SQLException sqe) {
            JOptionPane.showMessageDialog(this,
                    "Error SQL: " + sqe.getMessage(),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // -----------------------------------------------------------------------
    // --- LÓGICA DE TABLA (CARGAR Y EVENTO) ---
    // -----------------------------------------------------------------------

    /** Carga los últimos movimientos de tipo ENTRADA en la tabla */
    private void cargarTablaMovimientos() {
        try {
            
            String[] columnas = {"ID", "Artículo", "Cantidad", "Ubicación", "Costo", "Vencimiento", "Entregado Por", "Fecha/Hora"};
            
            // Creamos un DefaultTableModel que puede manejar la inserción de Long y Timestamp
            DefaultTableModel nuevoModel = new DefaultTableModel(columnas, 0) {
                
                // ⭐ IMPORTANTE: Sobreescribimos getColumnClass para que reconozca los tipos de datos
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) return Long.class;      // ID
                    if (columnIndex == 2) return Integer.class;   // Cantidad
                    if (columnIndex == 5) return Timestamp.class; // Vencimiento (requiere Timestamp o null)
                    return Object.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            jTable1.setModel(nuevoModel);
            // Ocultar la columna del ID (Columna 0)
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(0).setWidth(0);

            var lista = movimientoControl.obtenerEntradas();

            for (Movimiento m : lista) {
                // ⭐ CORRECCIÓN: Si la fecha de vencimiento es null, insertamos null
                // Esto previene que el DateRenderer intente formatear el String "-" como fecha.
                Object fechaVencimientoParaTabla = m.getFechaVencimiento() != null ? m.getFechaVencimiento() : null;
                
                nuevoModel.addRow(new Object[]{
                    m.getIdMovimiento(), 
                    m.getNombreArticulo(),
                    m.getCantidad(),
                    m.getNombreUbicacionDestino() != null ? m.getNombreUbicacionDestino() : "-",
                    m.getCosto() != null ? String.format("%.2f", m.getCosto()) : "-",
                    fechaVencimientoParaTabla, // ⬅️ Ahora es Timestamp o null
                    (m.getEntregado() != null ? m.getEntregado() : "-"),
                    m.getFechaHora()
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void configurarEventoTabla() {
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    cargarFormularioDesdeTablas();
                }
            }
        });
    }

    /** Carga los datos de la fila seleccionada de la tabla al formulario. */
    private void cargarFormularioDesdeTablas() {
        int fila = jTable1.getSelectedRow();
        if (fila >= 0) {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

            // 1. Obtener el ID del movimiento (Columna 0)
            // ⭐ CORRECCIÓN DE CLASSCASTEXCEPTION: Manejar Integer/Long de forma segura
            Object idObj = model.getValueAt(fila, 0);
            Long idMov = null;
            if (idObj instanceof Integer) {
                idMov = ((Integer) idObj).longValue();
            } else if (idObj instanceof Long) {
                idMov = (Long) idObj;
            }
            this.idMovimientoSeleccionado = idMov;
            
            // 2. Obtener los nombres de Artículo y Ubicación
            String nombreArticulo = (String) model.getValueAt(fila, 1);
            String nombreUbicacion = (String) model.getValueAt(fila, 3);
            
            // 3. Cantidad y Entregado
            jFTCantidadEntrada.setText(model.getValueAt(fila, 2).toString());
            jFTEntregadoA.setText((String) model.getValueAt(fila, 6));

            // 4. Costo y Donado
            // El costo en la tabla ahora es un String ("XX.XX" o "-")
            String costoStr = (String) model.getValueAt(fila, 4); 
            boolean esDonado = costoStr.equals("-");
            jCheckBoxDonado.setSelected(esDonado);
            jTFCosto.setText(esDonado ? "" : costoStr);
            jTFCosto.setVisible(!esDonado);
            jLabelCosto.setVisible(!esDonado);

            // 5. Vencimiento
            // ⭐ CORRECCIÓN: La tabla ahora contiene Timestamp o null
            Object vencimientoObj = model.getValueAt(fila, 5); 
            boolean tieneVencimiento = (vencimientoObj instanceof Timestamp);
            
            jCheckBoxVencimiento.setSelected(tieneVencimiento);
            jDateVencimiento.setVisible(tieneVencimiento);
            jLabelVencimiento.setVisible(tieneVencimiento);
            
            if (tieneVencimiento) {
                jDateVencimiento.setDate(new Date(((Timestamp) vencimientoObj).getTime()));
            } else {
                jDateVencimiento.setDate(null);
            }

            // 6. Seleccionar en Combobox
            seleccionarEnComboBox(jCBArticuloEntrada, nombreArticulo);
            seleccionarEnComboBox(jCBUbicacionEntrada, nombreUbicacion);
        }
    }
    
    /** Método auxiliar para seleccionar un ítem por su nombre en un JComboBox */
    private <T> void seleccionarEnComboBox(JComboBox<T> combo, String nombre) {
        if (nombre == null) return;
        DefaultComboBoxModel<T> model = (DefaultComboBoxModel<T>) combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            T item = model.getElementAt(i);
            // Esto asume que el método toString() del objeto Articulo/Ubicacion devuelve el nombre
            if (item != null && item.toString().equals(nombre)) {
                combo.setSelectedItem(item);
                return;
            }
        }
    }

    // --- MÉTODOS AUXILIARES ---
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
        jLabelTitulo = new javax.swing.JLabel();
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
        jCBArticuloEntrada = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jFTEntregadoA = new javax.swing.JFormattedTextField();
        jCheckBoxVencimiento = new javax.swing.JCheckBox();
        jCheckBoxDonado = new javax.swing.JCheckBox();
        jLabelVencimiento = new javax.swing.JLabel();
        jLabelCosto = new javax.swing.JLabel();
        jTFCosto = new javax.swing.JTextField();
        jDateVencimiento = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        bRegistrarEntrada = new javax.swing.JButton();
        bConsultar = new javax.swing.JButton();
        bModificar = new javax.swing.JButton();
        bVerTodo = new javax.swing.JButton();

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

        jLabelTitulo.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        jLabelTitulo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitulo.setText("Entrada de Bienes Mobiliarios");
        jPanel2.add(jLabelTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 0, 792, 60));

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

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
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

        jLabelVencimiento.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabelVencimiento.setText("Fecha de Vencimiento:");
        jPanel4.add(jLabelVencimiento, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 400, 170, 30));

        jLabelCosto.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabelCosto.setText("Costo (Bs.):");
        jPanel4.add(jLabelCosto, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 170, 30));
        jPanel4.add(jTFCosto, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 310, 160, 30));
        jPanel4.add(jDateVencimiento, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 400, 150, 30));

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

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 0, -1, 430));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 70, 5));

        bRegistrarEntrada.setBackground(new java.awt.Color(13, 51, 131));
        bRegistrarEntrada.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bRegistrarEntrada.setForeground(new java.awt.Color(255, 255, 255));
        bRegistrarEntrada.setText("Registrar");
        bRegistrarEntrada.setMaximumSize(new java.awt.Dimension(120, 32));
        bRegistrarEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRegistrarEntradaActionPerformed(evt);
            }
        });
        jPanel5.add(bRegistrarEntrada);

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

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 430, 800, 50));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 800, 480));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 802, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void bRegistrarEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRegistrarEntradaActionPerformed
        
    }//GEN-LAST:event_bRegistrarEntradaActionPerformed

    private void jCheckBoxDonadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxDonadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxDonadoActionPerformed

    private void jFTEntregadoAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTEntregadoAActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTEntregadoAActionPerformed

    private void jCBArticuloEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBArticuloEntradaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBArticuloEntradaActionPerformed

    private void jFTCantidadEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTCantidadEntradaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTCantidadEntradaActionPerformed

    private void jCBUbicacionEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBUbicacionEntradaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBUbicacionEntradaActionPerformed

    private void bConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bConsultarActionPerformed
        // TODO add your handling code here:
        
    try {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        // 1. Obtener filtros
        Articulo articulo = (Articulo) jCBArticuloEntrada.getSelectedItem();
        Ubicacion ubicacion = (Ubicacion) jCBUbicacionEntrada.getSelectedItem();

        // Extraer IDs
        Integer idArticulo = (articulo != null) ? articulo.getIdArticulo() : null;
        Integer idUbicacion = (ubicacion != null) ? ubicacion.getId_ubicacion() : null;
        
        // ⭐ IMPORTANTE: Si quieres ver TODO, los combos deben estar sin selección.
        // Si siempre hay algo seleccionado, SIEMPRE filtrará por esa combinación exacta.
        
        // 2. Llamada al controlador (Esto imprimirá el DEBUG en consola gracias al DAO modificado)
        List<Movimiento> lista = movimientoControl.buscarEntradas(idArticulo, idUbicacion);

        if (lista.isEmpty()) {
            // Mostramos mensaje detallado para ayudar a entender por qué no hay datos
            String msg = "No se encontraron resultados.\n\nFiltros aplicados:\n";
            msg += "Artículo: " + (articulo != null ? articulo.getNombre() : "TODOS") + "\n";
            msg += "Ubicación: " + (ubicacion != null ? ubicacion.getNombre() : "TODAS") + "\n\n";
            msg += "¿Estás seguro de que existe una ENTRADA exacta para esta combinación?";
            
            JOptionPane.showMessageDialog(this, msg, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Movimiento m : lista) {
            Object fechaVencimientoParaTabla = m.getFechaVencimiento() != null ? m.getFechaVencimiento() : null;
            
            model.addRow(new Object[]{
                m.getIdMovimiento(), 
                m.getNombreArticulo(),
                m.getCantidad(),
                m.getNombreUbicacionDestino() != null ? m.getNombreUbicacionDestino() : "-",
                m.getCosto() != null ? String.format("%.2f", m.getCosto()) : "-",
                fechaVencimientoParaTabla, 
                (m.getEntregado() != null ? m.getEntregado() : "-"),
                m.getFechaHora()
            });
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al consultar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_bConsultarActionPerformed

    private void bModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bModificarActionPerformed
        // TODO add your handling code here:
        int fila = jTable1.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(this, "Seleccione un movimiento de entrada para modificar.");
        return;
    }

    try {
        // 1. Obtener ID del movimiento seleccionado
        int idMovimiento = Integer.parseInt(jTable1.getModel().getValueAt(fila, 0).toString());
        
        // 2. RECOLECCIÓN Y VALIDACIÓN DE DATOS
        Articulo articulo = (Articulo) jCBArticuloEntrada.getSelectedItem();
        Ubicacion ubicacion = (Ubicacion) jCBUbicacionEntrada.getSelectedItem();
        int cantidad = parseIntSafe(jFTCantidadEntrada.getText());

        if (articulo == null || ubicacion == null || cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "Artículo, Ubicación y Cantidad son obligatorios y deben ser válidos.",
                                          "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Timestamp fechaVencimiento = null;
        if (jCheckBoxVencimiento.isSelected()) {
            Date selectedDate = jDateVencimiento.getDate();
            if (selectedDate == null) {
                 JOptionPane.showMessageDialog(this, "Selecciona una fecha de vencimiento válida.",
                                              "Validación de Fecha", JOptionPane.WARNING_MESSAGE);
                 return;
            }
            fechaVencimiento = new Timestamp(selectedDate.getTime());
        }

        Double costo = null;
        if (!jCheckBoxDonado.isSelected()) {
            double costoVal = parseDoubleSafe(jTFCosto.getText());
            costo = costoVal > 0 ? costoVal : null;
        }
        
        // 3. LLAMADA AL CONTROLADOR
        // ⭐ REQUIERE implementar boolean actualizarEntrada(...) en MovimientoControlador ⭐
        boolean ok = movimientoControl.actualizarEntrada(
            idMovimiento,
            articulo.getIdArticulo(),
            cantidad,
            ubicacion.getId_ubicacion(),
            jFTEntregadoA.getText().trim(),
            jCheckBoxDonado.isSelected(),
            costo,
            fechaVencimiento
        );

        // 4. MANEJO DE RESPUESTA
        if (ok) {
            JOptionPane.showMessageDialog(this, "Entrada modificada correctamente.");
            cargarTablaMovimientos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo modificar la entrada.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (CapacidadInsuficienteException e) {
        // Manejo de excepción de capacidad similar al método registrar
        JOptionPane.showMessageDialog(this, 
            String.format("La modificación excede la capacidad de la ubicación.\n" +
                          "Restante: %.3f m³ | Requerido: %.3f m³", 
                          e.getCapacidadRestante(), e.getEspacioRequerido()),
            "Capacidad Insuficiente", JOptionPane.WARNING_MESSAGE);
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al intentar modificar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_bModificarActionPerformed

    private void bModificar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bModificar1ActionPerformed
    
    }//GEN-LAST:event_bModificar1ActionPerformed

    private void bVerTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bVerTodoActionPerformed
        // TODO add your handling code here:
    try {
        // 1. Limpiar la selección de los ComboBoxes (para enviar null al DAO)
        jCBArticuloEntrada.setSelectedItem(null); 
        jCBUbicacionEntrada.setSelectedItem(null);

        // 2. Llamar al método de consulta con filtros nulos (que es equivalente a listar todo)
        List<Movimiento> lista = movimientoControl.buscarEntradas(null, null);

        // 3. Limpiar la tabla y cargar la lista completa
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        for (Movimiento m : lista) {
            
            Object fechaVencimientoParaTabla = m.getFechaVencimiento() != null ? m.getFechaVencimiento() : null;
            
            model.addRow(new Object[]{
                m.getIdMovimiento(), 
                m.getNombreArticulo(),
                m.getCantidad(),
                m.getNombreUbicacionDestino() != null ? m.getNombreUbicacionDestino() : "-",
                m.getCosto() != null ? String.format("%.2f", m.getCosto()) : "-",
                fechaVencimientoParaTabla, 
                (m.getEntregado() != null ? m.getEntregado() : "-"),
                m.getFechaHora()
            });
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar todos los movimientos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }    
    }//GEN-LAST:event_bVerTodoActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bConsultar;
    private javax.swing.JButton bModificar;
    private javax.swing.JButton bRegistrarEntrada;
    private javax.swing.JButton bVerTodo;
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
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelCosto;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JLabel jLabelVencimiento;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTFCosto;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
