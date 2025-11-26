package Vista;

import Modelo.Articulo;
import Controlador.ArticuloControlador;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PanelArticulos extends JFrame {

    // --- Componentes de la UI ---
    private JFormattedTextField jFTextNombre;
    private JFormattedTextField jFTextCodigoBienNacional;
    private JComboBox<String> jCBoxCategoria;
    private JFormattedTextField jFTextDetalles;
    private JFormattedTextField jFTextAltura;
    private JFormattedTextField jFAnchura;
    private JFormattedTextField jFTextProfundidad;
    private JCheckBox jCheckBoxBienNacional;
    private JLabel jLabelBienNacional; // Etiqueta dinámica
    private JTable jTable1;
    private DefaultTableModel modeloTabla;
    
    // Botones
    private JButton bCrear, bModificar, bConsultar, bVerTodo, bDeshabilitar, bExportar, bSalir;

    // --- Variables de Control ---
    private final ArticuloControlador controlador;
    private Usuario usuarioActual;
    private final Color COLOR_AZUL = new Color(13, 51, 131); // Color institucional

    public PanelArticulos(Usuario usuario) {
        this.usuarioActual = usuario;
        this.controlador = new ArticuloControlador();
        
        // Configuración básica de la ventana
        setTitle("Gestión de Bienes Nacionales y Artículos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700); // Tamaño inicial más amplio
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600)); // Evitar que se haga muy pequeña
        
        // Inicializar Componentes Gráficos
        initUI();
        
        // Configuración de Filtros y Listeners lógicos
        configurarLogica();
        
        // Permisos de usuario
        if (usuarioActual == null || !"Administrador".equalsIgnoreCase(usuarioActual.getRol())) {
            bDeshabilitar.setVisible(false);
            bCrear.setVisible(false);
            bModificar.setVisible(false);
        }

        cargarTabla();
    }

    // Constructor vacío por compatibilidad
    public PanelArticulos() {
        this(null);
    }

    private void initUI() {
        // Panel Principal con BorderLayout para que se adapte a la pantalla
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // ==========================================
        // 1. HEADER (NORTE)
        // ==========================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_AZUL);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(new EmptyBorder(0, 20, 0, 20)); // Margen interno

        JLabel lblTitulo = new JLabel("Gestión de Bienes Nacionales y Artículos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        bSalir = crearBotonHeader("Volver");
        
        headerPanel.add(lblTitulo, BorderLayout.CENTER);
        headerPanel.add(bSalir, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. CONTENIDO CENTRAL (CENTRO)
        // ==========================================
        // Usamos GridBagLayout para dividir: Formulario (Izq) y Tabla (Der)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Panel Formulario (Izquierda) ---
        JPanel formPanel = crearPanelFormulario();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3; // Ocupa el 30% del ancho aprox
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 10);
        centerPanel.add(formPanel, gbc);

        // --- Panel Tabla (Derecha) ---
        JPanel tablePanel = crearPanelTabla();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7; // Ocupa el 70% del ancho (se estira más)
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
        
        bCrear = crearBotonAccion("Registrar");
        bModificar = crearBotonAccion("Modificar");
        bConsultar = crearBotonAccion("Consultar");
        bVerTodo = crearBotonAccion("Ver Todo");
        bDeshabilitar = crearBotonAccion("Deshabilitar");
        bExportar = crearBotonAccion("Exportar a PDF");

        footerPanel.add(bCrear);
        footerPanel.add(bModificar);
        footerPanel.add(bConsultar);
        footerPanel.add(bVerTodo);
        footerPanel.add(bDeshabilitar);
        footerPanel.add(bExportar);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_AZUL), "Datos del Artículo"));
        
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        
        // Inicialización de campos
        jFTextNombre = new JFormattedTextField();
        jFTextDetalles = new JFormattedTextField();
        jCheckBoxBienNacional = new JCheckBox("Marcar si es un Bien Nacional");
        jCheckBoxBienNacional.setBackground(Color.WHITE);
        jLabelBienNacional = new JLabel("Código Bien Nacional:");
        jFTextCodigoBienNacional = new JFormattedTextField();
        jCBoxCategoria = new JComboBox<>(new String[] { "Mobiliario", "Papelería", "Tecnología", "Limpieza", "Electrodomésticos", "Otros" });
        jFTextAltura = new JFormattedTextField();
        jFAnchura = new JFormattedTextField();
        jFTextProfundidad = new JFormattedTextField();

        // Fila 0: Nombre
        addLabelAndField(p, "Nombre:", jFTextNombre, 0, g);
        
        // Fila 1: Detalles
        addLabelAndField(p, "Detalles:", jFTextDetalles, 1, g);
        
        // Fila 2: Checkbox
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        p.add(jCheckBoxBienNacional, g);
        g.gridwidth = 1; // reset

        // Fila 3: Código (Inicialmente oculto por lógica, pero añadido al layout)
        addLabelAndField(p, jLabelBienNacional, jFTextCodigoBienNacional, 3, g);

        // Fila 4: Categoría
        addLabelAndField(p, "Categoría:", jCBoxCategoria, 4, g);
        
        // Separador visual
        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        p.add(new JSeparator(), g);
        g.gridwidth = 1;

        // Fila 6: Altura
        addLabelAndField(p, "Altura (m):", jFTextAltura, 6, g);
        
        // Fila 7: Anchura
        addLabelAndField(p, "Anchura (m):", jFAnchura, 7, g);
        
        // Fila 8: Profundidad
        addLabelAndField(p, "Profundidad (m):", jFTextProfundidad, 8, g);
        
        // Espaciador vertical para empujar todo hacia arriba
        g.gridy = 9; g.weighty = 1.0;
        p.add(new JLabel(), g);

        return p;
    }

    private void addLabelAndField(JPanel p, Object label, JComponent field, int row, GridBagConstraints g) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        if (label instanceof String) p.add(new JLabel((String)label), g);
        else p.add((Component)label, g);
        
        g.gridx = 1; g.weightx = 1.0;
        field.setPreferredSize(new Dimension(150, 25));
        p.add(field, g);
    }

    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        
        String[] headers = new String[]{
            "ID", "Nombre", "Cod. Bien", "Categoría", "Detalles", "Volumen (m³)", "Deshabilitado"
        };
        
        modeloTabla = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            
            // CORRECTO: Esta configuración asegura que la columna 6 se muestre como CheckBox.
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) return Boolean.class;
                if (columnIndex == 5) return Double.class;
                return super.getColumnClass(columnIndex);
            }
        };
        
        jTable1 = new JTable(modeloTabla);
        jTable1.setRowHeight(25);
        jTable1.getTableHeader().setBackground(COLOR_AZUL);
        jTable1.getTableHeader().setForeground(Color.WHITE);
        jTable1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Ocultar ID
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scroll = new JScrollPane(jTable1);
        p.add(scroll, BorderLayout.CENTER);
        
        return p;
    }

    private JButton crearBotonHeader(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Black", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Listener
        btn.addActionListener(this::bSalirActionPerformed);
        return btn;
    }

    private JButton crearBotonAccion(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Asignar listeners según el texto (para mantener tu lógica original)
        switch (texto) {
            case "Registrar": btn.addActionListener(this::bCrearActionPerformed); break;
            case "Modificar": btn.addActionListener(this::bModificarActionPerformed); break;
            case "Consultar": btn.addActionListener(this::bConsultarActionPerformed); break;
            case "Ver Todo": btn.addActionListener(this::bVerTodoActionPerformed); break;
            case "Deshabilitar": btn.addActionListener(this::bDeshabilitarActionPerformed); break;
            case "Exportar a PDF": btn.addActionListener(this::bExportarActionPerformed); break;
        }
        return btn;
    }

    private void configurarLogica() {
        // 1. Visibilidad inicial
        jLabelBienNacional.setVisible(false);
        jFTextCodigoBienNacional.setVisible(false);

        // 2. Listener Checkbox
        jCheckBoxBienNacional.addItemListener(e -> {
            boolean isChecked = e.getStateChange() == ItemEvent.SELECTED;
            jLabelBienNacional.setVisible(isChecked);
            jFTextCodigoBienNacional.setVisible(isChecked);
            if (!isChecked) jFTextCodigoBienNacional.setText("");
        });

        // 3. Filtros de Texto
        AbstractDocument docCodigo = (AbstractDocument) jFTextCodigoBienNacional.getDocument();
        docCodigo.setDocumentFilter(new SoloNumerosLongitudFijaFilter(8));

        AbstractDocument docNombre = (AbstractDocument) jFTextNombre.getDocument();
        docNombre.setDocumentFilter(new SoloLetrasYEspaciosFilter());
        
        // 4. Selección Tabla
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = jTable1.getSelectedRow();
                if (fila >= 0) cargarFormularioDesdeTabla(fila);
            }
        });
    }

    // ========================================================================
    // === MÉTODOS LÓGICOS ===
    // ========================================================================

    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Articulo> lista = controlador.obtenerTodosArticulos();
            if (lista == null) return;

            for (Articulo art : lista) {
                double espacio = art.getAltura() * art.getAnchura() * art.getProfundidad();
                modeloTabla.addRow(new Object[]{
                    art.getIdArticulo(),
                    art.getNombre(),
                    art.getCodigoBienNacional(),
                    art.getCategoria(),
                    art.getDetalles(),
                    espacio,
                    art.isDeshabilitado()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar artículos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bCrearActionPerformed(ActionEvent evt) {
        try {
            String nombre = jFTextNombre.getText().trim();
            String codigo = jFTextCodigoBienNacional.getText().trim();
            String categoria = (String) jCBoxCategoria.getSelectedItem();
            String detalles = jFTextDetalles.getText().trim();
            double altura = parseDoubleSafe(jFTextAltura.getText());
            double anchura = parseDoubleSafe(jFAnchura.getText());
            double profundidad = parseDoubleSafe(jFTextProfundidad.getText());

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (jCheckBoxBienNacional.isSelected() && codigo.length() != 8) {
                JOptionPane.showMessageDialog(this, "El Código Bien Nacional debe tener exactamente 8 dígitos.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Articulo art = new Articulo();
            art.setNombre(nombre);
            art.setCodigoBienNacional(codigo);
            art.setCategoria(categoria);
            art.setDetalles(detalles);
            art.setAltura(altura);
            art.setAnchura(anchura);
            art.setProfundidad(profundidad);
            art.setEspacioUnitario(altura * anchura * profundidad);
            art.setDeshabilitado(false);

            boolean ok = controlador.crearArticulo(art, this.usuarioActual.getIdUsuario());

            if (ok) {
                JOptionPane.showMessageDialog(this, "Artículo registrado correctamente.");
                cargarTabla();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el artículo.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void bModificarActionPerformed(ActionEvent evt) {
        int fila = jTable1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un artículo para modificar.");
            return;
        }
        try {
            int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
            String nombre = jFTextNombre.getText().trim();
            String codigo = jFTextCodigoBienNacional.getText().trim();
            String categoria = (String) jCBoxCategoria.getSelectedItem();
            String detalles = jFTextDetalles.getText().trim();
            double altura = parseDoubleSafe(jFTextAltura.getText());
            double anchura = parseDoubleSafe(jFAnchura.getText());
            double profundidad = parseDoubleSafe(jFTextProfundidad.getText());

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Articulo art = new Articulo();
            art.setIdArticulo(id);
            art.setNombre(nombre);
            art.setCodigoBienNacional(codigo);
            art.setCategoria(categoria);
            art.setDetalles(detalles);
            art.setAltura(altura);
            art.setAnchura(anchura);
            art.setProfundidad(profundidad);
            art.setEspacioUnitario(altura * anchura * profundidad);

            boolean estadoActual = (Boolean) modeloTabla.getValueAt(fila, 6);
            art.setDeshabilitado(estadoActual);

            if (controlador.actualizarArticulo(art, this.usuarioActual.getIdUsuario())) {
                JOptionPane.showMessageDialog(this, "Artículo modificado correctamente.");
                cargarTabla();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo modificar el artículo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bDeshabilitarActionPerformed(ActionEvent evt) {
        int fila = jTable1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un artículo para cambiar su estado.");
            return;
        }

        try {
            int idArticulo = (int) modeloTabla.getValueAt(fila, 0);
            String nombre = modeloTabla.getValueAt(fila, 1).toString();
            boolean estadoActual = (boolean) modeloTabla.getValueAt(fila, 6); // Columna 6 es deshabilitado

            boolean nuevoEstado = !estadoActual;
            String accion = nuevoEstado ? "DESHABILITAR" : "HABILITAR";

            int confirm = JOptionPane.showConfirmDialog(this, "¿Desea " + accion + " el artículo: " + nombre + "?", "Confirmar Acción", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            if (controlador.actualizarEstadoDeshabilitado(idArticulo, nuevoEstado)) {
                cargarTabla(); // Recargamos para ver cambios (¡Ahora funciona el check visualmente!)
                JOptionPane.showMessageDialog(this, "Artículo " + accion.toLowerCase() + " correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al " + accion.toLowerCase() + " el artículo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cambiar el estado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bConsultarActionPerformed(ActionEvent evt) {
        try {
            // CORRECCIÓN APLICADA: Obtener texto directamente de los campos.
            String nombre = jFTextNombre.getText().trim(); 
            String codigoBien = jFTextCodigoBienNacional.getText().trim();
            
            String categoria = "";
            if (jCBoxCategoria.getSelectedItem() != null) {
                categoria = jCBoxCategoria.getSelectedItem().toString().trim();
            }

            // 1. Limpiar la tabla antes de cargar nuevos resultados
            modeloTabla.setRowCount(0); 
            
            // 2. Ejecutar la búsqueda combinada
            List<Articulo> lista = controlador.buscarArticulosCombinado(nombre, codigoBien, categoria);

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron artículos con esos criterios.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }

            // 3. Llenar la tabla con los resultados
            for (Articulo art : lista) {
                // Cálculo del espacio unitario (Altura * Anchura * Profundidad)
                double espacio = art.getAltura() * art.getAnchura() * art.getProfundidad(); 
                
                modeloTabla.addRow(new Object[]{
                    art.getIdArticulo(), art.getNombre(), art.getCodigoBienNacional(),
                    art.getCategoria(), art.getDetalles(), espacio, art.isDeshabilitado()
                });
            }
        } catch (Exception ex) {
            // Manejo de errores
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bVerTodoActionPerformed(ActionEvent evt) {
        try {
            limpiarFormulario();
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Tabla actualizada.", "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bExportarActionPerformed(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Artículos");
        fileChooser.setSelectedFile(new File("Reporte_Articulos.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                com.itextpdf.text.Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph title = new Paragraph("Reporte de Artículos", fontTitle);
                title.setAlignment(Paragraph.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                int colCount = jTable1.getColumnCount();
                int visibleColCount = colCount - 1; // Excluir ID
                PdfPTable pdfTable = new PdfPTable(visibleColCount);
                pdfTable.setWidthPercentage(100);

                com.itextpdf.text.Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
                BaseColor headerColor = new BaseColor(13, 51, 131); // Azul Institucional

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
                        String text = (value instanceof Boolean && (Boolean) value) ? "Sí" : (value instanceof Boolean) ? "No" : (value != null ? value.toString() : "");
                        PdfPCell cell = new PdfPCell(new Phrase(text, fontData));
                        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                        pdfTable.addCell(cell);
                    }
                }

                document.add(pdfTable);
                JOptionPane.showMessageDialog(this, "Reporte exportado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (DocumentException | FileNotFoundException ex) {
                Logger.getLogger(PanelArticulos.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
            } finally {
                if (document.isOpen()) document.close();
            }
        }
    }

    private void bSalirActionPerformed(ActionEvent evt) {
        this.dispose();
        // NOTA: Asumiendo que 'PrincipalVista' existe y recibe un usuario
        new PrincipalVista(this.usuarioActual).setVisible(true);
    }

    // ==========================================
    // === UTILIDADES Y FILTROS ===
    // ==========================================

    private void limpiarFormulario() {
        jFTextNombre.setText("");
        jFTextCodigoBienNacional.setText("");
        jCBoxCategoria.setSelectedIndex(0);
        jFTextDetalles.setText("");
        jFTextAltura.setText("");
        jFAnchura.setText("");
        jFTextProfundidad.setText("");
        jCheckBoxBienNacional.setSelected(false);
    }

    private void cargarFormularioDesdeTabla(int fila) {
        try {
            int idArticulo = (Integer) modeloTabla.getValueAt(fila, 0);
            Articulo art = controlador.obtenerArticuloPorId(idArticulo);

            if (art != null) {
                jFTextNombre.setText(art.getNombre());
                jFTextCodigoBienNacional.setText(art.getCodigoBienNacional());
                jCBoxCategoria.setSelectedItem(art.getCategoria());
                jFTextDetalles.setText(art.getDetalles());
                jFTextAltura.setText(String.valueOf(art.getAltura()));
                jFAnchura.setText(String.valueOf(art.getAnchura()));
                jFTextProfundidad.setText(String.valueOf(art.getProfundidad()));
                
                // Lógica del checkbox basada en si hay código
                if (art.getCodigoBienNacional() != null && !art.getCodigoBienNacional().isEmpty()) {
                    jCheckBoxBienNacional.setSelected(true);
                } else {
                    jCheckBoxBienNacional.setSelected(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // NOTA: Ya no se usa en bConsultar, pero se deja por si otras partes lo usan
    private String safeGet(JFormattedTextField f) {
        try { return (f.getText() == null) ? "" : f.getText().trim(); } catch (Exception e) { return ""; }
    }

    private double parseDoubleSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0.0 : Double.parseDouble(s.trim().replace(",", ".")); }
        catch (Exception e) { return 0.0; }
    }

    // --- Filtros Internos Static ---
    private static class SoloNumerosLongitudFijaFilter extends DocumentFilter {
        private final int maxLength;
        public SoloNumerosLongitudFijaFilter(int maxLength) { this.maxLength = maxLength; }
        
        private String keepDigits(String text) { return text == null ? "" : text.replaceAll("\\D+", ""); }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            String filtered = keepDigits(string);
            if (filtered.isEmpty()) return;
            int available = maxLength - fb.getDocument().getLength();
            if (available <= 0) return;
            if (filtered.length() > available) filtered = filtered.substring(0, available);
            super.insertString(fb, offset, filtered, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            String filtered = keepDigits(text);
            int currentLen = fb.getDocument().getLength();
            int newLen = currentLen - length + filtered.length();
            if (newLen > maxLength) {
                int allowed = maxLength - (currentLen - length);
                if (allowed <= 0) filtered = "";
                else if (filtered.length() > allowed) filtered = filtered.substring(0, allowed);
            }
            if (filtered.isEmpty()) { if (length > 0) super.replace(fb, offset, length, "", attrs); return; }
            super.replace(fb, offset, length, filtered, attrs);
        }
    }

    private static class SoloLetrasYEspaciosFilter extends DocumentFilter {
        private String removeDigits(String text) { return text == null ? "" : text.replaceAll("\\d+", ""); }
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            String filtered = removeDigits(string);
            if (!filtered.isEmpty()) super.insertString(fb, offset, filtered, attr);
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            String filtered = removeDigits(text);
            super.replace(fb, offset, length, filtered, attrs);
        }
    }
}
