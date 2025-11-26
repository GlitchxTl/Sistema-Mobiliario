package Vista;

import Modelo.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PanelReportes extends JFrame {

    private Usuario usuarioActual;
    
    // Constantes de Estilo
    private final Color COLOR_AZUL = new Color(13, 51, 131);
    private final Font FONT_TITLE = new Font("Segoe UI Black", Font.BOLD, 24);
    private final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 18);

    // Componentes
    private JButton bAuditoría;
    private JButton bVistaReporteGastos;
    private JButton bVolver;

    public PanelReportes(Usuario usuario) {
        this.usuarioActual = usuario;
        
        // Configuración de la Ventana
        setTitle("Menú de Reportes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500); // Tamaño inicial razonable
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        initUI();
    }

    // Constructor vacío para compatibilidad
    public PanelReportes() {
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
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80)); // Un poco más alto para el título grande
        headerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel lblTitulo = new JLabel("Reportes", SwingConstants.CENTER);
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(Color.WHITE);

        bVolver = crearBotonHeader("Volver");

        headerPanel.add(lblTitulo, BorderLayout.CENTER);
        headerPanel.add(bVolver, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. CONTENIDO CENTRAL (BOTONES DE MENÚ)
        // ==========================================
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Espacio entre botones
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;

        // Botón Auditoría
        bAuditoría = crearBotonMenu("Auditoría");
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(bAuditoría, gbc);

        // Botón Reporte Gastos
        bVistaReporteGastos = crearBotonMenu("Reportes Gastos Operativos");
        gbc.gridx = 1;
        gbc.gridy = 0;
        centerPanel.add(bVistaReporteGastos, gbc);

        // Contenedor intermedio para controlar el tamaño máximo de los botones
        // Esto evita que los botones se hagan gigantes en pantalla completa
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(centerPanel); // Agregamos el panel de botones al wrapper centrado

        mainPanel.add(wrapperPanel, BorderLayout.CENTER);
    }

    private JButton crearBotonHeader(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this::bVolverActionPerformed);
        return btn;
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        // Tamaño preferido grande para los botones del menú
        btn.setPreferredSize(new Dimension(300, 100)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (texto.equals("Auditoría")) {
            btn.addActionListener(this::bAuditoríaActionPerformed);
        } else {
            btn.addActionListener(this::bVistaReporteGastosActionPerformed);
        }
        
        return btn;
    }

    // ==========================================
    // === ACCIONES ===
    // ==========================================

    private void bAuditoríaActionPerformed(ActionEvent evt) {
        VistaLogAuditoria panelAuditoria = new VistaLogAuditoria(this.usuarioActual);
        panelAuditoria.setVisible(true);
        this.dispose();
    }

    private void bVistaReporteGastosActionPerformed(ActionEvent evt) {
        VistaReporteGastos panelGastos = new VistaReporteGastos(this.usuarioActual);
        panelGastos.setVisible(true);
        this.dispose();
    }

    private void bVolverActionPerformed(ActionEvent evt) {
        this.dispose();
        new PrincipalVista(this.usuarioActual).setVisible(true);
    }
}
