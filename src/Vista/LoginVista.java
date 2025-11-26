package Vista;

import Controlador.AuthControlador;
import java.awt.*;
import Modelo.Usuario;
import javax.swing.*;

public class LoginVista extends JFrame {
    
    // Campo para inyectar el controlador (no crear dentro de los handlers)
    private AuthControlador authControlador;

    // Declaración de variables de la UI (Manual)
    private JPanel Bg;
    private JPanel jPanel1;
    private JLabel jLabel1; // Título
    private JLabel jLabel2; // Subtítulo "Inicio de Sesión"
    private JLabel jLabel3; // Etiqueta Usuario
    private JLabel jLabel4; // Etiqueta Contraseña
    private JLabel jLabel5; // Footer
    private JTextField txtUsuario;       // Correspondiente a jFormattedTextField1
    private JPasswordField txtPassword;  // Correspondiente a jPasswordField1
    private JButton bIngresar;
    private JButton bRegistrar;

    public LoginVista() {
        this(null); 
    }

    public LoginVista(AuthControlador authControlador) {
        this.authControlador = authControlador;
        initComponents();
        
        // 🚨 CAMBIO APLICADO AQUÍ: Permite la maximización/redimensionamiento
        this.setResizable(true); 
        setLocationRelativeTo(null); // Centrar la ventana
        
        // Configurar cursores
        bIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        this.pack(); // Empaquetar después de inicializar
    }

    // Setter para inyectar el controlador
    public void setAuthControlador(AuthControlador c) {
        this.authControlador = c;
    }

    // Métodos para que el controlador pueda obtener los valores de la vista
    public String getUsuarioInput() {
        return txtUsuario.getText().trim();
    }

    public String getPasswordInput() {
        return new String(txtPassword.getPassword());
    }

    // Métodos para mostrar mensajes (la vista hace solo UI)
    public void showError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // 🎨 Reimplementación manual del initComponents
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Bg = new JPanel();
        Bg.setBackground(new Color(245, 245, 245));
        // Usamos BorderLayout para que el jPanel1 se centre en el área CENTER
        Bg.setLayout(new BorderLayout()); 

        // El panel interior que contiene todos los campos y botones
        jPanel1 = new JPanel(new GridBagLayout()); 
        jPanel1.setBackground(new Color(255, 255, 255));
        // Tamaño base para el empaquetado inicial
        jPanel1.setPreferredSize(new Dimension(510, 510)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Márgenes internos (padding)
        gbc.fill = GridBagConstraints.HORIZONTAL; // Estirar horizontalmente

        // --- Configuración de Estilos ---
        Font fontLabel = new Font("Segoe UI", 0, 16);
        Font fontTitle = new Font("Segoe UI Black", 1, 24);
        Color colorPrimary = new Color(0, 102, 204);
        
        // 1. Título principal: SISTEMA MOBILIARIO
        jLabel1 = new JLabel("SISTEMA MOBILIARIO");
        jLabel1.setFont(fontTitle);
        jLabel1.setForeground(colorPrimary);
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.ipady = 10;
        gbc.weightx = 1.0; 
        jPanel1.add(jLabel1, gbc);
        
        // 2. Subtítulo: Inicio de Sesión
        jLabel2 = new JLabel("Inicio de Sesión");
        jLabel2.setFont(new Font("Segoe UI", 0, 14));
        jLabel2.setForeground(new Color(102, 102, 102));
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.ipady = 5;
        jPanel1.add(jLabel2, gbc);
        
        gbc.gridwidth = 1; gbc.weightx = 0.0; gbc.ipady = 0; // Reset
        
        // 3. Campo Usuario
        jLabel3 = new JLabel("Usuario:");
        jLabel3.setFont(fontLabel);
        txtUsuario = new JTextField(25);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1; 
        jPanel1.add(jLabel3, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.9; 
        jPanel1.add(txtUsuario, gbc);

        // 4. Campo Contraseña
        jLabel4 = new JLabel("Contraseña:");
        jLabel4.setFont(fontLabel);
        txtPassword = new JPasswordField(25);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.1;
        jPanel1.add(jLabel4, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.9;
        jPanel1.add(txtPassword, gbc);

        // 5. Botón Ingresar
        bIngresar = new JButton("INGRESAR");
        bIngresar.setFont(new Font("Segoe UI Black", 1, 14));
        bIngresar.setBackground(colorPrimary);
        bIngresar.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.ipady = 5;
        gbc.insets = new Insets(30, 80, 10, 80); // Mayor margen horizontal para centrar
        jPanel1.add(bIngresar, gbc);

        // 6. Botón Registrar
        bRegistrar = new JButton("REGISTRAR");
        bRegistrar.setFont(new Font("Segoe UI Black", 1, 14));
        bRegistrar.setBackground(new Color(0, 102, 255));
        bRegistrar.setForeground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.ipady = 5;
        gbc.insets = new Insets(10, 80, 20, 80); 
        jPanel1.add(bRegistrar, gbc);

        // 7. Footer
        jLabel5 = new JLabel("© 2025 Sistema Mobiliario NotMobi");
        jLabel5.setFont(new Font("Segoe UI", 0, 14));
        jLabel5.setForeground(new Color(102, 102, 102));
        jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.ipady = 5;
        gbc.insets = new Insets(20, 10, 10, 10); 
        jPanel1.add(jLabel5, gbc);
        
        // Agregamos el panel principal (jPanel1) al centro del contenedor (Bg)
        Bg.add(jPanel1, BorderLayout.CENTER); 
        
        getContentPane().add(Bg, BorderLayout.CENTER);
        
        // --- Añadir Listeners ---
        bIngresar.addActionListener(this::bIngresarActionPerformed);
        bRegistrar.addActionListener(this::bRegistrarActionPerformed);
    }

    // --- Lógica de Eventos ---

    private void bIngresarActionPerformed(java.awt.event.ActionEvent evt) {
        if (authControlador == null) {
            showError("Controlador no inicializado. Reinicia la aplicación o ponte en contacto con soporte.");
            return;
        }

        String usuario = getUsuarioInput();
        String password = getPasswordInput();

        if (usuario.isEmpty() || password.isEmpty()) {
            showError("Usuario y contraseña son obligatorios");
            return;
        }

        // Delegar la autenticación al controlador (que ejecutará la consulta en background)
        authControlador.autenticarUsuario(usuario, password);
    }

    private void bRegistrarActionPerformed(java.awt.event.ActionEvent evt) {
        // Asumiendo que RegistroVista tiene un constructor sin argumentos o uno que acepta el controlador
        RegistroVista registroVista = new RegistroVista(this.authControlador);
        registroVista.setVisible(true);
        this.dispose();
    }
}