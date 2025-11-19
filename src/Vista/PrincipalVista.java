package Vista;
import Controlador.AuthControlador;
import Modelo.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrincipalVista extends javax.swing.JFrame {
    private Usuario usuarioActual; 
    private boolean cerrandoPorBoton = false;

    public PrincipalVista(Usuario usuario) {
        initComponents();
        this.usuarioActual = usuario; 
        setLocationRelativeTo(null);
        actualizarBienvenida();
        configurarMenuMovimientos();
    }

    public PrincipalVista() {
        initComponents();
        setLocationRelativeTo(null);
        configurarMenuMovimientos();
    }

    private void actualizarBienvenida() {
        lblBienvenida.setText("¡Bienvenido!, " + (usuarioActual != null ? usuarioActual.getNombreUsuario() : ""));
    }

private void configurarMenuMovimientos() {
    
    // ==========================================================
    // ======> EL CAMBIO CLAVE: AtomicBoolean <======
    //
    // Esta variable es accesible y MODIFICABLE por ambos listeners.
    final AtomicBoolean cerrandoPorBoton = new AtomicBoolean(false);
    // ==========================================================
    
    // Ya no es necesario setFocusable(false) en este enfoque, pero lo dejaremos.
    bGMovimientos.setFocusable(false);

    // --- 1. Crear el Panel que CONTENDRÁ los botones ---
    JPanel menuPanel = new JPanel();
    menuPanel.setLayout(new GridLayout(3, 1, 0, 2));
    menuPanel.setBackground(new java.awt.Color(255, 255, 255));
    menuPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

    // --- 2. Crear la JWindow que "flotará" ---
    Window parentWindow = SwingUtilities.getWindowAncestor(bGMovimientos);
    final JWindow menuWindow = new JWindow(parentWindow);

    // --- 3. Creación y adición de botones (incluyendo setFocusable) ---
    
    // ======== BOTÓN ENTRADA ========
    JButton btnEntrada = new JButton("Entrada");
    btnEntrada.setBackground(new java.awt.Color(0, 102, 204));
    btnEntrada.setFont(new java.awt.Font("Segoe UI", 0, 14));
    btnEntrada.setForeground(Color.WHITE);
    btnEntrada.setBorder(null);
    btnEntrada.setPreferredSize(new Dimension(bGMovimientos.getWidth(), 40));
    btnEntrada.setFocusable(false);
    btnEntrada.addActionListener(e -> {
        menuWindow.setVisible(false);
        // ⭐ ENVÍA EL USUARIO ⭐
        new PanelEntrada(this.usuarioActual).setVisible(true);
        this.dispose();
    });
    menuPanel.add(btnEntrada);

    // ======== BOTÓN TRASLADO ========
    JButton btnTraslado = new JButton("Traslado");
    btnTraslado.setBackground(new java.awt.Color(13, 51, 131));
    btnTraslado.setFont(new java.awt.Font("Segoe UI", 0, 14));
    btnTraslado.setForeground(Color.WHITE);
    btnTraslado.setBorder(null);
    btnTraslado.setPreferredSize(new Dimension(bGMovimientos.getWidth(), 40));
    btnTraslado.setFocusable(false);
    btnTraslado.addActionListener(e -> {
        menuWindow.setVisible(false);
        // ⭐ ENVÍA EL USUARIO ⭐
        new PanelTraslado(this.usuarioActual).setVisible(true);
        this.dispose();
    });
    menuPanel.add(btnTraslado);

    // ======== BOTÓN SALIDA ========
    JButton btnSalida = new JButton("Salida");
    btnSalida.setBackground(new java.awt.Color(0, 102, 204));
    btnSalida.setFont(new java.awt.Font("Segoe UI", 0, 14));
    btnSalida.setForeground(Color.WHITE);
    btnSalida.setBorder(null);
    btnSalida.setPreferredSize(new Dimension(bGMovimientos.getWidth(), 40));
    btnSalida.setFocusable(false);
    btnSalida.addActionListener(e -> {
        menuWindow.setVisible(false);
        // ⭐ ENVÍA EL USUARIO ⭐
        new PanelSalida(this.usuarioActual).setVisible(true);
        this.dispose();
    });
    menuPanel.add(btnSalida);

    // --- 4. Ensamblar la JWindow ---
    menuWindow.setContentPane(menuPanel);
    menuWindow.pack();

    // --- 5. Lógica de "Ocultar al hacer clic fuera" ---
    menuWindow.addWindowFocusListener(new WindowAdapter() {
        @Override
        public void windowLostFocus(WindowEvent e) {
            // Solo cerramos si NO fue el botón quien inició el cierre
            if (!cerrandoPorBoton.get()) { // <-- Usamos .get()
                menuWindow.setVisible(false);
            }
            // Reiniciamos la bandera
            cerrandoPorBoton.set(false); // <-- Usamos .set(false)
        }
    });

    // --- 6. Lógica de MOSTRAR / OCULTAR (¡Ahora sí funciona!) ---
    bGMovimientos.addActionListener((ActionEvent e) -> {
        if (menuWindow.isVisible()) {
            // Si está visible, la cerramos
            
            // 1. Levantamos la bandera: ¡Vamos a cerrar la ventana!
            cerrandoPorBoton.set(true); // <-- Usamos .set(true)
            
            // 2. Cerramos
            menuWindow.setVisible(false);
        } else {
            // Si está oculta, la mostramos
            Point location = bGMovimientos.getLocationOnScreen();
            int height = bGMovimientos.getHeight();
            
            menuWindow.setLocation(location.x, location.y + height);
            
            menuWindow.setVisible(true);
            menuWindow.requestFocus();
        }
    });
} 
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        bArticulos = new javax.swing.JButton();
        bEstadística = new javax.swing.JButton();
        bUbicacion1 = new javax.swing.JButton();
        bReportes = new javax.swing.JButton();
        bGMovimientos = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnCerrarSesion = new javax.swing.JButton();
        btnGestionUsuarios = new javax.swing.JButton();
        btnCerrarSesion2 = new javax.swing.JButton();
        lblBienvenida = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelPrincipal.setBackground(new java.awt.Color(255, 255, 255));
        panelPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(13, 51, 131));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Sistema Mobiliario");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 35, 250, 47));
        jPanel2.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 88, 168, 10));

        bArticulos.setBackground(new java.awt.Color(0, 102, 204));
        bArticulos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        bArticulos.setForeground(new java.awt.Color(255, 255, 255));
        bArticulos.setText("Gestión Artículos");
        bArticulos.setBorder(null);
        bArticulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bArticulosActionPerformed(evt);
            }
        });
        jPanel2.add(bArticulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 250, 70));

        bEstadística.setBackground(new java.awt.Color(0, 102, 204));
        bEstadística.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        bEstadística.setForeground(new java.awt.Color(255, 255, 255));
        bEstadística.setText("Estadística");
        bEstadística.setBorder(null);
        bEstadística.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bEstadísticaActionPerformed(evt);
            }
        });
        jPanel2.add(bEstadística, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 440, 250, 60));

        bUbicacion1.setBackground(new java.awt.Color(13, 51, 131));
        bUbicacion1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        bUbicacion1.setForeground(new java.awt.Color(255, 255, 255));
        bUbicacion1.setText("Gestión Ubicación");
        bUbicacion1.setBorder(null);
        bUbicacion1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bUbicacion1ActionPerformed(evt);
            }
        });
        jPanel2.add(bUbicacion1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 250, 70));

        bReportes.setBackground(new java.awt.Color(13, 51, 131));
        bReportes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        bReportes.setForeground(new java.awt.Color(255, 255, 255));
        bReportes.setText("Reportes");
        bReportes.setBorder(null);
        bReportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bReportesActionPerformed(evt);
            }
        });
        jPanel2.add(bReportes, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 370, 250, 70));

        bGMovimientos.setBackground(new java.awt.Color(0, 102, 204));
        bGMovimientos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        bGMovimientos.setForeground(new java.awt.Color(255, 255, 255));
        bGMovimientos.setText("Gestión Movimientos");
        bGMovimientos.setBorder(null);
        bGMovimientos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bGMovimientosActionPerformed(evt);
            }
        });
        jPanel2.add(bGMovimientos, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 300, 250, 70));

        panelPrincipal.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 250, 500));

        jPanel3.setBackground(new java.awt.Color(0, 102, 204));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 580, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );

        panelPrincipal.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 580, 140));

        btnCerrarSesion.setBackground(new java.awt.Color(0, 102, 204));
        btnCerrarSesion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnCerrarSesion.setForeground(new java.awt.Color(255, 255, 255));
        btnCerrarSesion.setText("Cerrar Sesión");
        btnCerrarSesion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });
        panelPrincipal.add(btnCerrarSesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 0, 190, 60));

        btnGestionUsuarios.setBackground(new java.awt.Color(0, 102, 204));
        btnGestionUsuarios.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnGestionUsuarios.setForeground(new java.awt.Color(255, 255, 255));
        btnGestionUsuarios.setText("Gestión Usuarios");
        btnGestionUsuarios.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnGestionUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGestionUsuariosActionPerformed(evt);
            }
        });
        panelPrincipal.add(btnGestionUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, 180, 60));

        btnCerrarSesion2.setBackground(new java.awt.Color(13, 51, 131));
        btnCerrarSesion2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnCerrarSesion2.setForeground(new java.awt.Color(255, 255, 255));
        btnCerrarSesion2.setText("Configuración");
        btnCerrarSesion2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnCerrarSesion2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesion2ActionPerformed(evt);
            }
        });
        panelPrincipal.add(btnCerrarSesion2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 0, 210, 60));

        lblBienvenida.setBackground(new java.awt.Color(255, 255, 255));
        lblBienvenida.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        lblBienvenida.setText("¡Bienvenido!");
        panelPrincipal.add(lblBienvenida, new org.netbeans.lib.awtextra.AbsoluteConstraints(252, 200, 580, 60));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bReportesActionPerformed
        PanelReportes panelReportes = new PanelReportes(this.usuarioActual);
        panelReportes.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bReportesActionPerformed

    private void bArticulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bArticulosActionPerformed
        // TODO add your handling code here:
        new PanelArticulos(this.usuarioActual).setVisible(true);
        this.dispose();// Cierra la vista actual
    }//GEN-LAST:event_bArticulosActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea cerrar sesión?",
            "Confirmar cierre de sesión",
            JOptionPane.YES_NO_OPTION
        );
        
        if(confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginVista().setVisible(true);
        }
        
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void bEstadísticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bEstadísticaActionPerformed
        PanelEstadistica panelEstadistica = new PanelEstadistica(this.usuarioActual);
        panelEstadistica.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bEstadísticaActionPerformed

    private void bUbicacion1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUbicacion1ActionPerformed
        new PanelUbicacion(this.usuarioActual).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bUbicacion1ActionPerformed

    private void bGMovimientosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bGMovimientosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bGMovimientosActionPerformed

    private void btnGestionUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGestionUsuariosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGestionUsuariosActionPerformed

    private void btnCerrarSesion2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesion2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCerrarSesion2ActionPerformed

    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bArticulos;
    private javax.swing.JButton bEstadística;
    private javax.swing.JButton bGMovimientos;
    private javax.swing.JButton bReportes;
    private javax.swing.JButton bUbicacion1;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnCerrarSesion2;
    private javax.swing.JButton btnGestionUsuarios;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JPanel panelPrincipal;
    // End of variables declaration//GEN-END:variables
}
