package Vista;

import Controlador.ArticuloControlador;
import Controlador.MovimientoControlador;
import Modelo.Articulo;
import Modelo.CapacidadInsuficienteException;
import Modelo.Movimiento;
import Modelo.Ubicacion;
import Modelo.UbicacionDAO;
import Modelo.Usuario;
import util.GestorBcv;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.toedter.calendar.JDateChooser; // Asegúrate de tener la librería JCalendar

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PanelEntrada extends JFrame {

    // --- Componentes de la UI ---
    private JComboBox<Articulo> jCBArticuloEntrada;
    private JFormattedTextField jFTCantidadEntrada;
    private JComboBox<Ubicacion> jCBUbicacionEntrada;
    private JFormattedTextField jFTEntregadoA;
    private JCheckBox jCheckBoxDonado;
    private JLabel jLabelCostoDivisa;
    private JTextField jTFCostoDivisa;
    private JCheckBox jCheckBoxVencimiento;
    private JLabel jLabelVencimiento;
    private JDateChooser jDateVencimiento;
    private JTable jTable1;
    private DefaultTableModel modeloTabla;

    // Botones
    private JButton bRegistrarEntrada, bConsultar, bModificar, bVerTodo, bExportar, bVolver;

    // --- Variables de Control ---
    private final ArticuloControlador articuloControl = new ArticuloControlador();
    private final MovimientoControlador movimientoControl = new MovimientoControlador();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();
    private Usuario usuarioActual;
    private Long idMovimientoSeleccionado = null;
    
    // Constantes de Diseño
    private final Color COLOR_AZUL = new Color(13, 51, 131);
    private final Font FONT_TITLE = new Font("Segoe UI Black", Font.BOLD, 18);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);

    // Constructor que recibe el usuario
    public PanelEntrada(Usuario usuario) {
        this.usuarioActual = usuario;
        
        // Configuración de la Ventana
        setTitle("Entrada de Bienes Mobiliarios");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        
        // Inicializar UI y Lógica
        initUI();
        cargarCombos();
        cargarTablaMovimientos();
        configurarLogica(); // Listeners y visibilidad inicial
        
        // Permisos
        if (usuarioActual != null && !"Administrador".equals(usuarioActual.getRol())) {
            bModificar.setVisible(false);
        }
    }

    // Constructor vacío
    public PanelEntrada() {
        this(null);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // ==========================================
        // 1. HEADER (NORTE)
        // ==========================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_AZUL);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel lblTitulo = new JLabel("Entrada de Bienes Mobiliarios", SwingConstants.CENTER);
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(Color.WHITE);

        bVolver = crearBotonHeader("Volver");

        headerPanel.add(lblTitulo, BorderLayout.CENTER);
        headerPanel.add(bVolver, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. CONTENIDO CENTRAL (CENTRO)
        // ==========================================
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Panel Formulario (Izquierda) ---
        JPanel formPanel = crearPanelFormulario();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.35; // 35% del ancho
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 10);
        centerPanel.add(formPanel, gbc);

        // --- Panel Tabla (Derecha) ---
        JPanel tablePanel = crearPanelTabla();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.65; // 65% del ancho
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 10, 20);
        centerPanel.add(tablePanel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. FOOTER BOTONES (SUR)
        // ==========================================
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        footerPanel.setBackground(Color.WHITE);

        bRegistrarEntrada = crearBotonAccion("Registrar");
        bConsultar = crearBotonAccion("Consultar");
        bModificar = crearBotonAccion("Modificar");
        bVerTodo = crearBotonAccion("Ver Todo");
        bExportar = crearBotonAccion("Exportar");

        footerPanel.add(bRegistrarEntrada);
        footerPanel.add(bConsultar);
        footerPanel.add(bModificar);
        footerPanel.add(bVerTodo);
        footerPanel.add(bExportar);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_AZUL), 
                "Datos de Entrada", 
                TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, 
                FONT_BOLD, 
                COLOR_AZUL));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 5, 8, 5);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        // Inicialización componentes formulario
        jCBArticuloEntrada = new JComboBox<>();
        jFTCantidadEntrada = new JFormattedTextField();
        jCBUbicacionEntrada = new JComboBox<>();
        jFTEntregadoA = new JFormattedTextField();
        jCheckBoxDonado = new JCheckBox("Marcar si es donado");
        jCheckBoxDonado.setBackground(Color.WHITE);
        jCheckBoxDonado.setFont(FONT_PLAIN);
        jLabelCostoDivisa = new JLabel("Costo ($):");
        jLabelCostoDivisa.setFont(FONT_PLAIN);
        jTFCostoDivisa = new JTextField();
        jCheckBoxVencimiento = new JCheckBox("¿Se puede vencer?");
        jCheckBoxVencimiento.setBackground(Color.WHITE);
        jCheckBoxVencimiento.setFont(FONT_PLAIN);
        jLabelVencimiento = new JLabel("Fecha Vencimiento:");
        jLabelVencimiento.setFont(FONT_PLAIN);
        jDateVencimiento = new JDateChooser();

        // Fila 0: Artículo
        addLabelAndField(p, "Artículo:", jCBArticuloEntrada, 0, g);
        // Fila 1: Cantidad
        addLabelAndField(p, "Cantidad:", jFTCantidadEntrada, 1, g);
        // Fila 2: Ubicación
        addLabelAndField(p, "Ubicación:", jCBUbicacionEntrada, 2, g);
        // Fila 3: Entregado a
        addLabelAndField(p, "Entregado a:", jFTEntregadoA, 3, g);
        
        // Fila 4: Separador y Check Donado
        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        p.add(new JSeparator(), g);
        g.gridy = 5;
        p.add(jCheckBoxDonado, g);
        g.gridwidth = 1;

        // Fila 6: Costo (Label y Field)
        addLabelAndField(p, jLabelCostoDivisa, jTFCostoDivisa, 6, g);

        // Fila 7: Separador y Check Vencimiento
        g.gridx = 0; g.gridy = 7; g.gridwidth = 2;
        p.add(new JSeparator(), g);
        g.gridy = 8;
        p.add(jCheckBoxVencimiento, g);
        g.gridwidth = 1;

        // Fila 9: Fecha Vencimiento
        addLabelAndField(p, jLabelVencimiento, jDateVencimiento, 9, g);

        // Espaciador final
        g.gridy = 10; g.weighty = 1.0;
        p.add(new JLabel(), g);

        return p;
    }

    private void addLabelAndField(JPanel p, Object label, JComponent field, int row, GridBagConstraints g) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        if (label instanceof String) {
            JLabel l = new JLabel((String) label);
            l.setFont(FONT_PLAIN);
            p.add(l, g);
        } else {
            p.add((Component) label, g);
        }

        g.gridx = 1; g.weightx = 1.0;
        field.setPreferredSize(new Dimension(150, 30));
        p.add(field, g);
    }

    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_AZUL), "Registro de Movimientos"));

        jTable1 = new JTable();
        jTable1.setRowHeight(25);
        jTable1.getTableHeader().setBackground(COLOR_AZUL);
        jTable1.getTableHeader().setForeground(Color.WHITE);
        jTable1.getTableHeader().setFont(FONT_BOLD);

        JScrollPane scroll = new JScrollPane(jTable1);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private JButton crearBotonHeader(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this::bVolverActionPerformed);
        return btn;
    }

    private JButton crearBotonAccion(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        switch (texto) {
            case "Registrar": btn.addActionListener(e -> onRegistrarEntrada()); break;
            case "Consultar": btn.addActionListener(this::bConsultarActionPerformed); break;
            case "Modificar": btn.addActionListener(this::bModificarActionPerformed); break;
            case "Ver Todo": btn.addActionListener(this::bVerTodoActionPerformed); break;
            case "Exportar": btn.addActionListener(this::bExportarActionPerformed); break;
        }
        return btn;
    }

    // ========================================================================
    // === LÓGICA DE NEGOCIO (Preservada del código original) ===
    // ========================================================================

    private void configurarLogica() {
        // Eventos Checkboxes
        jCheckBoxDonado.addActionListener(e -> {
            boolean donado = jCheckBoxDonado.isSelected();
            jLabelCostoDivisa.setVisible(!donado);
            jTFCostoDivisa.setVisible(!donado);
        });

        jCheckBoxVencimiento.addActionListener(e -> {
            boolean vencible = jCheckBoxVencimiento.isSelected();
            jLabelVencimiento.setVisible(vencible);
            jDateVencimiento.setVisible(vencible);
        });

        // Visibilidad Inicial
        jLabelVencimiento.setVisible(false);
        jDateVencimiento.setVisible(false);
        
        // Evento Tabla
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    cargarFormularioDesdeTablas();
                }
            }
        });
    }

    private void cargarCombos() {
        try {
            DefaultComboBoxModel<Articulo> mArticulos = new DefaultComboBoxModel<>();
            List<Articulo> articulos = articuloControl.obtenerTodosArticulos();
            for (Articulo a : articulos) mArticulos.addElement(a);
            jCBArticuloEntrada.setModel(mArticulos);

            DefaultComboBoxModel<Ubicacion> mUbicaciones = new DefaultComboBoxModel<>();
            List<Ubicacion> ubicaciones = ubicacionDAO.listar();
            for (Ubicacion u : ubicaciones) mUbicaciones.addElement(u);
            jCBUbicacionEntrada.setModel(mUbicaciones);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRegistrarEntrada() {
        Articulo articulo = (Articulo) jCBArticuloEntrada.getSelectedItem();
        Ubicacion ubicacion = (Ubicacion) jCBUbicacionEntrada.getSelectedItem();
        int cantidad = parseIntSafe(jFTCantidadEntrada.getText());

        try {
            if (articulo == null || ubicacion == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un artículo y una ubicación.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor que cero.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Timestamp fechaVencimiento = null;
            if (jCheckBoxVencimiento.isSelected()) {
                Date selectedDate = jDateVencimiento.getDate();
                if (selectedDate == null) {
                    JOptionPane.showMessageDialog(this, "Selecciona una fecha de vencimiento válida.", "Validación de Fecha", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fechaVencimiento = new Timestamp(selectedDate.getTime());
                
                // Validación fecha anterior a hoy
                java.util.Calendar calVencimiento = java.util.Calendar.getInstance();
                calVencimiento.setTime(fechaVencimiento);
                setMidnight(calVencimiento);

                java.util.Calendar calHoy = java.util.Calendar.getInstance();
                setMidnight(calHoy);

                if (calVencimiento.before(calHoy)) {
                    JOptionPane.showMessageDialog(this, "La fecha de vencimiento no puede ser anterior al día de hoy.", "Producto Vencido", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Double costoDivisa = null;
            Double costoBolivar = null;
            if (!jCheckBoxDonado.isSelected()) {
                double costoVal = parseDoubleSafe(jTFCostoDivisa.getText());
                if (costoVal > 0) {
                    costoDivisa = costoVal;
                    double tasaActual = GestorBcv.getInstance().getTasaActual();
                    if (tasaActual <= 0) {
                        JOptionPane.showMessageDialog(this, "No se pudo obtener la tasa de cambio actual.", "Error de Tasa", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    costoBolivar = costoDivisa * tasaActual;
                }
            }

            boolean ok = movimientoControl.registrarEntrada(
                articulo.getIdArticulo(),
                cantidad,
                ubicacion.getId_ubicacion(),
                jFTEntregadoA.getText().trim(),
                jCheckBoxDonado.isSelected(),
                costoDivisa,
                costoBolivar,
                fechaVencimiento
            );

            if (ok) {
                JOptionPane.showMessageDialog(this, "Entrada registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarTablaMovimientos();
            } else {
                JOptionPane.showMessageDialog(this, "Error desconocido al registrar la entrada.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (CapacidadInsuficienteException e) {
            manejarExcepcionCapacidad(e);
        } catch (SQLException sqe) {
            JOptionPane.showMessageDialog(this, "Error SQL: " + sqe.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cargarTablaMovimientos() {
        try {
            String[] columnas = {"ID", "Artículo", "Cantidad", "Ubicación", "Costo $", "Costo bs", "Vencimiento", "Entregado Por", "Fecha/Hora"};
            
            modeloTabla = new DefaultTableModel(columnas, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) return Long.class;
                    if (columnIndex == 2) return Integer.class;
                    if (columnIndex == 6) return Timestamp.class;
                    return Object.class;
                }
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };

            jTable1.setModel(modeloTabla);
            // Ocultar ID
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(0).setWidth(0);

            var lista = movimientoControl.obtenerEntradas();

            for (Movimiento m : lista) {
                Object fechaVenc = m.getFechaVencimiento() != null ? m.getFechaVencimiento() : null;
                Object costoDiv = m.getCosto() != null ? String.format("%.2f", m.getCosto()) : "-";
                double costoBsVal = m.getCostoBs();
                Object costoBs = (costoBsVal > 0) ? String.format("%.2f", costoBsVal) : "-";

                modeloTabla.addRow(new Object[]{
                    m.getIdMovimiento(), m.getNombreArticulo(), m.getCantidad(),
                    m.getNombreUbicacionDestino() != null ? m.getNombreUbicacionDestino() : "-",
                    costoDiv, costoBs, fechaVenc,
                    (m.getEntregado() != null ? m.getEntregado() : "-"),
                    m.getFechaHora()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarFormularioDesdeTablas() {
        int fila = jTable1.getSelectedRow();
        if (fila >= 0) {
            // ID
            Object idObj = modeloTabla.getValueAt(fila, 0);
            if (idObj instanceof Integer) this.idMovimientoSeleccionado = ((Integer) idObj).longValue();
            else if (idObj instanceof Long) this.idMovimientoSeleccionado = (Long) idObj;

            // Datos básicos
            String nombreArticulo = (String) modeloTabla.getValueAt(fila, 1);
            String nombreUbicacion = (String) modeloTabla.getValueAt(fila, 3);
            jFTCantidadEntrada.setText(modeloTabla.getValueAt(fila, 2).toString());
            jFTEntregadoA.setText((String) modeloTabla.getValueAt(fila, 7));

            // Costos
            String costoDivisaStr = (String) modeloTabla.getValueAt(fila, 4);
            boolean esDonado = costoDivisaStr.equals("-");
            jCheckBoxDonado.setSelected(esDonado);
            jTFCostoDivisa.setText(esDonado ? "" : costoDivisaStr);
            jTFCostoDivisa.setVisible(!esDonado);
            jLabelCostoDivisa.setVisible(!esDonado);

            // Vencimiento
            Object vencimientoObj = modeloTabla.getValueAt(fila, 6);
            boolean tieneVencimiento = (vencimientoObj instanceof Timestamp);
            jCheckBoxVencimiento.setSelected(tieneVencimiento);
            jDateVencimiento.setVisible(tieneVencimiento);
            jLabelVencimiento.setVisible(tieneVencimiento);

            if (tieneVencimiento) jDateVencimiento.setDate(new Date(((Timestamp) vencimientoObj).getTime()));
            else jDateVencimiento.setDate(null);

            // Combos
            seleccionarEnComboBox(jCBArticuloEntrada, nombreArticulo);
            seleccionarEnComboBox(jCBUbicacionEntrada, nombreUbicacion);
        }
    }

    private void bModificarActionPerformed(ActionEvent evt) {
        int fila = jTable1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un movimiento de entrada para modificar.");
            return;
        }

        try {
            int idMovimiento = Integer.parseInt(jTable1.getModel().getValueAt(fila, 0).toString());
            Articulo articulo = (Articulo) jCBArticuloEntrada.getSelectedItem();
            Ubicacion ubicacion = (Ubicacion) jCBUbicacionEntrada.getSelectedItem();
            int cantidad = parseIntSafe(jFTCantidadEntrada.getText());

            if (articulo == null || ubicacion == null || cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "Datos obligatorios inválidos.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Timestamp fechaVencimiento = null;
            if (jCheckBoxVencimiento.isSelected()) {
                Date selectedDate = jDateVencimiento.getDate();
                if (selectedDate == null) {
                    JOptionPane.showMessageDialog(this, "Selecciona una fecha válida.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fechaVencimiento = new Timestamp(selectedDate.getTime());
            }

            Double costoDivisa = null;
            Double costoBolivar = null;
            if (!jCheckBoxDonado.isSelected()) {
                double costoDivisaVal = parseDoubleSafe(jTFCostoDivisa.getText());
                if (costoDivisaVal > 0) {
                    costoDivisa = costoDivisaVal;
                    double tasaActual = GestorBcv.getInstance().getTasaActual();
                    if (tasaActual <= 0) {
                        JOptionPane.showMessageDialog(this, "Error al obtener tasa BCV.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    costoBolivar = costoDivisa * tasaActual;
                }
            }

            boolean ok = movimientoControl.actualizarEntrada(
                idMovimiento, articulo.getIdArticulo(), cantidad, ubicacion.getId_ubicacion(),
                jFTEntregadoA.getText().trim(), jCheckBoxDonado.isSelected(),
                costoDivisa, costoBolivar, fechaVencimiento
            );

            if (ok) {
                JOptionPane.showMessageDialog(this, "Entrada modificada correctamente.");
                cargarTablaMovimientos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo modificar la entrada.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (CapacidadInsuficienteException e) {
            JOptionPane.showMessageDialog(this, String.format("Capacidad excedida.\nRestante: %.3f m³", e.getCapacidadRestante()), "Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bConsultarActionPerformed(ActionEvent evt) {
        try {
            modeloTabla.setRowCount(0);
            Articulo articulo = (Articulo) jCBArticuloEntrada.getSelectedItem();
            Ubicacion ubicacion = (Ubicacion) jCBUbicacionEntrada.getSelectedItem();
            Integer idArt = (articulo != null) ? articulo.getIdArticulo() : null;
            Integer idUbi = (ubicacion != null) ? ubicacion.getId_ubicacion() : null;

            List<Movimiento> lista = movimientoControl.buscarEntradas(idArt, idUbi);

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron resultados para los filtros seleccionados.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (Movimiento m : lista) {
                Object fechaVenc = m.getFechaVencimiento() != null ? m.getFechaVencimiento() : null;
                String cDiv = m.getCosto() != null ? String.format("%.2f", m.getCosto()) : "-";
                String cBs = m.getCostoBolivar() != null ? String.format("%.2f", m.getCostoBolivar()) : "-";
                
                modeloTabla.addRow(new Object[]{
                    m.getIdMovimiento(), m.getNombreArticulo(), m.getCantidad(),
                    m.getNombreUbicacionDestino() != null ? m.getNombreUbicacionDestino() : "-",
                    cDiv, cBs, fechaVenc,
                    (m.getEntregado() != null ? m.getEntregado() : "-"), m.getFechaHora()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar: " + e.getMessage());
        }
    }

    private void bVerTodoActionPerformed(ActionEvent evt) {
        try {
            jCBArticuloEntrada.setSelectedItem(null);
            jCBUbicacionEntrada.setSelectedItem(null);
            List<Movimiento> lista = movimientoControl.buscarEntradas(null, null);
            modeloTabla.setRowCount(0);
            for (Movimiento m : lista) {
                 Object fechaVenc = m.getFechaVencimiento() != null ? m.getFechaVencimiento() : null;
                 modeloTabla.addRow(new Object[]{
                    m.getIdMovimiento(), m.getNombreArticulo(), m.getCantidad(),
                    m.getNombreUbicacionDestino() != null ? m.getNombreUbicacionDestino() : "-",
                    m.getCosto() != null ? String.format("%.2f", m.getCosto()) : "-",
                    m.getCostoBolivar() != null ? String.format("%.2f", m.getCostoBolivar()) : "-",
                    fechaVenc, (m.getEntregado() != null ? m.getEntregado() : "-"), m.getFechaHora()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bExportarActionPerformed(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Entradas");
        fileChooser.setSelectedFile(new File("Reporte_Entradas.pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".pdf")) fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");

            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                Paragraph title = new Paragraph("Reporte de Entradas de Inventario", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK));
                title.setAlignment(Paragraph.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                int colCount = jTable1.getColumnCount();
                int visibleColCount = colCount - 1; // Ignorar ID
                PdfPTable pdfTable = new PdfPTable(visibleColCount);
                pdfTable.setWidthPercentage(100);

                com.itextpdf.text.Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
                BaseColor headerColor = new BaseColor(155, 89, 182);

                for (int i = 1; i < colCount; i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(jTable1.getColumnName(i), fontHeader));
                    cell.setBackgroundColor(headerColor);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }

                com.itextpdf.text.Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
                for (int rows = 0; rows < jTable1.getRowCount(); rows++) {
                    for (int cols = 1; cols < colCount; cols++) {
                        Object value = jTable1.getValueAt(rows, cols);
                        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : "", fontData));
                        pdfTable.addCell(cell);
                    }
                }

                document.add(pdfTable);
                JOptionPane.showMessageDialog(this, "Reporte exportado exitosamente.");

            } catch (DocumentException | FileNotFoundException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
            } finally {
                if (document.isOpen()) document.close();
            }
        }
    }

    private void bVolverActionPerformed(ActionEvent evt) {
        this.dispose();
        new PrincipalVista(this.usuarioActual).setVisible(true);
    }

    // --- Utilidades ---
    private int parseIntSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0 : Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private double parseDoubleSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0 : Double.parseDouble(s.trim()); } catch (Exception e) { return 0; }
    }

    private void setMidnight(java.util.Calendar cal) {
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
    }

    private <T> void seleccionarEnComboBox(JComboBox<T> combo, String nombre) {
        if (nombre == null) return;
        DefaultComboBoxModel<T> model = (DefaultComboBoxModel<T>) combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            T item = model.getElementAt(i);
            if (item != null && item.toString().equals(nombre)) {
                combo.setSelectedItem(item);
                return;
            }
        }
    }

    private void limpiarCampos() {
        jFTCantidadEntrada.setText("");
        jFTEntregadoA.setText("");
        jTFCostoDivisa.setText("");
        jDateVencimiento.setDate(null);
    }

    private void manejarExcepcionCapacidad(CapacidadInsuficienteException e) {
        String msg = String.format("Capacidad insuficiente en '%s'.\nRestante: %.3f m³\nRequerido: %.3f m³", 
                e.getNombreUbicacion(), e.getCapacidadRestante(), e.getEspacioRequerido());
        if (!e.getSugerencias().isEmpty()) {
            msg += "\n\nSugerencias:";
            for (Ubicacion u : e.getSugerencias()) msg += String.format("\n- %s (Libre: %.3f m³)", u.getNombre(), u.getCapacidadRestante());
        }
        JOptionPane.showMessageDialog(this, msg, "Error de Capacidad", JOptionPane.WARNING_MESSAGE);
    }
}
