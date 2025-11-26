package Vista;

import Controlador.ArticuloControlador;
import Controlador.MovimientoControlador;
import Modelo.Articulo;
import Modelo.InventarioDAO;
import Modelo.Movimiento;
import Modelo.Ubicacion;
import Modelo.UbicacionDAO;
import Modelo.Usuario;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PanelSalida extends JFrame {

    // --- Componentes de la UI ---
    private JComboBox<Articulo> jCBArticuloSalida;
    private JFormattedTextField jFTCantidadSalida;
    private JComboBox<Ubicacion> jCBUbicacionOrigen;
    private JFormattedTextField jFTMotivo;
    
    // Tablas
    private JTable jTablaSalida; // Tabla superior (Historial)
    private JTable jTableEntrada_Inventario; // Tabla inferior (Stock disponible)
    private DefaultTableModel modeloTablaSalida;
    private DefaultTableModel modeloTablaEntrada;

    // Botones
    private JButton bRegistrarSalida, bConsultarSalida, bVerTodo, bExportar, bVolver;

    // --- Variables de Control ---
    private final ArticuloControlador articuloControl = new ArticuloControlador();
    private final InventarioDAO inventarioDAO = new InventarioDAO();
    private final MovimientoControlador movimientoControl = new MovimientoControlador();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();
    private Usuario usuarioActual;
    private Long idMovimientoSeleccionado = null;

    // Constantes de Estilo
    private final Color COLOR_AZUL = new Color(13, 51, 131);
    private final Font FONT_TITLE = new Font("Segoe UI Black", Font.BOLD, 18);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);

    // Constructor
    public PanelSalida(Usuario usuario) {
        this.usuarioActual = usuario;

        // Configuración de la Ventana
        setTitle("Baja de Mobiliario (Salidas)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750); 
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        // Inicialización
        initUI();
        cargarCombos();
        cargarTablaSalidas();
        cargarTablaEntradas();
        configurarEventosTablas();
    }

    public PanelSalida() {
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

        JLabel lblTitulo = new JLabel("Baja de Mobiliario", SwingConstants.CENTER);
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
        gbc.weightx = 0.30; 
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 10);
        centerPanel.add(formPanel, gbc);

        // --- Panel Tablas (Derecha) ---
        JPanel tablesPanel = crearPanelTablas();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.70; 
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 10, 20);
        centerPanel.add(tablesPanel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. FOOTER BOTONES (SUR)
        // ==========================================
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        footerPanel.setBackground(Color.WHITE);

        bRegistrarSalida = crearBotonAccion("Confirmar");
        bConsultarSalida = crearBotonAccion("Consultar");
        bVerTodo = crearBotonAccion("Ver Todo");
        bExportar = crearBotonAccion("Exportar");

        footerPanel.add(bRegistrarSalida);
        footerPanel.add(bConsultarSalida);
        footerPanel.add(bVerTodo);
        footerPanel.add(bExportar);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_AZUL),
                "Datos de Salida",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                FONT_BOLD,
                COLOR_AZUL));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 5, 10, 5);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        // Componentes
        jCBArticuloSalida = new JComboBox<>();
        jFTCantidadSalida = new JFormattedTextField();
        jCBUbicacionOrigen = new JComboBox<>();
        jFTMotivo = new JFormattedTextField();

        // Fila 0: Artículo
        addLabelAndField(p, "Artículo:", jCBArticuloSalida, 0, g);
        // Fila 1: Cantidad
        addLabelAndField(p, "Cantidad:", jFTCantidadSalida, 1, g);
        // Fila 2: Ubicación Origen
        addLabelAndField(p, "Ubicación Origen:", jCBUbicacionOrigen, 2, g);
        // Fila 3: Motivo
        addLabelAndField(p, "Motivo:", jFTMotivo, 3, g);

        // Espaciador vertical
        g.gridy = 4; g.weighty = 1.0;
        p.add(new JLabel(), g);

        return p;
    }

    private void addLabelAndField(JPanel p, String text, JComponent field, int row, GridBagConstraints g) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        JLabel l = new JLabel(text);
        l.setFont(FONT_PLAIN);
        p.add(l, g);

        g.gridx = 1; g.weightx = 1.0;
        field.setPreferredSize(new Dimension(150, 30));
        p.add(field, g);
    }

    private JPanel crearPanelTablas() {
        JPanel container = new JPanel(new GridLayout(2, 1, 0, 15));
        container.setBackground(Color.WHITE);

        // --- Tabla Superior: Salidas ---
        JPanel panelSalidas = new JPanel(new BorderLayout());
        panelSalidas.setBackground(Color.WHITE);
        panelSalidas.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_AZUL), "Historial de Salidas", 
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.TOP, FONT_BOLD, COLOR_AZUL));
        
        jTablaSalida = new JTable();
        jTablaSalida.setRowHeight(25);
        jTablaSalida.getTableHeader().setBackground(COLOR_AZUL);
        jTablaSalida.getTableHeader().setForeground(Color.WHITE);
        jTablaSalida.getTableHeader().setFont(FONT_BOLD);
        panelSalidas.add(new JScrollPane(jTablaSalida), BorderLayout.CENTER);

        // --- Tabla Inferior: Inventario (Entradas) ---
        JPanel panelInventario = new JPanel(new BorderLayout());
        panelInventario.setBackground(Color.WHITE);
        panelInventario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(46, 204, 113)), "Inventario Disponible (Origen)", 
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.TOP, FONT_BOLD, new Color(46, 204, 113)));

        jTableEntrada_Inventario = new JTable();
        jTableEntrada_Inventario.setRowHeight(25);
        jTableEntrada_Inventario.getTableHeader().setBackground(new Color(46, 204, 113)); // Header Verde
        jTableEntrada_Inventario.getTableHeader().setForeground(Color.WHITE);
        jTableEntrada_Inventario.getTableHeader().setFont(FONT_BOLD);
        panelInventario.add(new JScrollPane(jTableEntrada_Inventario), BorderLayout.CENTER);

        container.add(panelSalidas);
        container.add(panelInventario);

        return container;
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
            case "Confirmar": btn.addActionListener(e -> onRegistrarSalida()); break;
            case "Consultar": btn.addActionListener(this::bConsultarSalidaActionPerformed); break;
            case "Ver Todo": btn.addActionListener(this::bVerTodoActionPerformed); break;
            case "Exportar": btn.addActionListener(this::bExportarActionPerformed); break;
        }
        return btn;
    }

    // ========================================================================
    // === LÓGICA DE NEGOCIO ===
    // ========================================================================

    private void configurarEventosTablas() {
        jTablaSalida.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) cargarFormularioDesdeTablaSalida();
            }
        });

        jTableEntrada_Inventario.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) cargarFormularioDesdeTablaEntrada();
            }
        });
    }

    private void cargarCombos() {
        try {
            DefaultComboBoxModel<Articulo> modeloArticulos = new DefaultComboBoxModel<>();
            List<Articulo> articulos = articuloControl.obtenerTodosArticulos();
            for (Articulo a : articulos) modeloArticulos.addElement(a);
            jCBArticuloSalida.setModel(modeloArticulos);

            DefaultComboBoxModel<Ubicacion> modeloUbicaciones = new DefaultComboBoxModel<>();
            List<Ubicacion> ubicaciones = ubicacionDAO.listar();
            for (Ubicacion u : ubicaciones) modeloUbicaciones.addElement(u);
            jCBUbicacionOrigen.setModel(modeloUbicaciones);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRegistrarSalida() {
        try {
            Articulo art = (Articulo) jCBArticuloSalida.getSelectedItem();
            Ubicacion ubicOrigen = (Ubicacion) jCBUbicacionOrigen.getSelectedItem();
            int cantidadInt = parseIntSafe(jFTCantidadSalida.getText());
            double cantidadDouble = (double) cantidadInt;
            String motivo = jFTMotivo.getText().trim();

            if (art == null || ubicOrigen == null) {
                JOptionPane.showMessageDialog(this, "Selecciona artículo y ubicación origen.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cantidadInt <= 0) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida (>0).", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes indicar un motivo de salida.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!this.inventarioDAO.existeStockArticuloEnUbicacion(art.getIdArticulo(), ubicOrigen.getId_ubicacion())) {
                JOptionPane.showMessageDialog(this, "El artículo no se encuentra registrado en la ubicación de origen.", "Error de Ubicación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double stockDisponible = this.inventarioDAO.getStockPorUbicacion(art.getIdArticulo(), ubicOrigen.getId_ubicacion());
            if (stockDisponible < cantidadDouble) {
                JOptionPane.showMessageDialog(this, "Stock insuficiente. Disponible: " + String.format("%.2f", stockDisponible), "Error de Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = movimientoControl.registrarSalida(art.getIdArticulo(), cantidadInt, ubicOrigen.getId_ubicacion(), motivo);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Salida registrada correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
                jFTCantidadSalida.setText("");
                jFTMotivo.setText("");
                cargarTablaSalidas();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar la salida.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error SQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTablaSalidas() {
        try {
            String[] columnas = {"ID", "Artículo", "Cantidad", "Ubicación", "Motivo", "Entregado Por", "Fecha/Hora"};
            
            modeloTablaSalida = new DefaultTableModel(columnas, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) return Long.class;
                    if (columnIndex == 2) return Integer.class;
                    return Object.class;
                }
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };

            jTablaSalida.setModel(modeloTablaSalida);
            jTablaSalida.getColumnModel().getColumn(0).setMinWidth(0);
            jTablaSalida.getColumnModel().getColumn(0).setMaxWidth(0);
            jTablaSalida.getColumnModel().getColumn(0).setWidth(0);

            List<Movimiento> listaSalidas = movimientoControl.obtenerSalidas();
            for (Movimiento m : listaSalidas) {
                modeloTablaSalida.addRow(new Object[]{
                    m.getIdMovimiento(), m.getNombreArticulo(), m.getCantidad(),
                    m.getNombreUbicacionOrigen() != null ? m.getNombreUbicacionOrigen() : "-",
                    m.getMotivo() != null ? m.getMotivo() : "-",
                    m.getEntregado() != null ? m.getEntregado() : "-", m.getFechaHora()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarTablaEntradas() {
        try {
            String[] columnas = {"ID", "Artículo", "Cantidad", "Ubicación Destino", "Costo", "Vencimiento", "Entregado Por", "Fecha/Hora"};
            
            modeloTablaEntrada = new DefaultTableModel(columnas, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) return Long.class;
                    if (columnIndex == 2) return Integer.class;
                    return Object.class;
                }
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };

            jTableEntrada_Inventario.setModel(modeloTablaEntrada);
            jTableEntrada_Inventario.getColumnModel().getColumn(0).setMinWidth(0);
            jTableEntrada_Inventario.getColumnModel().getColumn(0).setMaxWidth(0);
            jTableEntrada_Inventario.getColumnModel().getColumn(0).setWidth(0);

            List<Movimiento> listaEntradas = movimientoControl.obtenerEntradas();
            for (Movimiento m : listaEntradas) {
                modeloTablaEntrada.addRow(new Object[]{
                    m.getIdMovimiento(), m.getNombreArticulo(), m.getCantidad(),
                    m.getNombreUbicacionDestino() != null ? m.getNombreUbicacionDestino() : "-",
                    m.getCosto() != null ? String.format("%.2f", m.getCosto()) : "-",
                    (m.getFechaVencimiento() != null ? m.getFechaVencimiento() : "-"),
                    (m.getEntregado() != null ? m.getEntregado() : "-"), m.getFechaHora()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarFormularioDesdeTablaSalida() {
        int fila = jTablaSalida.getSelectedRow();
        if (fila >= 0) {
            Object idObj = modeloTablaSalida.getValueAt(fila, 0);
            this.idMovimientoSeleccionado = (idObj instanceof Integer) ? ((Integer) idObj).longValue() : (Long) idObj;

            String nombreArticulo = (String) modeloTablaSalida.getValueAt(fila, 1);
            String nombreUbicacion = (String) modeloTablaSalida.getValueAt(fila, 3);
            String motivo = (String) modeloTablaSalida.getValueAt(fila, 4);

            jFTCantidadSalida.setText(modeloTablaSalida.getValueAt(fila, 2).toString());
            jFTMotivo.setText(motivo.equals("-") ? "" : motivo);

            seleccionarEnComboBox(jCBArticuloSalida, nombreArticulo);
            seleccionarEnComboBox(jCBUbicacionOrigen, nombreUbicacion);
        }
    }

    private void cargarFormularioDesdeTablaEntrada() {
        int fila = jTableEntrada_Inventario.getSelectedRow();
        if (fila >= 0) {
            Object idObj = modeloTablaEntrada.getValueAt(fila, 0);
            if (idObj instanceof Long) this.idMovimientoSeleccionado = (Long) idObj;
            else if (idObj instanceof Integer) this.idMovimientoSeleccionado = ((Integer) idObj).longValue();

            String nombreArticulo = (String) modeloTablaEntrada.getValueAt(fila, 1);
            String nombreUbicacionDestino = (String) modeloTablaEntrada.getValueAt(fila, 3);

            jFTCantidadSalida.setText(modeloTablaEntrada.getValueAt(fila, 2).toString());
            jFTMotivo.setText(""); 

            seleccionarEnComboBox(jCBArticuloSalida, nombreArticulo);
            seleccionarEnComboBox(jCBUbicacionOrigen, nombreUbicacionDestino);
        }
    }

    private void bConsultarSalidaActionPerformed(ActionEvent evt) {
        try {
            modeloTablaSalida.setRowCount(0);
            Articulo articulo = (Articulo) jCBArticuloSalida.getSelectedItem();
            Ubicacion ubicacion = (Ubicacion) jCBUbicacionOrigen.getSelectedItem();
            Integer idArt = (articulo != null) ? articulo.getIdArticulo() : null;
            Integer idUbi = (ubicacion != null) ? ubicacion.getId_ubicacion() : null;

            List<Movimiento> lista = movimientoControl.buscarSalidas(idArt, idUbi);

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron salidas.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (Movimiento m : lista) {
                modeloTablaSalida.addRow(new Object[]{
                    m.getIdMovimiento(), m.getNombreArticulo(), m.getCantidad(),
                    m.getNombreUbicacionOrigen() != null ? m.getNombreUbicacionOrigen() : "-",
                    m.getMotivo() != null ? m.getMotivo() : "-",
                    m.getEntregado() != null ? m.getEntregado() : "-", m.getFechaHora()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bVerTodoActionPerformed(ActionEvent evt) {
        try {
            jCBArticuloSalida.setSelectedItem(null);
            jCBUbicacionOrigen.setSelectedItem(null);
            List<Movimiento> lista = movimientoControl.buscarSalidas(null, null);
            modeloTablaSalida.setRowCount(0);
            for (Movimiento m : lista) {
                modeloTablaSalida.addRow(new Object[]{
                    m.getIdMovimiento(), m.getNombreArticulo(), m.getCantidad(),
                    m.getNombreUbicacionOrigen() != null ? m.getNombreUbicacionOrigen() : "-",
                    (m.getMotivo() != null ? m.getMotivo() : "-"),
                    (m.getEntregado() != null ? m.getEntregado() : "-"), m.getFechaHora()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bExportarActionPerformed(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Movimientos");
        fileChooser.setSelectedFile(new File("Reporte_Inventario_Movimientos.pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".pdf")) fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");

            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                Paragraph title = new Paragraph("Reporte Consolidado de Inventario", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY));
                title.setAlignment(Paragraph.ALIGN_CENTER);
                title.setSpacingAfter(30);
                document.add(title);

                // Exportar Tabla Salidas
                exportarTablaPDF(document, jTablaSalida, "Sección 1: Movimientos de Salida", new BaseColor(230, 126, 34));
                
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("-----------------------------------------------------------------------"));
                document.add(new Paragraph("\n"));

                // Exportar Tabla Entradas
                exportarTablaPDF(document, jTableEntrada_Inventario, "Sección 2: Inventario de Entrada", new BaseColor(46, 204, 113));

                document.add(new Paragraph("\n"));
                JOptionPane.showMessageDialog(this, "Reporte exportado exitosamente.");

            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
            } finally {
                if (document.isOpen()) document.close();
            }
        }
    }

    // ⭐ MÉTODO CORREGIDO para evitar conflicto entre java.awt.Font y com.itextpdf.text.Font ⭐
    private void exportarTablaPDF(Document document, JTable table, String title, BaseColor color) throws DocumentException {
        // Usamos nombre completo de la clase Font de iText
        com.itextpdf.text.Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        com.itextpdf.text.Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
        
        Paragraph pTitle = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, color));
        pTitle.setSpacingAfter(10);
        document.add(pTitle);

        int colCount = table.getColumnCount();
        int visibleCols = colCount - 1; 
        
        if (visibleCols > 0) {
            PdfPTable pdfTable = new PdfPTable(visibleCols);
            pdfTable.setWidthPercentage(100);

            for (int i = 1; i < colCount; i++) {
                PdfPCell cell = new PdfPCell(new Phrase(table.getColumnName(i), fontHeader));
                cell.setBackgroundColor(color);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

            for (int rows = 0; rows < table.getRowCount(); rows++) {
                for (int cols = 1; cols < colCount; cols++) {
                    Object val = table.getValueAt(rows, cols);
                    PdfPCell cell = new PdfPCell(new Phrase(val != null ? val.toString() : "", fontData));
                    pdfTable.addCell(cell);
                }
            }
            document.add(pdfTable);
        } else {
            document.add(new Paragraph("No hay datos visibles."));
        }
    }

    private void bVolverActionPerformed(ActionEvent evt) {
        this.dispose();
        new PrincipalVista(this.usuarioActual).setVisible(true);
    }

    // --- Utilidades ---
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

    private int parseIntSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0 : Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
}
