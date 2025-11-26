package Vista;

import Modelo.LogAuditoriaSistema;
import Modelo.ReporteDAO;
import Modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class VistaLogAuditoria extends JFrame {

    private final ReporteDAO dao = new ReporteDAO();
    private Usuario usuarioActual;
    
    // Componentes UI
    private JTable tblLogAuditoria;
    private DefaultTableModel modeloTabla;
    private JButton bVolver, bCargarTabla;

    // Constantes de Estilo
    private final Color COLOR_AZUL = new Color(13, 51, 131);
    private final Font FONT_TITLE = new Font("Segoe UI Black", Font.BOLD, 18);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public VistaLogAuditoria(Usuario usuario) {
        this.usuarioActual = usuario;
        
        // Configuración de la ventana
        setTitle("Reporte de Auditoría de Transacciones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600); // Tamaño inicial más amplio para ver bien la tabla
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        
        initUI();
        
        // Opcional: Cargar datos automáticamente al abrir (si lo deseas, descomenta la línea abajo)
        // cargarTablaAuditoria(); 
    }

    // Constructor vacío
    public VistaLogAuditoria() {
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

        JLabel lblTitle = new JLabel("Auditoría del Sistema", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        bVolver = createHeaderButton("Volver");

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(bVolver, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. TABLA (CENTRO)
        // ==========================================
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new EmptyBorder(20, 20, 0, 20));
        tablePanel.setBackground(Color.WHITE);

        String[] columnas = {"Fecha/Hora", "Usuario", "Acción", "Recurso", "ID Reg.", "Detalle"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblLogAuditoria = new JTable(modeloTabla);
        tblLogAuditoria.setRowHeight(25);
        tblLogAuditoria.getTableHeader().setBackground(COLOR_AZUL);
        tblLogAuditoria.getTableHeader().setForeground(Color.WHITE);
        tblLogAuditoria.getTableHeader().setFont(FONT_BOLD);
        
        JScrollPane scrollPane = new JScrollPane(tblLogAuditoria);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // ==========================================
        // 3. FOOTER (SUR)
        // ==========================================
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        footerPanel.setBackground(Color.WHITE);
        
        bCargarTabla = createActionButton("Cargar Tabla Auditoría");
        footerPanel.add(bCargarTabla);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createHeaderButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this::bVolverActionPerformed);
        return btn;
    }

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(250, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this::cargarTablaAuditoriaActionPerformed);
        return btn;
    }

    // ==========================================
    // === LÓGICA DE NEGOCIO ===
    // ==========================================

    private void cargarTablaAuditoriaActionPerformed(ActionEvent evt) {
        cargarTablaAuditoria();
    }

    private void cargarTablaAuditoria() {
        try {
            List<LogAuditoriaSistema> log = dao.obtenerLogAuditoriaSistema();
            modeloTabla.setRowCount(0); // Limpiar tabla

            if (log.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron registros de auditoría.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (LogAuditoriaSistema item : log) {
                modeloTabla.addRow(new Object[]{
                    item.getFechaHora(),
                    item.getUsuario(),
                    item.getAccionRealizada(),
                    item.getRecursoAfectado(),
                    item.getIdRegistroAfectado(),
                    item.getDetalle()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar auditoría: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bVolverActionPerformed(ActionEvent evt) {
        this.dispose();
        new PanelReportes(this.usuarioActual).setVisible(true);
    }
}
