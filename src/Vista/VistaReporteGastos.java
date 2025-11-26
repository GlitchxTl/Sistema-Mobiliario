package Vista;

import Modelo.GastoOperativoDAO;
import Modelo.GastoOperativo;
import Modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class VistaReporteGastos extends JFrame {

    // DAOs y Modelos
    private final GastoOperativoDAO gastoDAO = new GastoOperativoDAO();
    private Usuario usuarioActual;

    // Componentes (Declaración, imitando las variables de tu código original)
    private JPanel jPanel1;        // Contenedor principal (fondo blanco)
    private JPanel jPanel2;        // Encabezado (fondo azul)
    private JLabel jLabel1;        // Título del encabezado
    private JButton bVolver;       // Botón Volver
    private JScrollPane jScrollPane1; // Scroll para la tabla
    private JTable tblReporteGastos; // La tabla de datos
    private JButton cargarTablaReporte; // Botón Cargar Reporte
    
    // Colores usados en tu vista
    private static final Color COLOR_AZUL_OSCURO = new Color(13, 51, 131);
    private static final Color COLOR_BLANCO = new Color(255, 255, 255);

    // -------------------------------------------------------------
    // --- Constructor ---
    // -------------------------------------------------------------
    
    public VistaReporteGastos(Usuario usuario) {
        this.usuarioActual = usuario;
        
        // El initComponents manual
        setupFrame();
        setupComponents();
        setupLayout();
        
        // Acción de cargar la tabla al iniciar (opcional)
        cargarTablaGastosActionPerformed(); 
    }
    
    // -------------------------------------------------------------
    // --- Inicialización y Diseño (Reemplaza initComponents) ---
    // -------------------------------------------------------------

    private void setupFrame() {
        setTitle("Reporte Mensual de Gastos Operativos (Agregado)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 🚨 CAMBIO CLAVE 1: Permitir redimensionar para maximizar
        setResizable(true); 
        
        // Establecer un tamaño inicial, aunque se va a maximizar
        setPreferredSize(new Dimension(1024, 768)); 
        
        // 🚨 CAMBIO CLAVE 2: Maximizar la ventana a pantalla completa
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void setupComponents() {
        // Inicialización de componentes
        jPanel1 = new JPanel(new BorderLayout());
        jPanel2 = new JPanel(new BorderLayout()); // Usamos BorderLayout para el encabezado
        jLabel1 = new JLabel("Reporte Mensual de Gastos Operativos");
        bVolver = new JButton("Volver");
        tblReporteGastos = new JTable();
        jScrollPane1 = new JScrollPane(tblReporteGastos);
        cargarTablaReporte = new JButton("Actualizar Reporte de Gastos");

        // --- Estilos y Propiedades del Encabezado (jPanel2) ---
        jPanel2.setBackground(COLOR_AZUL_OSCURO);

        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 22));
        jLabel1.setForeground(COLOR_BLANCO);
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setPreferredSize(new Dimension(500, 80)); // Altura ligeramente ajustada para encabezado
        
        bVolver.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bVolver.setBackground(COLOR_AZUL_OSCURO);
        bVolver.setForeground(COLOR_BLANCO);
        bVolver.setBorderPainted(false);
        bVolver.setFocusPainted(false);
        bVolver.setPreferredSize(new Dimension(100, 80)); // Para darle altura

        // --- Estilos y Propiedades del Contenido (jPanel1) ---
        jPanel1.setBackground(COLOR_BLANCO);
        
        // --- Estilos del Botón de Carga ---
        cargarTablaReporte.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cargarTablaReporte.setBackground(COLOR_AZUL_OSCURO);
        cargarTablaReporte.setForeground(COLOR_BLANCO);
        cargarTablaReporte.setPreferredSize(new Dimension(300, 50)); // Altura ligeramente ajustada

        // --- Listener para el Botón ---
        cargarTablaReporte.addActionListener(e -> cargarTablaGastosActionPerformed());
        bVolver.addActionListener(e -> bVolverActionPerformed());

        // 🚨 CAMBIO: Configuración Inicial de la Tabla para reflejar los datos agregados
        String[] columnasIniciales = {"Año", "Mes", "Total Gastado (BS/Divisa)", "# Transacciones"};
        tblReporteGastos.setModel(new DefaultTableModel(columnasIniciales, 0));
        tblReporteGastos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }
    
    private void setupLayout() {
        // --- 1. Layout del Encabezado (jPanel2) ---
        jPanel2.add(jLabel1, BorderLayout.CENTER);
        
        // Usar un panel auxiliar para alinear el botón a la derecha y mantener la altura
        JPanel panelVolver = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelVolver.setOpaque(false); // Necesario si queremos que el color de jPanel2 se vea
        panelVolver.add(bVolver);
        jPanel2.add(panelVolver, BorderLayout.EAST);
        
        // Panel para el botón de carga (centrado)
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10)); // Margen vertical
        panelBoton.setBackground(COLOR_BLANCO);
        panelBoton.add(cargarTablaReporte);

        // --- 2. Layout del Contenedor Principal (jPanel1) ---
        // Norte: Encabezado
        jPanel1.add(jPanel2, BorderLayout.NORTH);
        
        // Centro: Tabla de datos (ocupará todo el espacio restante)
        jPanel1.add(jScrollPane1, BorderLayout.CENTER);
        
        // Sur: Botón de carga
        jPanel1.add(panelBoton, BorderLayout.SOUTH);

        // 3. Agregar el panel al Frame
        setContentPane(jPanel1);
        pack();
    }
    
    // -------------------------------------------------------------
    // --- Lógica de Manejo de Eventos ---
    // -------------------------------------------------------------
    
    private void cargarTablaGastosActionPerformed() {
        try {
            // ⚠️ La lista ahora contiene objetos GastoOperativo que representan un REPORTE MENSUAL
            List<GastoOperativo> reportesMensuales = gastoDAO.obtenerGastosOperativos();

            // 🚨 CAMBIO: Columnas para el reporte mensual
            String[] columnas = {"Año", "Mes", "Total Gastado (BS/Divisa)", "# Transacciones"};
            
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                // Sobrescribir para evitar edición y mejorar tipos
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    // 🚨 CAMBIO: Clases de las columnas del reporte
                    if (columnIndex == 0) return Integer.class; // Año
                    if (columnIndex == 1) return Integer.class; // Mes
                    if (columnIndex == 2) return Double.class; // Total Gastado
                    if (columnIndex == 3) return Long.class; // # Transacciones
                    return String.class;
                }
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            double granTotalGastos = 0.0;
            
            for (GastoOperativo item : reportesMensuales) {
                // 🚨 CAMBIO: Mapeo de 4 campos del reporte
                Object[] fila = new Object[4];
                fila[0] = item.getAnio(); // Año
                fila[1] = item.getMes(); // Mes
                fila[2] = item.getTotalGastosMes(); // Monto Agregado
                fila[3] = item.getNumTransacciones(); // Conteo
                modelo.addRow(fila);
                
                if (item.getTotalGastosMes() != null) {
                    granTotalGastos += item.getTotalGastosMes(); // Suma el total de cada mes
                }
            }

            tblReporteGastos.setModel(modelo);
            
            if (reportesMensuales.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron gastos operativos registrados.", "Información", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    String.format("Reporte de %d meses cargado. Gran Total de Gastos: %.2f", reportesMensuales.size(), granTotalGastos), 
                    "Resumen", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error de Base de Datos al cargar el reporte: " + e.getMessage(), 
                "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, 
                "Error inesperado: " + e.getMessage(), 
                "Error de Sistema", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void bVolverActionPerformed() {
        this.dispose();
        // Asumiendo que existe una clase PanelReportes para el menú principal
        new PanelReportes(this.usuarioActual).setVisible(true); // Descomentar al usar en tu aplicación
       
    }

}
