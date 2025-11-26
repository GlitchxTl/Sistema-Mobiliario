package Vista;

import util.GestorBcv;
import util.Bcv;
import Modelo.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrincipalVista extends JFrame {
    
    // Propiedades
    private Usuario usuarioActual;
    private final AtomicBoolean cerrandoPorBoton = new AtomicBoolean(false); 
    
    // Declaración de variables de la UI
    private JPanel panelPrincipal;
    private JPanel jPanel2; 
    private JPanel jPanel3; 
    private JLabel jLabel1; 
    private JSeparator jSeparator1;
    private JButton bArticulos;
    private JButton bEstadística;
    private JButton bUbicacion1;
    private JButton bAuditoría;
    private JButton bGMovimientos;
    private JButton jButton1; 
    private JLabel lblBienvenida;
    private JLabel iLogo; 
    private JButton btnCerrarSesion;
    private JLabel jLabel2; 
    private JLabel jLabel3; 
    private JLabel precioBcv;

    // --- CONSTRUCTORES ---
    public PrincipalVista(Usuario usuario) {
        this.usuarioActual = usuario;
        initComponents();
        cargarPrecioBcvAsync();
        this.setResizable(true); 
        setLocationRelativeTo(null);
        actualizarBienvenida();
        configurarMenuMovimientos();
        
        ajustarLogo(); 
        
        this.pack();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public PrincipalVista() {
        initComponents();
        cargarPrecioBcvAsync();
        this.setResizable(true); 
        setLocationRelativeTo(null);
        actualizarBienvenida();
        configurarMenuMovimientos();
        ajustarLogo();
        this.pack();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    // --- MÉTODOS DE LÓGICA ---

    private void actualizarBienvenida() {
        lblBienvenida.setText("¡Bienvenido a NotMobi!, " + (usuarioActual != null ? usuarioActual.getNombreUsuario() : ""));
    }

    /**
     * Asegura que la imagen se cargue desde el classpath y que el escalado 
     * se realice solo cuando iLogo tenga dimensiones válidas.
     */
    private void ajustarLogo() {
        // 1. Cargar la imagen desde el classpath, método más robusto.
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/Images/LogoNotMobi.png"));

        // 2. Verificar si la carga fue exitosa y la imagen no es nula.
        if (iconoOriginal != null && iconoOriginal.getImageLoadStatus() == MediaTracker.COMPLETE && iconoOriginal.getIconWidth() > 0) {
            
            // 3. Si las dimensiones del JLabel son 0, usa un listener para esperar a que se le asignen.
            if (iLogo.getWidth() == 0 || iLogo.getHeight() == 0) {
                iLogo.addComponentListener(new java.awt.event.ComponentAdapter() {
                    @Override
                    public void componentResized(java.awt.event.ComponentEvent evt) {
                        iLogo.removeComponentListener(this); // Remover después del primer ajuste
                        adjustImage(iconoOriginal);
                    }
                });
            } else {
                // 4. Si ya tiene dimensiones, ajusta inmediatamente.
                adjustImage(iconoOriginal);
            }
        } else {
            // Manejo de error de imagen
            iLogo.setText("Logo no encontrado");
        }
    }
    
    // Método auxiliar para escalar la imagen (Solución al error anterior)
    private void adjustImage(ImageIcon iconoOriginal) {
        // Asegura que tanto la imagen esté cargada como que el JLabel tenga tamaño.
        if (iconoOriginal.getImageLoadStatus() == MediaTracker.COMPLETE && iLogo.getWidth() > 0 && iLogo.getHeight() > 0) {
            Image imagen = iconoOriginal.getImage();
            Image imagenEscalada = imagen.getScaledInstance(
                iLogo.getWidth(),
                iLogo.getHeight(),
                Image.SCALE_SMOOTH
            );
            iLogo.setIcon(new ImageIcon(imagenEscalada));
        } else {
             // Intenta mostrarla en tamaño original si el escalado falla por dimensiones
             iLogo.setIcon(iconoOriginal);
        }
    }
    
    private void cargarPrecioBcvAsync() {
        if (precioBcv == null) return;
        
        precioBcv.setText("...");

        SwingWorker<Double, Void> worker = new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                Bcv bcv = new Bcv();
                return bcv.getRate();
            }

            @Override
            protected void done() {
                try {
                    Double rate = get();
                    GestorBcv.getInstance().setTasaActual(rate);
                    precioBcv.setText(String.format("%.2f", GestorBcv.getInstance().getTasaActual()));
                } catch (Exception e) {
                    GestorBcv.getInstance().setTasaActual(-1.0);
                    precioBcv.setText("Error");
                }
            }
        };
        worker.execute();
    }
    
    private void configurarMenuMovimientos() {
        final AtomicBoolean cerrandoPorBoton = new AtomicBoolean(false); 
        bGMovimientos.setFocusable(false);

        // --- 1. Panel de botones ---
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(3, 1, 0, 2));
        menuPanel.setBackground(new Color(255, 255, 255));
        menuPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // --- 2. JWindow flotante ---
        Window parentWindow = SwingUtilities.getWindowAncestor(bGMovimientos);
        final JWindow menuWindow = new JWindow(parentWindow);

        // --- 3. Creación de botones ---
        JButton btnEntrada = createMenuItemButton("Entrada", new Color(0, 102, 204), e -> {
            new PanelEntrada(this.usuarioActual).setVisible(true);
            this.dispose();
        });
        menuPanel.add(btnEntrada);

        JButton btnTraslado = createMenuItemButton("Traslado", new Color(13, 51, 131), e -> {
            menuWindow.setVisible(false);
            new PanelTraslado(this.usuarioActual).setVisible(true);
            this.dispose();
        });
        menuPanel.add(btnTraslado);

        JButton btnSalida = createMenuItemButton("Salida", new Color(0, 102, 204), e -> {
            menuWindow.setVisible(false);
            new PanelSalida(this.usuarioActual).setVisible(true);
            this.dispose();
        });
        menuPanel.add(btnSalida);

        menuWindow.setContentPane(menuPanel);
        menuWindow.pack();

        // --- 5. Lógica de foco y cierre ---
        menuWindow.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                if (!cerrandoPorBoton.get()) { 
                    menuWindow.setVisible(false);
                }
                cerrandoPorBoton.set(false); 
            }
        });

        // --- 6. Lógica de MOSTRAR / OCULTAR ---
        bGMovimientos.addActionListener((ActionEvent e) -> {
            if (menuWindow.isVisible()) {
                cerrandoPorBoton.set(true); 
                menuWindow.setVisible(false);
            } else {
                Point location = bGMovimientos.getLocationOnScreen();
                int height = bGMovimientos.getHeight();
                
                menuWindow.setSize(bGMovimientos.getWidth(), menuPanel.getPreferredSize().height);
                menuWindow.setLocation(location.x, location.y + height);
                
                menuWindow.setVisible(true);
                menuWindow.requestFocus();
            }
        });
    }
    
    // Método auxiliar para crear botones del menú desplegable
    private JButton createMenuItemButton(String text, Color bgColor, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setFont(new Font("Segoe UI", 0, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(null);
        button.setPreferredSize(new Dimension(250, 40));
        button.setFocusable(false);
        button.addActionListener(listener);
        return button;
    }
    
    // 🎨 REIMPLEMENTACIÓN MANUAL DE initComponents
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        // 1. Panel Principal (BorderLayout)
        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(255, 255, 255));
        
        // 2. Panel Lateral de Menú (WEST - GridBagLayout para botones expandibles)
        jPanel2 = new JPanel(new GridBagLayout()); 
        jPanel2.setBackground(new Color(13, 51, 131));
        jPanel2.setPreferredSize(new Dimension(250, 500)); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; 
        gbc.weightx = 1.0; 
        
        // --- 2a. Título y Separador ---
        jLabel1 = new JLabel("Sistema Mobiliario");
        jLabel1.setFont(new Font("Segoe UI Black", 1, 20));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.ipady = 35; gbc.weighty = 0.01;
        jPanel2.add(jLabel1, gbc);
        
        jSeparator1 = new JSeparator();
        jSeparator1.setForeground(Color.WHITE); 
        gbc.gridy = 1; gbc.ipady = 0; gbc.weighty = 0.01;
        gbc.insets = new Insets(0, 44, 10, 44);
        jPanel2.add(jSeparator1, gbc);
        
        // --- 2b. Creación y adición de botones (Alto Peso Vertical) ---
        gbc.weighty = 1.0; // ¡Clave! Todos los botones compartirán el espacio restante
        gbc.ipady = 0;
        gbc.insets = new Insets(0, 0, 0, 0); 

        int currentRow = 2; 

        bArticulos = createMenuButton("Gestión Artículos", new Color(0, 102, 204), this::bArticulosActionPerformed);
        gbc.gridy = currentRow++; jPanel2.add(bArticulos, gbc);
        
        bUbicacion1 = createMenuButton("Gestión Ubicación", new Color(13, 51, 131), this::bUbicacion1ActionPerformed);
        gbc.gridy = currentRow++; jPanel2.add(bUbicacion1, gbc);
        
        jButton1 = createMenuButton("Orden De compra", new Color(0, 102, 204), this::jButton1ActionPerformed);
        gbc.gridy = currentRow++; jPanel2.add(jButton1, gbc);
        
        bGMovimientos = createMenuButton("Gestión Movimientos", new Color(13, 51, 131), this::bGMovimientosActionPerformed);
        gbc.gridy = currentRow++; jPanel2.add(bGMovimientos, gbc);
        
        bEstadística = createMenuButton("Estadística", new Color(0, 102, 204), this::bEstadísticaActionPerformed);
        gbc.gridy = currentRow++; jPanel2.add(bEstadística, gbc);
        
        bAuditoría = createMenuButton("Reportes", new Color(13, 51, 131), this::bAuditoríaActionPerformed);
        gbc.gridy = currentRow++; jPanel2.add(bAuditoría, gbc);

        panelPrincipal.add(jPanel2, BorderLayout.WEST); 
        
        // 3. Panel de Contenido Principal (CENTER)
        JPanel contentPanel = new JPanel(new BorderLayout());
        
        // 3a. Header Superior (Tasa BCV y Cerrar Sesión)
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JPanel topStripPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        jLabel3 = new JLabel("Tasa Actual:");
        jLabel3.setFont(new Font("Segoe UI", 1, 14));
        precioBcv = new JLabel();
        precioBcv.setFont(new Font("Segoe UI", 0, 14));
        
        topStripPanel.add(Box.createHorizontalStrut(10));
        topStripPanel.add(jLabel3);
        topStripPanel.add(precioBcv);
        
        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setFont(new Font("Segoe UI", 0, 18));
        btnCerrarSesion.setBackground(new Color(0, 102, 204));
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        btnCerrarSesion.addActionListener(this::btnCerrarSesionActionPerformed);
        
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Alineación a la derecha
        closePanel.add(btnCerrarSesion);
        
        headerPanel.add(topStripPanel, BorderLayout.WEST);
        headerPanel.add(closePanel, BorderLayout.EAST);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // 3b. Panel Banner (Bienvenida y Logo) - NORTE del centro
        jPanel3 = new JPanel(new BorderLayout());
        jPanel3.setBackground(new Color(0, 102, 204));
        jPanel3.setPreferredSize(new Dimension(580, 140));
        
        lblBienvenida = new JLabel("¡Bienvenido!");
        lblBienvenida.setFont(new Font("Segoe UI Black", 1, 20));
        lblBienvenida.setForeground(Color.WHITE);
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(28, 20, 0, 0));
        
        iLogo = new JLabel();
        iLogo.setPreferredSize(new Dimension(171, 140)); // Se ajusta para ocupar la altura completa de jPanel3
        iLogo.setHorizontalAlignment(SwingConstants.CENTER);
        iLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 14));
        
        jPanel3.add(lblBienvenida, BorderLayout.WEST);
        jPanel3.add(iLogo, BorderLayout.EAST); // El logo se añade aquí.
        
        // 3c. Imagen de Fondo (CENTER del centro)
        jLabel2 = new JLabel();
        try {
            // Carga la imagen de fondo desde el classpath
            jLabel2.setIcon(new ImageIcon(getClass().getResource("/Images/sistema-vivant-muebles-sygma-003.jpg")));
        } catch (Exception e) {
            jLabel2.setText("Imagen de Fondo no disponible");
            jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        }
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Panel contenedor para Banner e Imagen de Fondo
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(jPanel3, BorderLayout.NORTH);
        centerPanel.add(jLabel2, BorderLayout.CENTER);
        
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        panelPrincipal.add(contentPanel, BorderLayout.CENTER);
        
        getContentPane().add(panelPrincipal);
    }
    
    // Método auxiliar para crear botones de menú lateral
    private JButton createMenuButton(String text, Color bgColor, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setFont(new Font("Segoe UI", 0, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(null);
        button.setMinimumSize(new Dimension(250, 60)); 
        button.addActionListener(listener);
        return button;
    }
    
    // --- LÓGICA DE EVENTOS ---

    private void bAuditoríaActionPerformed(java.awt.event.ActionEvent evt) {
        // Asumiendo que PanelReportes existe y acepta Usuario
        PanelReportes panelReportes = new PanelReportes(this.usuarioActual);
        panelReportes.setVisible(true);
        this.dispose();
    }
    
    private void bArticulosActionPerformed(java.awt.event.ActionEvent evt) {
        // Asumiendo que PanelArticulos existe y acepta Usuario
        new PanelArticulos(this.usuarioActual).setVisible(true);
        this.dispose();
    }
    
    private void bEstadísticaActionPerformed(java.awt.event.ActionEvent evt) {
        // Asumiendo que PanelEstadistica existe y acepta Usuario
        PanelEstadistica panelEstadistica = new PanelEstadistica(this.usuarioActual);
        panelEstadistica.setVisible(true);
        this.dispose();
    }
    
    private void bUbicacion1ActionPerformed(java.awt.event.ActionEvent evt) {
        // Asumiendo que PanelUbicacion existe y acepta Usuario
        new PanelUbicacion(this.usuarioActual).setVisible(true);
        this.dispose();
    }
    
    private void bGMovimientosActionPerformed(java.awt.event.ActionEvent evt) {
        // La lógica del menú desplegable se maneja en configurarMenuMovimientos()
    }
    
    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea cerrar sesión?",
            "Confirmar cierre de sesión",
            JOptionPane.YES_NO_OPTION
        );

        if(confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginVista().setVisible(true); // Asumiendo que LoginVista existe
        }
    }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        // Asumiendo que PanelOrdenCompra existe y acepta Usuario
        PanelOrdenCompra panelCompra = new PanelOrdenCompra(this.usuarioActual);
        panelCompra.setVisible(true);
        this.dispose();
    }
}