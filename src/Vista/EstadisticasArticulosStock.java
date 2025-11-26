package Vista;

import Modelo.DatoGrafico;
import Modelo.EstadisticasDAO;
import Modelo.Usuario;
import util.GeneradorGraficos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import org.jfree.chart.ChartPanel;

public class EstadisticasArticulosStock extends JFrame {

    private final EstadisticasDAO dao = new EstadisticasDAO();
    private final GeneradorGraficos generador = new GeneradorGraficos();
    private Usuario usuarioActual;
    
    // Componentes UI
    private JPanel pnlGraficoContainer; // El panel dentro del Scroll
    private ChartPanel panelGraficoActual; // El gráfico JFreeChart
    private JButton bCargar, bExportar, bVolver;

    // Constantes de Estilo
    private final Color COLOR_AZUL = new Color(13, 51, 131);
    private final Font FONT_TITLE = new Font("Segoe UI Black", Font.BOLD, 18);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public EstadisticasArticulosStock(Usuario usuario) {
        this.usuarioActual = usuario;
        
        // Configuración de la Ventana
        setTitle("Estadísticas de Stock de Artículos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null); // Centrar

        initUI();
    }

    // Constructor vacío
    public EstadisticasArticulosStock() {
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

        JLabel lblTitle = new JLabel("Estadísticas: Cantidad de Bienes Mobiliarios", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        bVolver = createHeaderButton("Volver");

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(bVolver, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. ZONA DE GRÁFICO CON SCROLL (CENTRO)
        // ==========================================
        // Creamos un panel contenedor que irá DENTRO del ScrollPane.
        // Este panel crecerá dinámicamente según la altura del gráfico.
        pnlGraficoContainer = new JPanel(new BorderLayout());
        pnlGraficoContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(pnlGraficoContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll más rápido
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // 3. FOOTER CON CONTROLES (SUR)
        // ==========================================
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        footerPanel.setBackground(Color.WHITE);

        bCargar = createActionButton("Cargar Gráfico");
        bExportar = createActionButton("Exportar a PDF");

        footerPanel.add(bCargar);
        footerPanel.add(bExportar);

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
        btn.addActionListener(e -> bVolverActionPerformed());
        return btn;
    }

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (text.equals("Cargar Gráfico")) {
            btn.addActionListener(e -> cargarGrafico());
        } else {
            btn.addActionListener(e -> exportarGrafico());
        }
        return btn;
    }

    // ==========================================
    // === LÓGICA DEL GRÁFICO ===
    // ==========================================

    private void cargarGrafico() {
        try {
            // 1. Obtener datos
            List<DatoGrafico> datos = dao.obtenerTopStock(9999, true);

            if (datos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay datos para mostrar.");
                return;
            }

            // 2. Calcular Altura Dinámica
            final int ALTURA_ESTANDAR = 400;
            final int PIXELES_POR_BARRA = 30;
            int alturaCalculada = Math.max(ALTURA_ESTANDAR, (datos.size() * PIXELES_POR_BARRA) + 100);

            // 3. Generar Gráfico
            panelGraficoActual = generador.crearGraficoBarras(datos, "Existencia Actual por Artículo");
            
            // 4. Configurar Tamaño
            // IMPORTANTE: Seteamos el tamaño preferido del ChartPanel para forzar al JScrollPane a mostrar barra
            panelGraficoActual.setPreferredSize(new Dimension(pnlGraficoContainer.getWidth() - 20, alturaCalculada));

            // 5. Agregar al contenedor
            pnlGraficoContainer.removeAll();
            pnlGraficoContainer.add(panelGraficoActual, BorderLayout.CENTER);
            
            // 6. Refrescar UI
            pnlGraficoContainer.revalidate();
            pnlGraficoContainer.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar gráfico: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarGrafico() {
        if (panelGraficoActual == null) {
            JOptionPane.showMessageDialog(this, "Primero carga un gráfico para poder exportarlo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser ch = new JFileChooser();
        ch.setDialogTitle("Guardar Gráfico como PDF");
        ch.setSelectedFile(new File("Reporte_Stock_Grafico.pdf"));

        if (ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File archivo = ch.getSelectedFile();
                if (!archivo.getName().endsWith(".pdf")) {
                    archivo = new File(archivo.getAbsolutePath() + ".pdf");
                }

                generador.exportarPDF(panelGraficoActual, archivo);
                JOptionPane.showMessageDialog(this, "PDF guardado exitosamente.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exportando: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void bVolverActionPerformed() {
        this.dispose();
        new PanelEstadistica(this.usuarioActual).setVisible(true);
    }
}