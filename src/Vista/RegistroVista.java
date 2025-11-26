package Vista;

import Controlador.AuthControlador;
import Controlador.RegistrationResult;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Vista para registrar usuarios. Solo UI: recoge inputs y muestra resultados.
 * Toda la lógica y validaciones de negocio vive en AuthControlador.
 */
public class RegistroVista extends JFrame {

    private final AuthControlador authControlador;
    // Mapa nombreRol -> idRol (ajusta números si tu BD tiene otros ids)
    private final Map<String, Integer> rolMap = new HashMap<>();

    // Declaración de variables de la UI (Manual)
    private JPanel Bg;
    private JPanel jPanel1;
    private JLabel jLabel1;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JTextField txtNombreCompleto; // Renombrado de jFormattedTextField1
    private JTextField txtUsuario;       // Renombrado de jFormattedTextField2
    private JPasswordField txtPassword;  // Renombrado de jPasswordField1
    private JButton bRegistrarse;
    private JLabel jLabel5;
    private JButton bVolver;
    private JComboBox<String> cboxRol;
    private JLabel jLabel6;
    
    public RegistroVista() {
        this(new AuthControlador());
    }

    public RegistroVista(AuthControlador authControlador) {
        this.authControlador = authControlador;
        initRolMap();
        initComponents();
        
        // 🚨 CAMBIO DE MAXIMIZACIÓN MANTENIDO: true para permitir la maximización/redimensionamiento
        this.setResizable(true); 
        setLocationRelativeTo(null);
        this.pack();
    }

    private void initRolMap() {
        // Mapea los textos del combo a los id_rol de la BD
        rolMap.put("Usuario", 1);
        rolMap.put("Archivista", 2);
        rolMap.put("Escribiente", 3);
        rolMap.put("Notario", 4);
    }

    // 🎨 Reimplementación manual del initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Bg = new JPanel();
        Bg.setLayout(new BorderLayout()); // Usamos BorderLayout para centrar el panel principal
        
        // El panel interior que contiene todos los campos y botones
        jPanel1 = new JPanel(new GridBagLayout()); // Usamos GridBagLayout para centrar y organizar
        jPanel1.setBackground(new Color(255, 255, 255));
        jPanel1.setPreferredSize(new Dimension(510, 503)); // Tamaño original del panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); // Márgenes internos para los componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Estirar horizontalmente
        
        // Inicialización de Componentes
        Font fontLabel = new Font("Segoe UI", 0, 16);
        Font fontHeader = new Font("Segoe UI Black", 1, 20);
        Font fontButton = new Font("Segoe UI Black", 1, 18);

        // 1. Título
        jLabel1 = new JLabel("Registro de Nuevo Usuario");
        jLabel1.setFont(fontHeader);
        jLabel1.setForeground(new Color(0, 102, 204));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.ipady = 30; // 30px padding vertical
        jPanel1.add(jLabel1, gbc);
        
        gbc.gridwidth = 1; gbc.ipady = 0; // Reset
        
        // 2. Nombre Completo
        jLabel3 = new JLabel("Nombre Completo:");
        jLabel3.setFont(fontLabel);
        txtNombreCompleto = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 1; jPanel1.add(jLabel3, gbc);
        gbc.gridx = 1; gbc.gridy = 1; jPanel1.add(txtNombreCompleto, gbc);

        // 3. Usuario
        jLabel4 = new JLabel("Usuario:");
        jLabel4.setFont(fontLabel);
        txtUsuario = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 2; jPanel1.add(jLabel4, gbc);
        gbc.gridx = 1; gbc.gridy = 2; jPanel1.add(txtUsuario, gbc);

        // 4. Contraseña
        jLabel6 = new JLabel("Contraseña:");
        jLabel6.setFont(fontLabel);
        txtPassword = new JPasswordField(20);
        
        gbc.gridx = 0; gbc.gridy = 3; jPanel1.add(jLabel6, gbc);
        gbc.gridx = 1; gbc.gridy = 3; jPanel1.add(txtPassword, gbc);

        // 5. Rol
        jLabel5 = new JLabel("Rol:");
        jLabel5.setFont(fontLabel);
        String[] roles = {"Usuario", "Archivista", "Escribiente", "Notario"};
        cboxRol = new JComboBox<>(roles);
        
        gbc.gridx = 0; gbc.gridy = 4; jPanel1.add(jLabel5, gbc);
        gbc.gridx = 1; gbc.gridy = 4; jPanel1.add(cboxRol, gbc);
        
        // 6. Botón Registrarse
        bRegistrarse = new JButton("Registrarse");
        bRegistrarse.setFont(fontButton);
        bRegistrarse.setBackground(new Color(0, 102, 204));
        bRegistrarse.setForeground(new Color(255, 255, 255));
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.ipady = 10;
        gbc.insets = new Insets(20, 60, 5, 60); // Márgenes más grandes para el botón
        jPanel1.add(bRegistrarse, gbc);
        
        // 7. Botón Volver
        bVolver = new JButton("Volver");
        bVolver.setFont(fontButton);
        bVolver.setBackground(new Color(0, 102, 255));
        bVolver.setForeground(new Color(255, 255, 255));
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.ipady = 10;
        gbc.insets = new Insets(5, 60, 20, 60); // Márgenes
        jPanel1.add(bVolver, gbc);
        
        // 8. Envolver el jPanel1 en el contenedor principal (Bg) usando BorderLayout.CENTER
        // Esto centrará jPanel1 y permitirá que el fondo (Bg) se estire
        Bg.add(jPanel1, BorderLayout.CENTER); 
        
        getContentPane().add(Bg, BorderLayout.CENTER);
        
        // Añadir Listeners (Actions)
        bRegistrarse.addActionListener(this::bRegistrarseActionPerformed);
        bVolver.addActionListener(this::bVolverActionPerformed);
        cboxRol.addActionListener(this::cboxRolActionPerformed);
    }
    
    // --- Lógica de Eventos (Se mantiene) ---

    private void bRegistrarseActionPerformed(java.awt.event.ActionEvent evt) {
        final String nombre = txtNombreCompleto.getText().trim();
        final String usuario = txtUsuario.getText().trim();
        final String password = new String(txtPassword.getPassword());

        if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String rolSeleccionado = ((String) cboxRol.getSelectedItem()).trim();
        int idRol = rolMap.getOrDefault(rolSeleccionado, 1);

        bRegistrarse.setEnabled(false);

        new javax.swing.SwingWorker<RegistrationResult, Void>() {
            @Override
            protected RegistrationResult doInBackground() {
                return authControlador.registrarUsuario(nombre, usuario, password, idRol);
            }

            @Override
            protected void done() {
                bRegistrarse.setEnabled(true);
                try {
                    RegistrationResult res = get();
                    if (res.isSuccess()) {
                        JOptionPane.showMessageDialog(RegistroVista.this, res.getMessage(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        
                    } else {
                        JOptionPane.showMessageDialog(RegistroVista.this, res.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(RegistroVista.this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();

    }

    private void bVolverActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
        LoginVista panelLogin = new LoginVista();
        panelLogin.setVisible(true);
    
    }

    private void cboxRolActionPerformed(java.awt.event.ActionEvent evt) {
        // No hay lógica específica aquí por ahora
    }
}
