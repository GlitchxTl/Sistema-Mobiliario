package Vista;

import Controlador.ArticuloControlador;
import Controlador.MovimientoControlador;
import Modelo.Articulo;
import Modelo.CapacidadInsuficienteException;
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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PanelTraslado extends JFrame {

    // --- Componentes de la UI ---
    private JComboBox<Articulo> jCBArticuloTraslado;
    private JFormattedTextField jFTCantidad;
    private JComboBox<Ubicacion> jCBUbicacionOrigen;
    private JComboBox<Ubicacion> jCBUbicacionDestino;
    private JFormattedTextField jFTEntregadoA;
    private JTable jTableTraslado;
    private DefaultTableModel modeloTabla;

    // Botones
    private JButton bRegistrarTraslado, bConsultar, bModificar, bVerTodo, bExportar, bVolver;

    // --- Variables de Control ---
    private final ArticuloControlador articuloControl = new ArticuloControlador();
    private final MovimientoControlador movimientoControl = new MovimientoControlador();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();
    private Usuario usuarioActual;
    private Long idMovimientoSeleccionado = null;

    // Constantes de Estilo
    private final Color COLOR_AZUL = new Color(13, 51, 131);
    private final Font FONT_TITLE = new Font("Segoe UI Black", Font.BOLD, 18);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);

    // Constructor que recibe el usuario
    public PanelTraslado(Usuario usuario) {
        this.usuarioActual = usuario;

        // Configuración de la Ventana
        setTitle("Traslado de Bienes Mobiliarios");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        // Inicializar UI y Lógica
        initUI();
        cargarCombos();
        cargarTablaTraslados();
        configurarEventos();

        // Permisos
        if (usuarioActual != null && !"Administrador".equals(usuarioActual.getRol())) {
            bModificar.setVisible(false);
        }
    }

    // Constructor vacío
    public PanelTraslado() {
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

        JLabel lblTitulo = new JLabel("Traslado de Bienes Mobiliarios", SwingConstants.CENTER);
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

        bRegistrarTraslado = crearBotonAccion("Registrar");
        bConsultar = crearBotonAccion("Consultar");
        bModificar = crearBotonAccion("Modificar");
        bVerTodo = crearBotonAccion("Ver Todo");
        bExportar = crearBotonAccion("Exportar");

        footerPanel.add(bRegistrarTraslado);
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
                "Datos del Traslado",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                FONT_BOLD,
                COLOR_AZUL));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 5, 10, 5);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        // Inicialización componentes
        jCBArticuloTraslado = new JComboBox<>();
        jFTCantidad = new JFormattedTextField();
        jCBUbicacionOrigen = new JComboBox<>();
        jCBUbicacionDestino = new JComboBox<>();
        jFTEntregadoA = new JFormattedTextField();

        // Fila 0: Artículo
        addLabelAndField(p, "Artículo:", jCBArticuloTraslado, 0, g);
        // Fila 1: Cantidad
        addLabelAndField(p, "Cantidad:", jFTCantidad, 1, g);
        // Fila 2: Origen
        addLabelAndField(p, "Ubicación Origen:", jCBUbicacionOrigen, 2, g);
        // Fila 3: Destino
        addLabelAndField(p, "Ubicación Destino:", jCBUbicacionDestino, 3, g);
        // Fila 4: Entregado A
        addLabelAndField(p, "Entregado a:", jFTEntregadoA, 4, g);

        // Espaciador final
        g.gridy = 5; g.weighty = 1.0;
        p.add(new JLabel(), g);

        return p;
    }

    private void addLabelAndField(JPanel p, String labelText, JComponent field, int row, GridBagConstraints g) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        JLabel l = new JLabel(labelText);
        l.setFont(FONT_PLAIN);
        p.add(l, g);

        g.gridx = 1; g.weightx = 1.0;
        field.setPreferredSize(new Dimension(150, 30));
        p.add(field, g);
    }

    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_AZUL), "Historial de Traslados"));

        jTableTraslado = new JTable();
        jTableTraslado.setRowHeight(25);
        jTableTraslado.getTableHeader().setBackground(COLOR_AZUL);
        jTableTraslado.getTableHeader().setForeground(Color.WHITE);
        jTableTraslado.getTableHeader().setFont(FONT_BOLD);

        JScrollPane scroll = new JScrollPane(jTableTraslado);
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
            case "Registrar": btn.addActionListener(e -> onRegistrarTraslado()); break;
            case "Consultar": btn.addActionListener(this::bConsultarActionPerformed); break;
            case "Modificar": btn.addActionListener(this::bModificarActionPerformed); break;
            case "Ver Todo": btn.addActionListener(this::bVerTodoActionPerformed); break;
            case "Exportar": btn.addActionListener(this::bExportarActionPerformed); break;
        }
        return btn;
    }

    // ========================================================================
    // === LÓGICA DE NEGOCIO (Preservada) ===
    // ========================================================================

    private void configurarEventos() {
        jTableTraslado.addMouseListener(new MouseAdapter() {
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
            // Artículos
            DefaultComboBoxModel<Articulo> modeloArticulos = new DefaultComboBoxModel<>();
            List<Articulo> articulos = articuloControl.obtenerTodosArticulos();
            for (Articulo a : articulos) modeloArticulos.addElement(a);
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
            jCBUbicacionDestino.setModel(modeloUbicDestino);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRegistrarTraslado() {
        try {
            Articulo art = (Articulo) jCBArticuloTraslado.getSelectedItem();
            Ubicacion origen = (Ubicacion) jCBUbicacionOrigen.getSelectedItem();
            Ubicacion destino = (Ubicacion) jCBUbicacionDestino.getSelectedItem();
            int cantidad = parseIntSafe(jFTCantidad.getText());
            String entregadoA = jFTEntregadoA.getText().trim();

            if (art == null || origen == null || destino == null) {
                JOptionPane.showMessageDialog(this, "Selecciona artículo, ubicación origen y destino.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (origen.getId_ubicacion() == destino.getId_ubicacion()) {
                JOptionPane.showMessageDialog(this, "La ubicación origen y destino no pueden ser la misma.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida (>0).", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Ubicacion uDestino = ubicacionDAO.obtenerPorId(destino.getId_ubicacion());
            if (uDestino == null) {
                JOptionPane.showMessageDialog(this, "Error: Ubicación de destino no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double volumenArticulo = art.getAltura() * art.getAnchura() * art.getProfundidad();
            double volumenTotal = volumenArticulo * cantidad;

            if (uDestino.getCapacidadRestante() < volumenTotal) {
                JOptionPane.showMessageDialog(this, "No hay suficiente espacio en la ubicación destino.\nCapacidad restante: " + uDestino.getCapacidadRestante() + " m³", "Capacidad insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = movimientoControl.registrarTraslado(
                    art.getIdArticulo(),
                    cantidad,
                    origen.getId_ubicacion(),
                    destino.getId_ubicacion(),
                    entregadoA
            );

            if (ok) {
                JOptionPane.showMessageDialog(this, "Traslado registrado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
                jFTCantidad.setText("");
                jFTEntregadoA.setText("");
                cargarTablaTraslados();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el traslado.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error SQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en la Operación", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cargarTablaTraslados() {
        try {
            List<Movimiento> traslados = movimientoControl.obtenerTraslados();
            String[] columnas = {"ID", "Artículo", "Cantidad", "Origen", "Destino", "Entregado Por", "Fecha/Hora"};

            modeloTabla = new DefaultTableModel(columnas, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) return Long.class;
                    if (columnIndex == 2) return Integer.class;
                    return Object.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };

            jTableTraslado.setModel(modeloTabla);
            jTableTraslado.getColumnModel().getColumn(0).setMinWidth(0);
            jTableTraslado.getColumnModel().getColumn(0).setMaxWidth(0);
            jTableTraslado.getColumnModel().getColumn(0).setWidth(0);

            for (Movimiento m : traslados) {
                modeloTabla.addRow(new Object[]{
                    m.getIdMovimiento(),
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
            JOptionPane.showMessageDialog(this, "Error cargando tabla: " + e.getMessage());
        }
    }

    private void cargarFormularioDesdeTablas() {
        int fila = jTableTraslado.getSelectedRow();
        if (fila >= 0) {
            Object idObj = modeloTabla.getValueAt(fila, 0);
            if (idObj instanceof Integer) this.idMovimientoSeleccionado = ((Integer) idObj).longValue();
            else if (idObj instanceof Long) this.idMovimientoSeleccionado = (Long) idObj;

            String nombreArticulo = (String) modeloTabla.getValueAt(fila, 1);
            String nombreUbicacionOrigen = (String) modeloTabla.getValueAt(fila, 3);
            String nombreUbicacionDestino = (String) modeloTabla.getValueAt(fila, 4);

            jFTCantidad.setText(modeloTabla.getValueAt(fila, 2).toString());
            jFTEntregadoA.setText((String) modeloTabla.getValueAt(fila, 5));

            seleccionarEnComboBox(jCBArticuloTraslado, nombreArticulo);
            seleccionarEnComboBox(jCBUbicacionOrigen, nombreUbicacionOrigen);
            seleccionarEnComboBox(jCBUbicacionDestino, nombreUbicacionDestino);
        }
    }

    private void bModificarActionPerformed(ActionEvent evt) {
        if (this.idMovimientoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un traslado de la tabla para modificar.");
            return;
        }

        try {
            int idMovimiento = this.idMovimientoSeleccionado.intValue();
            Articulo articulo = (Articulo) jCBArticuloTraslado.getSelectedItem();
            Ubicacion origen = (Ubicacion) jCBUbicacionOrigen.getSelectedItem();
            Ubicacion destino = (Ubicacion) jCBUbicacionDestino.getSelectedItem();
            int cantidad = parseIntSafe(jFTCantidad.getText());

            if (articulo == null || origen == null || destino == null || cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "Datos obligatorios inválidos.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (Objects.equals(origen.getId_ubicacion(), destino.getId_ubicacion())) {
                JOptionPane.showMessageDialog(this, "La ubicación origen y destino no pueden ser la misma.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String entregadoA = jFTEntregadoA.getText().trim();

            boolean ok = movimientoControl.actualizarTraslado(
                    idMovimiento,
                    articulo.getIdArticulo(),
                    cantidad,
                    origen.getId_ubicacion(),
                    destino.getId_ubicacion(),
                    entregadoA
            );

            if (ok) {
                JOptionPane.showMessageDialog(this, "Traslado modificado correctamente.");
                cargarTablaTraslados();
                this.idMovimientoSeleccionado = null;
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo modificar el traslado.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (CapacidadInsuficienteException e) {
            JOptionPane.showMessageDialog(this, String.format("Capacidad excedida en destino.\nRestante: %.3f m³", e.getCapacidadRestante()), "Capacidad Insuficiente", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bConsultarActionPerformed(ActionEvent evt) {
        try {
            modeloTabla.setRowCount(0);
            Articulo articulo = (Articulo) jCBArticuloTraslado.getSelectedItem();
            Ubicacion ubicacion = (Ubicacion) jCBUbicacionDestino.getSelectedItem();

            Integer idArticulo = (articulo != null) ? articulo.getIdArticulo() : null;
            Integer idUbicacion = (ubicacion != null) ? ubicacion.getId_ubicacion() : null;

            List<Movimiento> lista = movimientoControl.buscarTraslados(idArticulo, idUbicacion);

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron traslados con esos filtros.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (Movimiento m : lista) {
                modeloTabla.addRow(new Object[]{
                    m.getIdMovimiento(), m.getNombreArticulo(), m.getCantidad(),
                    m.getNombreUbicacionOrigen(), m.getNombreUbicacionDestino(),
                    m.getEntregado(), m.getFechaHora()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar: " + e.getMessage());
        }
    }

    private void bVerTodoActionPerformed(ActionEvent evt) {
        try {
            jCBArticuloTraslado.setSelectedItem(null);
            jCBUbicacionDestino.setSelectedItem(null);

            List<Movimiento> lista = movimientoControl.buscarTraslados(null, null);
            modeloTabla.setRowCount(0);

            for (Movimiento m : lista) {
                modeloTabla.addRow(new Object[]{
                    m.getIdMovimiento(), m.getNombreArticulo(), m.getCantidad(),
                    m.getNombreUbicacionOrigen() != null ? m.getNombreUbicacionOrigen() : "-",
                    m.getNombreUbicacionDestino() != null ? m.getNombreUbicacionDestino() : "-",
                    (m.getEntregado() != null ? m.getEntregado() : "-"), m.getFechaHora()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bExportarActionPerformed(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Traslados");
        fileChooser.setSelectedFile(new File("Reporte_Traslados.pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".pdf")) fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");

            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                Paragraph title = new Paragraph("Reporte de Traslados de Inventario", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK));
                title.setAlignment(Paragraph.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                int colCount = jTableTraslado.getColumnCount();
                int visibleColCount = colCount - 1; 
                PdfPTable pdfTable = new PdfPTable(visibleColCount);
                pdfTable.setWidthPercentage(100);

                com.itextpdf.text.Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
                BaseColor headerColor = new BaseColor(155, 89, 182);

                for (int i = 1; i < colCount; i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(jTableTraslado.getColumnName(i), fontHeader));
                    cell.setBackgroundColor(headerColor);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }

                com.itextpdf.text.Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
                for (int rows = 0; rows < jTableTraslado.getRowCount(); rows++) {
                    for (int cols = 1; cols < colCount; cols++) {
                        Object value = jTableTraslado.getValueAt(rows, cols);
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
