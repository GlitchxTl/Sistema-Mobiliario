package Vista;

import Modelo.Ubicacion;
import Modelo.UbicacionDAO;
import Modelo.Usuario;
import Controlador.UbicacionControlador;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PanelUbicacion extends JFrame {

    // --- Componentes de la UI ---
    private JFormattedTextField jFTNombre;
    private JFormattedTextField jFTDescripcion;
    private JFormattedTextField jFTAltura;
    private JFormattedTextField jFTAnchura;
    private JFormattedTextField jFTProfundidad;
    private JTable jTablaUbicaciones;
    private DefaultTableModel modeloTabla;

    // Botones
    private JButton bCrearUbicacion, bConsultarUbicacion, bModificarUbicacion, bDeshabilitarUbicación, bVerTodo, bExportar, bVolver;

    // --- Variables de Control ---
    private final UbicacionDAO dao = new UbicacionDAO();
    private final UbicacionControlador controlador = new UbicacionControlador();
    private Usuario usuarioActual;

    // Listener personalizado
    public interface UbicacionChangeListener {
        void onUbicacionesChanged();
    }
    private final List<UbicacionChangeListener> listeners = new ArrayList<>();

    // Constantes de Estilo
    private final Color COLOR_AZUL = new Color(13, 51, 131);
    private final Font FONT_TITLE = new Font("Segoe UI Black", Font.BOLD, 18);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);

    // Constructor que recibe el usuario
    public PanelUbicacion(Usuario usuario) {
        this.usuarioActual = usuario;

        // Configuración de la Ventana
        setTitle("Gestión de Ubicaciones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        // Inicializar UI
        initUI();
        
        // Lógica de Tabla
        inicializarTabla();
        cargarTabla();

        // Permisos de Usuario
        if (usuarioActual == null || !"Administrador".equalsIgnoreCase(usuarioActual.getRol())) {
            bDeshabilitarUbicación.setVisible(false);
            bCrearUbicacion.setVisible(false);
            bModificarUbicacion.setVisible(false);
        }
    }

    // Constructor vacío
    public PanelUbicacion() {
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

        JLabel lblTitulo = new JLabel("Gestión de Ubicaciones", SwingConstants.CENTER);
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

        bCrearUbicacion = crearBotonAccion("Registrar");
        bConsultarUbicacion = crearBotonAccion("Consultar");
        bModificarUbicacion = crearBotonAccion("Modificar");
        bVerTodo = crearBotonAccion("Ver Todo");
        bDeshabilitarUbicación = crearBotonAccion("Deshabilitar");
        bExportar = crearBotonAccion("Exportar");

        footerPanel.add(bCrearUbicacion);
        footerPanel.add(bConsultarUbicacion);
        footerPanel.add(bModificarUbicacion);
        footerPanel.add(bVerTodo);
        footerPanel.add(bDeshabilitarUbicación);
        footerPanel.add(bExportar);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_AZUL),
                "Datos de Ubicación",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                FONT_BOLD,
                COLOR_AZUL));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 5, 10, 5);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        // Inicialización Componentes
        jFTNombre = new JFormattedTextField();
        jFTDescripcion = new JFormattedTextField();
        jFTAltura = new JFormattedTextField();
        jFTAnchura = new JFormattedTextField();
        jFTProfundidad = new JFormattedTextField();

        // Fila 0: Nombre
        addLabelAndField(p, "Nombre:", jFTNombre, 0, g);
        // Fila 1: Descripción
        addLabelAndField(p, "Descripción:", jFTDescripcion, 1, g);
        
        // Separador
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        p.add(new JSeparator(), g);
        g.gridwidth = 1;

        // Fila 3: Altura
        addLabelAndField(p, "Altura (m):", jFTAltura, 3, g);
        // Fila 4: Anchura
        addLabelAndField(p, "Anchura (m):", jFTAnchura, 4, g);
        // Fila 5: Profundidad
        addLabelAndField(p, "Profundidad (m):", jFTProfundidad, 5, g);

        // Espaciador
        g.gridy = 6; g.weighty = 1.0;
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

    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_AZUL), "Listado de Ubicaciones"));

        jTablaUbicaciones = new JTable();
        jTablaUbicaciones.setRowHeight(25);
        jTablaUbicaciones.getTableHeader().setBackground(COLOR_AZUL);
        jTablaUbicaciones.getTableHeader().setForeground(Color.WHITE);
        jTablaUbicaciones.getTableHeader().setFont(FONT_BOLD);

        JScrollPane scroll = new JScrollPane(jTablaUbicaciones);
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
        btn.addActionListener(this::jButton6ActionPerformed); // Volver
        return btn;
    }

    private JButton crearBotonAccion(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        switch (texto) {
            case "Registrar": btn.addActionListener(this::bCrearUbicacionActionPerformed); break;
            case "Consultar": btn.addActionListener(this::bConsultarUbicacionActionPerformed); break;
            case "Modificar": btn.addActionListener(this::bModificarUbicacionActionPerformed); break;
            case "Ver Todo": btn.addActionListener(this::bVerTodoActionPerformed); break;
            case "Deshabilitar": btn.addActionListener(this::bDeshabilitarUbicaciónActionPerformed); break;
            case "Exportar": btn.addActionListener(this::bExportarActionPerformed); break;
        }
        return btn;
    }

    // ========================================================================
    // === LÓGICA DE NEGOCIO Y DATOS ===
    // ========================================================================

    private void inicializarTabla() {
        String[] headers = new String[] { 
            "ID", "Nombre", "Capacidad (m³)", "Cap. Restante (m³)", "Descripción", "Deshabilitado" 
        };
        
        modeloTabla = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Boolean.class; 
                return super.getColumnClass(columnIndex);
            }
        };
        jTablaUbicaciones.setModel(modeloTabla);

        try {
            jTablaUbicaciones.getColumnModel().getColumn(0).setMinWidth(0);
            jTablaUbicaciones.getColumnModel().getColumn(0).setMaxWidth(0);
            jTablaUbicaciones.getColumnModel().getColumn(0).setWidth(0);
            jTablaUbicaciones.getColumnModel().getColumn(5).setPreferredWidth(80);
        } catch (Exception ignored) {}

        jTablaUbicaciones.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = jTablaUbicaciones.getSelectedRow();
                    if (fila >= 0) cargarFormularioDesdeTabla(fila);
                }
            }
        });
    }

    private void cargarFormularioDesdeTabla(int fila) {
        try {
            Object idObj = modeloTabla.getValueAt(fila, 0);
            if (idObj == null) return;
            int idUbicacion = Integer.parseInt(idObj.toString());

            Ubicacion ubicacion = controlador.obtenerPorId(idUbicacion);

            if (ubicacion != null) {
                jFTNombre.setText(ubicacion.getNombre());
                jFTDescripcion.setText(ubicacion.getDescripcion());
                jFTAltura.setText(String.valueOf(ubicacion.getAltura()));
                jFTAnchura.setText(String.valueOf(ubicacion.getAnchura())); 
                jFTProfundidad.setText(String.valueOf(ubicacion.getProfundidad()));
            } else {
                 JOptionPane.showMessageDialog(this, "No se encontró la ubicación completa.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar formulario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Ubicacion> lista = controlador.obtenerTodasUbicaciones();
            DecimalFormat df = new DecimalFormat("#.###");
            
            for (Ubicacion u : lista) {
                modeloTabla.addRow(new Object[] {
                    u.getId_ubicacion(), 
                    u.getNombre(),
                    df.format(u.getCapacidad()), 
                    df.format(u.getCapacidadRestante()),
                    u.getDescripcion(),
                    u.isDeshabilitado() 
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando ubicaciones: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Listeners Externos ---
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

    // --- Acciones de Botones ---

    private void bCrearUbicacionActionPerformed(ActionEvent evt) {                                                
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
            JOptionPane.showMessageDialog(this, "Error al crear: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bModificarUbicacionActionPerformed(ActionEvent evt) {                                                    
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
    }

    private void bConsultarUbicacionActionPerformed(ActionEvent evt) {                                                    
        try {
            String nombre = safeGet(jFTNombre);
            modeloTabla.setRowCount(0);
            List<Ubicacion> lista;
            
            if (nombre.isEmpty()) {
                lista = controlador.obtenerTodasUbicaciones();
            } else {
                lista = controlador.buscarPorNombre(nombre);
            }

            if (lista == null || lista.isEmpty()) {
                if (!nombre.isEmpty()) { 
                    JOptionPane.showMessageDialog(this, "No se encontraron ubicaciones con ese nombre.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                }
                return; 
            }

            DecimalFormat df = new DecimalFormat("#.###");
            for (Ubicacion u : lista) {
                modeloTabla.addRow(new Object[]{
                    u.getId_ubicacion(), u.getNombre(), df.format(u.getCapacidad()), 
                    df.format(u.getCapacidadRestante()), u.getDescripcion(), u.isDeshabilitado()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bDeshabilitarUbicaciónActionPerformed(ActionEvent evt) {                                                       
        int fila = jTablaUbicaciones.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una ubicación.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = parseIntSafe(String.valueOf(modeloTabla.getValueAt(fila, 0)));
        String nombre = String.valueOf(modeloTabla.getValueAt(fila, 1));
        boolean estadoActual = (boolean) modeloTabla.getValueAt(fila, 5);
        boolean nuevoEstado = !estadoActual;
        String accion = nuevoEstado ? "DESHABILITAR" : "HABILITAR";
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Desea " + accion + " la ubicación \"" + nombre + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (controlador.actualizarEstadoDeshabilitado(id, nuevoEstado)) {
                modeloTabla.setValueAt(nuevoEstado, fila, 5);
                JOptionPane.showMessageDialog(this, "Ubicación " + accion.toLowerCase() + " correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo cambiar el estado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bVerTodoActionPerformed(ActionEvent evt) {                                         
        try {
            jFTNombre.setText(""); 
            limpiarCampos(); 
            cargarTabla();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bExportarActionPerformed(ActionEvent evt) {                                          
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Ubicaciones");
        fileChooser.setSelectedFile(new File("Reporte_Ubicaciones.pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }
            
            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();
                
                Paragraph title = new Paragraph("Reporte de Ubicaciones", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK));
                title.setAlignment(Paragraph.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);
                
                int colCount = jTablaUbicaciones.getColumnCount();
                int visibleColCount = colCount - 1; 
                PdfPTable pdfTable = new PdfPTable(visibleColCount);
                pdfTable.setWidthPercentage(100);
                
                // ⭐ CORRECCIÓN: Uso de com.itextpdf.text.Font explícito para evitar conflicto
                com.itextpdf.text.Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
                BaseColor headerColor = new BaseColor(46, 204, 113);
                
                for (int i = 1; i < colCount; i++) { 
                    PdfPCell cell = new PdfPCell(new Phrase(jTablaUbicaciones.getColumnName(i), fontHeader));
                    cell.setBackgroundColor(headerColor);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }
                
                com.itextpdf.text.Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
                for (int rows = 0; rows < jTablaUbicaciones.getRowCount(); rows++) {
                    for (int cols = 1; cols < colCount; cols++) { 
                        Object value = jTablaUbicaciones.getValueAt(rows, cols);
                        String text = (value instanceof Boolean && (Boolean) value) ? "Sí" : (value instanceof Boolean) ? "No" : (value != null ? value.toString() : "");
                        PdfPCell cell = new PdfPCell(new Phrase(text, fontData));
                        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                        pdfTable.addCell(cell);
                    }
                }
                document.add(pdfTable);
                JOptionPane.showMessageDialog(this, "Reporte exportado exitosamente.");
            } catch (DocumentException | FileNotFoundException ex) {
                Logger.getLogger(PanelUbicacion.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
            } finally {
                if (document.isOpen()) document.close();
            }
        }
    }

    private void jButton6ActionPerformed(ActionEvent evt) {                                         
        this.dispose();
        new PrincipalVista(this.usuarioActual).setVisible(true);
    }

    // --- Utilidades ---
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
        try { return (s == null || s.trim().isEmpty()) ? 0 : Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private double parseDoubleSafe(JFormattedTextField f) {
        return parseDoubleSafe(safeGet(f));
    }
    private double parseDoubleSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0.0 : Double.parseDouble(s.trim().replace(",", ".")); } catch (Exception e) { return 0.0; }
    }

    public java.util.List<String> getNombresUbicaciones() {
        try {
            List<String> nombres = new ArrayList<>();
            for (Ubicacion u : dao.listar()) nombres.add(u.getNombre());
            return nombres;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}