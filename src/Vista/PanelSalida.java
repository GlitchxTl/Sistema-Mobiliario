package Vista;



import Controlador.ArticuloControlador;

import Controlador.MovimientoControlador;

import Modelo.Usuario;

import Modelo.Articulo;

import Modelo.Movimiento;

import Modelo.Ubicacion;

import Modelo.UbicacionDAO;

import java.sql.SQLException;

import java.util.List;

import javax.swing.*;

import javax.swing.table.DefaultTableModel;

import java.awt.event.MouseAdapter;

import java.awt.event.MouseEvent;



public class PanelSalida extends javax.swing.JFrame {



    private final ArticuloControlador articuloControl = new ArticuloControlador();

    private final MovimientoControlador movimientoControl = new MovimientoControlador();

    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();

    private Usuario usuarioActual;

    private Long idMovimientoSeleccionado = null; // Para seleccionar una fila para Modificar/Eliminar



    // ⭐ CAMBIO 2: Nuevo constructor para recibir el usuario ⭐

    public PanelSalida(Usuario usuario) {

        initComponents();

        this.usuarioActual = usuario; // Guarda el usuario

        

        

        

        cargarCombos();

        cargarTablaSalidas();  

        cargarTablaEntradas();

        

        configurarEventoTablaSalida(); // ⭐ NUEVO: Configura clic en tabla de Salidas

        configurarEventoTablaEntrada();

        bRegistrarSalida.addActionListener(e -> onRegistrarSalida());

    }

    

    // (Dejas el constructor vacío si el diseñador lo requiere)

    public PanelSalida() {

        this(null);

    }



    // -----------------------------------------------------------------------

    // --- LÓGICA DE VISIBILIDAD Y AUXILIARES ---

    // -----------------------------------------------------------------------

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

    // --- LÓGICA DE TABLAS ---

    // -------------------------------------------------------------------------

    

    /** Carga los movimientos de tipo SALIDA en la tabla jTablaSalida */

    private void cargarTablaSalidas() {

        try {

            // Columnas necesarias para el modelo: ID, Artículo, Cantidad, Origen, Motivo, Entregado, Fecha/Hora

            String[] columnas = {"ID", "Artículo", "Cantidad", "Ubicación", "Motivo", "Entregado Por", "Fecha/Hora"};

            

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

            

            jTablaSalida.setModel(nuevoModel);

            // Ocultar la columna del ID (Columna 0)

            jTablaSalida.getColumnModel().getColumn(0).setMinWidth(0);

            jTablaSalida.getColumnModel().getColumn(0).setMaxWidth(0);

            jTablaSalida.getColumnModel().getColumn(0).setWidth(0);



            List<Movimiento> listaSalidas = movimientoControl.obtenerSalidas();



            for (Movimiento m : listaSalidas) {

                nuevoModel.addRow(new Object[]{

                    m.getIdMovimiento(), // ID en la columna 0

                    m.getNombreArticulo(),

                    m.getCantidad(),

                    m.getNombreUbicacionOrigen() != null ? m.getNombreUbicacionOrigen() : "-",

                    m.getMotivo() != null ? m.getMotivo() : "-",

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

    

    /** Carga los movimientos de tipo ENTRADA en la tabla jTableEntrada */

private void cargarTablaEntradas() {

    try {

        // ⭐ 1. Definir columnas, incluyendo ID en la posición 0 ⭐

        String[] columnas = {"ID", "Artículo", "Cantidad", "Ubicación Destino", "Costo", "Vencimiento", "Entregado Por", "Fecha/Hora"};



        DefaultTableModel nuevoModel = new DefaultTableModel(columnas, 0) {

            @Override

            public Class<?> getColumnClass(int columnIndex) {

                // ⭐ Tipo de la columna 0 DEBE ser Long ⭐

                if (columnIndex == 0) return Long.class;

                if (columnIndex == 2) return Integer.class;

                return Object.class;

            }

            @Override

            public boolean isCellEditable(int row, int column) {

                return false;

            }

        };

        

        jTableEntrada_Inventario.setModel(nuevoModel); 

        

        // ⭐ 2. Ocultar la columna del ID (Columna 0) ⭐

        jTableEntrada_Inventario.getColumnModel().getColumn(0).setMinWidth(0);

        jTableEntrada_Inventario.getColumnModel().getColumn(0).setMaxWidth(0);

        jTableEntrada_Inventario.getColumnModel().getColumn(0).setWidth(0);



        List<Movimiento> listaEntradas = movimientoControl.obtenerEntradas();



        for (Movimiento m : listaEntradas) {

            nuevoModel.addRow(new Object[]{

                // ⭐ El primer elemento de la fila DEBE ser el ID ⭐

                    m.getIdMovimiento(), // Columna 0

                    m.getNombreArticulo(), // Columna 1

                    m.getCantidad(),

                    // En una entrada, la ubicación es el Destino

                    m.getNombreUbicacionDestino() != null ? m.getNombreUbicacionDestino() : "-",

                    // Formateamos Costo como String para asegurar compatibilidad si no usamos Double.class

                    m.getCosto() != null ? String.format("%.2f", m.getCosto()) : "-",

                    (m.getFechaVencimiento() != null ? m.getFechaVencimiento() : "-"),

                    (m.getEntregado() != null ? m.getEntregado() : "-"),

                    m.getFechaHora()

                });

            }



        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error al cargar la tabla de entradas: " + e.getMessage(),

                                         "Error", JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();

        }

}

    // -------------------------------------------------------------------------

    // --- LÓGICA DE SELECCIÓN DE TABLA ---

    // -------------------------------------------------------------------------



    /** Configura el evento de clic en la tabla de Salidas para cargar el formulario. */

    private void configurarEventoTablaSalida() {

        jTablaSalida.addMouseListener(new MouseAdapter() {

            @Override

            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 1) {

                    cargarFormularioDesdeTablaSalida();

                }

            }

        });

    }

    private void configurarEventoTablaEntrada() {

        jTableEntrada_Inventario.addMouseListener(new MouseAdapter() {

            @Override

            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 1) {

                    cargarFormularioDesdeTablaEntrada();

                }

            }

        });

    }



    /** ⭐ NUEVO MÉTODO: Carga los datos de la fila seleccionada de la tabla de Salidas al formulario. ⭐ */

    private void cargarFormularioDesdeTablaSalida() {

        int fila = jTablaSalida.getSelectedRow();

        if (fila >= 0) {

            DefaultTableModel model = (DefaultTableModel) jTablaSalida.getModel();



            // 1. Obtener el ID del movimiento (Columna 0)

            Object idObj = model.getValueAt(fila, 0);

            Long idMov = (idObj instanceof Integer) ? ((Integer) idObj).longValue() : (Long) idObj;

            this.idMovimientoSeleccionado = idMov;

            

            // 2. Obtener valores

            String nombreArticulo = (String) model.getValueAt(fila, 1);

            String nombreUbicacionOrigen = (String) model.getValueAt(fila, 3);

            String motivo = (String) model.getValueAt(fila, 4);

            

            // 3. Cargar campos de Salida

            jFTCantidadSalida.setText(model.getValueAt(fila, 2).toString());

            jFTMotivo.setText(motivo.equals("-") ? "" : motivo);

            

            // ⭐ AÑADIDO: Limpiar campos de Entrada (si existen en el formulario)

            // Esto asegura que al cambiar de Entrada a Salida, los campos se vacíen.

            limpiarCamposEntradaAsumidos(); 



            // 4. Seleccionar en Comboboxes

            seleccionarEnComboBox(jCBArticuloSalida, nombreArticulo);

            seleccionarEnComboBox(jCBUbicacionOrigen, nombreUbicacionOrigen);

        }

    }

    

    /** ⭐ MODIFICADO: Carga SÓLO los datos necesarios de la tabla de Entradas al formulario. ⭐ */

    private void cargarFormularioDesdeTablaEntrada() {

        int fila = jTableEntrada_Inventario.getSelectedRow();

        if (fila >= 0) {

            DefaultTableModel model = (DefaultTableModel) jTableEntrada_Inventario.getModel();



            // 1. Obtener el ID del movimiento (Columna 0) de forma robusta

            Object idObj = model.getValueAt(fila, 0);

            Long idMov = null;

            

            // ⭐ CORRECCIÓN DEL CASTING ⭐

            if (idObj instanceof Long) {

                idMov = (Long) idObj;

            } else if (idObj instanceof Integer) {

                idMov = ((Integer) idObj).longValue();

            } else if (idObj instanceof String) {

                // Si accidentalmente se leyó como String, intenta parsearlo.

                try {

                     idMov = Long.parseLong((String)idObj);

                } catch (NumberFormatException nfe) {

                     JOptionPane.showMessageDialog(this, "Error de datos: ID de movimiento no es un número válido.",

                            "Error de Conversión", JOptionPane.ERROR_MESSAGE);

                     return; 

                }

            } else {

                // Si el ID es null o inesperado, cancelamos la carga.

                return;

            }

            

            this.idMovimientoSeleccionado = idMov;

            

            // 2. Obtener valores necesarios (Índices de la tabla de Entrada)

            String nombreArticulo = (String) model.getValueAt(fila, 1);       // Columna 1

            String nombreUbicacionDestino = (String) model.getValueAt(fila, 3); // Columna 3

            

            // 3. Cargar campos comunes

            // La cantidad siempre debe ser un String para jFormattedTextField

            jFTCantidadSalida.setText(model.getValueAt(fila, 2).toString());  // Cantidad (Columna 2)

            

            // 4. Limpiar campos de la Salida y campos específicos de la Entrada (para evitar confusiones)

            jFTMotivo.setText(""); // El campo Motivo es específico de Salida, se limpia.

            limpiarCamposEntradaAsumidos(); // Limpia Costo, Vencimiento y Entregado Por



            // 5. Seleccionar en Comboboxes

            seleccionarEnComboBox(jCBArticuloSalida, nombreArticulo);

            // La Ubicación Destino de la entrada se carga en el combo de Ubicación Origen del formulario.

            seleccionarEnComboBox(jCBUbicacionOrigen, nombreUbicacionDestino);

        }

    }

    

    /** ⭐ NUEVO MÉTODO AUXILIAR: Para limpiar campos específicos de Entrada. ⭐ */

    private void limpiarCamposEntradaAsumidos() {

        // Asegúrate de que los nombres de los campos coincidan con tu diseño

        // if (jFTCosto != null) jFTCosto.setText("");

        // if (jFTFechaVencimiento != null) jFTFechaVencimiento.setText("");

        // if (jFTEntregadoPor != null) jFTEntregadoPor.setText("");

        

        // ⭐ Si no tienes estos campos, deja este método vacío. ⭐

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
        jCBUbicacionOrigen = new javax.swing.JComboBox<>();
        jFTCantidadSalida = new javax.swing.JFormattedTextField();
        jCBArticuloSalida = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        bRegistrarSalida = new javax.swing.JButton();
        bConsultarSalida = new javax.swing.JButton();
        bVerTodo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablaSalida = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableEntrada_Inventario = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

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
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 0, 800, 60));

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

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 60));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
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

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 50, 5));

        bRegistrarSalida.setBackground(new java.awt.Color(13, 51, 131));
        bRegistrarSalida.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bRegistrarSalida.setForeground(new java.awt.Color(255, 255, 255));
        bRegistrarSalida.setText("Confirmar");
        bRegistrarSalida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRegistrarSalidaActionPerformed(evt);
            }
        });
        jPanel7.add(bRegistrarSalida);

        bConsultarSalida.setBackground(new java.awt.Color(13, 51, 131));
        bConsultarSalida.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bConsultarSalida.setForeground(new java.awt.Color(255, 255, 255));
        bConsultarSalida.setText("Consultar");
        bConsultarSalida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConsultarSalidaActionPerformed(evt);
            }
        });
        jPanel7.add(bConsultarSalida);

        bVerTodo.setBackground(new java.awt.Color(13, 51, 131));
        bVerTodo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bVerTodo.setForeground(new java.awt.Color(255, 255, 255));
        bVerTodo.setText("Ver Todo");
        bVerTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bVerTodoActionPerformed(evt);
            }
        });
        jPanel7.add(bVerTodo);

        jPanel4.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 370, 350, 110));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 350, 480));

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

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 100, 450, 200));

        jTableEntrada_Inventario.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTableEntrada_Inventario);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 340, 450, 200));

        jPanel5.setBackground(new java.awt.Color(13, 51, 131));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Tabla de Salidas:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, 450, 40));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Tabla de Salidas:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, 450, 40));

        jPanel6.setBackground(new java.awt.Color(13, 51, 131));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Tabla de Articulos en el Inventario:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 300, 450, 40));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Tabla de Salidas:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 310, 450, 30));

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
        // Pasa el usuario de vuelta al menú principal
        new PrincipalVista(this.usuarioActual).setVisible(true);
    }//GEN-LAST:event_bVolverActionPerformed

    private void bRegistrarSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRegistrarSalidaActionPerformed

    }//GEN-LAST:event_bRegistrarSalidaActionPerformed

    private void jFTCantidadSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFTCantidadSalidaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFTCantidadSalidaActionPerformed

    private void bVerTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bVerTodoActionPerformed
        // TODO add your handling code here:
        try {
        // 1. Limpiar la selección de los ComboBoxes (para enviar null al DAO)
        // Usamos los nombres de componentes que identificamos en la consulta:
        jCBArticuloSalida.setSelectedItem(null); 
        jCBUbicacionOrigen.setSelectedItem(null);

        // 2. Llamar al método de consulta con filtros nulos (que es equivalente a listar todo)
        // Se asume que 'movimientoControl' es la instancia correcta del controlador.
        List<Movimiento> lista = movimientoControl.buscarSalidas(null, null);

        // 3. Limpiar la tabla y cargar la lista completa
        DefaultTableModel model = (DefaultTableModel) jTablaSalida.getModel(); // Asegúrate de usar jTablaSalida
        model.setRowCount(0);

        for (Movimiento m : lista) {
            // Lógica para llenar la fila, similar a tu método de consulta de Salida
            model.addRow(new Object[]{
                m.getIdMovimiento(), 
                m.getNombreArticulo(),
                m.getCantidad(),
                m.getNombreUbicacionOrigen() != null ? m.getNombreUbicacionOrigen() : "-", // Origen (de donde salió)
                (m.getMotivo() != null ? m.getMotivo() : "-"), // Motivo de la salida
                m.getFechaHora()
            });
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar todas las salidas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_bVerTodoActionPerformed

    private void bConsultarSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bConsultarSalidaActionPerformed
        // TODO add your handling code here:
        try {
            DefaultTableModel model = (DefaultTableModel) jTablaSalida.getModel();
            model.setRowCount(0);

            // 1. Obtener filtros y IDs
            Articulo articulo = (Articulo) jCBArticuloSalida.getSelectedItem();
            Ubicacion ubicacion = (Ubicacion) jCBUbicacionOrigen.getSelectedItem();

            // Los filtros deben incluir null si el combo no tiene un ítem seleccionado ("TODOS")
            Integer idArticulo = (articulo != null) ? articulo.getIdArticulo() : null;
            Integer idUbicacion = (ubicacion != null) ? ubicacion.getId_ubicacion() : null;

            // 2. Llamada al controlador para buscar SALIDAS
            // Se asume que MovimientoControlador tiene un método 'buscarSalidas' similar a 'buscarTraslados'.
            List<Movimiento> lista = movimientoControl.buscarSalidas(idArticulo, idUbicacion);

            // 3. Manejo de resultados
            if (lista.isEmpty()) {
                String msg = "No se encontraron salidas con los filtros aplicados:\n";
                msg += "Artículo: " + (articulo != null ? articulo.getNombre() : "TODOS") + "\n";
                msg += "Ubicación Origen: " + (ubicacion != null ? ubicacion.getNombre() : "TODAS");
                JOptionPane.showMessageDialog(this, msg, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 4. Llenar la tabla jTablaSalida
            for (Movimiento m : lista) {
                model.addRow(new Object[]{
                    m.getIdMovimiento(), // Columna 0
                    m.getNombreArticulo(),
                    m.getCantidad(),
                    m.getNombreUbicacionOrigen() != null ? m.getNombreUbicacionOrigen() : "-", // Ubicación (Origen)
                    m.getMotivo() != null ? m.getMotivo() : "-",
                    m.getEntregado() != null ? m.getEntregado() : "-",
                    m.getFechaHora()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar salidas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bConsultarSalidaActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bConsultarSalida;
    private javax.swing.JButton bRegistrarSalida;
    private javax.swing.JButton bVerTodo;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTablaSalida;
    private javax.swing.JTable jTableEntrada_Inventario;
    // End of variables declaration//GEN-END:variables
}
