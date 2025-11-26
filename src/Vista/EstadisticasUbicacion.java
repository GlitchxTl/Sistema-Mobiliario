package Vista;

import Controlador.EstadisticaControlador;
import Modelo.Usuario;
import Modelo.ValorUbicacionDTO;
import util.GeneradorGraficos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import org.jfree.chart.ChartPanel;

public class EstadisticasUbicacion extends JFrame {

    private final EstadisticaControlador control = new EstadisticaControlador();
    private final GeneradorGraficos generador = new GeneradorGraficos();
    private Usuario usuarioActual;
    
    // Componentes UI
    private JPanel pnlGraficoContainer; // Panel dentro del scroll
    private ChartPanel panelGraficoActual; // El gráfico
    private JButton bCargar, bExportar, bVolver;

    // Constantes de Estilo
    private final Color COLOR_AZUL = new Color(13, 51, 131);
    private final Font FONT_TITLE = new Font("Segoe UI Black", Font.BOLD, 18);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public EstadisticasUbicacion(Usuario usuario) {
        this.usuarioActual = usuario;
        
        // Configuración de Ventana
        setTitle("Estadísticas: Valor por Ubicación");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        initUI();
    }

    // Constructor vacío
    public EstadisticasUbicacion() {
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

        JLabel lblTitle = new JLabel("Estadísticas: Valor Total del Mobiliario por Ubicación", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        bVolver = createHeaderButton("Volver");

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(bVolver, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. ZONA GRÁFICO CON SCROLL (CENTRO)
        // ==========================================
        pnlGraficoContainer = new JPanel(new BorderLayout());
        pnlGraficoContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(pnlGraficoContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        // Barras de desplazamiento solo si son necesarias
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
            // 1. Obtener datos desde el controlador
            List<ValorUbicacionDTO> datosDTO = control.obtenerValorPorUbicacion();

            if (datosDTO == null || datosDTO.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay inventario valorado para mostrar.");
                return;
            }

            // 2. Mapear datos para el generador
            LinkedHashMap<String, Double> mapDatos = new LinkedHashMap<>();
            for (ValorUbicacionDTO dto : datosDTO) {
                mapDatos.put(dto.getNombreUbicacion(), dto.getValorTotal());
            }

            // 3. Calcular Altura Dinámica
            final int ALTURA_ESTANDAR = 400;
            final int PIXELES_POR_BARRA = 40; // Un poco más alto por ser valores monetarios
            int alturaCalculada = Math.max(ALTURA_ESTANDAR, (mapDatos.size() * PIXELES_POR_BARRA) + 100);

            // 4. Generar Gráfico
            panelGraficoActual = generador.crearGraficoValorPorUbicacion(mapDatos, "Valor Total por Ubicación (Bs)");
            
            // 5. Configurar Tamaño preferido para el ScrollPane
            panelGraficoActual.setPreferredSize(new Dimension(pnlGraficoContainer.getWidth() - 20, alturaCalculada));

            // 6. Mostrar
            pnlGraficoContainer.removeAll();
            pnlGraficoContainer.add(panelGraficoActual, BorderLayout.CENTER);
            
            // 7. Refrescar
            pnlGraficoContainer.revalidate();
            pnlGraficoContainer.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar gráfico: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarGrafico() {
        if (panelGraficoActual == null) {
            JOptionPane.showMessageDialog(this, "Primero carga un gráfico.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser ch = new JFileChooser();
        ch.setDialogTitle("Guardar Gráfico como PDF");
        ch.setSelectedFile(new File("Reporte_Valor_Ubicacion.pdf"));

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
