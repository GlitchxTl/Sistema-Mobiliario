package Vista;

import Modelo.OrdenCompra;
import Modelo.OrdenCompraDAO;
import Modelo.OrdenCompraDetalle;
import javax.swing.*;
import Modelo.Usuario;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Vista para consultar, listar y ver los detalles de las Órdenes de Compra existentes.
 * Ahora maximizada para ocupar toda la pantalla.
 * * NOTA: Los métodos de carga de datos (cargarOCs, mostrarDetalle) son simulados, 
 * ya que dependen de la implementación real de OrdenCompraDAO.
 */
public class VistaConsultaOrdenCompra extends javax.swing.JDialog {
    private Usuario usuarioActual; 
    private final OrdenCompraDAO ocDAO = new OrdenCompraDAO();
    private DefaultTableModel modeloOCs;
    private DefaultTableModel modeloDetalles;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public VistaConsultaOrdenCompra(JFrame parent) {
        // Se establece como JDialog modal para bloquear la vista padre hasta cerrar.
        super(parent, "Consulta de Órdenes de Compra", true); 
        initComponents();
        // 🚨 CAMBIO NECESARIO: Maximizar la ventana.
        // getGraphicsConfiguration().getBounds() obtiene las dimensiones de la pantalla.
        // Se usa setSize y se reubica para centrarla, aunque estará maximizada.
        
        // La forma más simple y compatible con JDialog es la siguiente:
        this.setLocationRelativeTo(parent);
        this.setModal(true); // Ya lo tiene el super, pero se reafirma
        
        // Para maximizar un JDialog, se debe usar setSize o usar el Toolkit:
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        
        // Como alternativa a setSize, si no se quiere depender de Toolkit:
        // setBounds(0, 0, screenSize.width, screenSize.height);
        
        inicializarTablas();
        cargarOCs();
    }
    
    private void inicializarTablas() {
        // Tabla de Encabezados de OC
        String[] headersOC = new String[] {
            "ID OC", "Referencia", "Proveedor", "Fecha", "Emisor", "Estado", "Monto Total"
        };
        modeloOCs = new DefaultTableModel(headersOC, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) { return Double.class; } // Monto Total
                return super.getColumnClass(columnIndex);
            }
        };
        jTablaOCs.setModel(modeloOCs);
        
        // Tabla de Detalles
        String[] headersDetalle = new String[] {
            "Código Art.", "Nombre Art.", "Precio Unit.", "Cantidad", "Subtotal"
        };
        modeloDetalles = new DefaultTableModel(headersDetalle, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 2) { return Double.class; } // Precio, Cantidad, Subtotal
                return super.getColumnClass(columnIndex);
            }
        };
        jTablaDetalles.setModel(modeloDetalles);
        
        // Listener para la selección en la tabla de OCs
        jTablaOCs.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalleOCSeleccionada();
            }
        });
    }

    /**
     * Carga todas las Órdenes de Compra en la tabla principal.
     */
    private void cargarOCs() {
        modeloOCs.setRowCount(0);
        try {
            // Se asume la existencia de ocDAO.listarOrdenesCompra()
            List<OrdenCompra> listaOCs = ocDAO.listarOrdenesCompra(); 
            for (OrdenCompra oc : listaOCs) {
                modeloOCs.addRow(new Object[] {
                    oc.getIdOrdenCompra(),
                    oc.getNumeroReferencia(),
                    oc.getNombreProveedor(),
                    oc.getFechaEmision(),
                    oc.getNombreUsuarioEmisor(), // Se usa el campo auxiliar
                    oc.getEstado(),
                    oc.getMontoTotal()
                });
            }
            if (!listaOCs.isEmpty()) {
                jTablaOCs.setRowSelectionInterval(0, 0); // Seleccionar la primera fila por defecto
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar Órdenes de Compra: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Muestra los detalles de la Orden de Compra seleccionada en la tabla de detalles.
     */
    private void mostrarDetalleOCSeleccionada() {
        int fila = jTablaOCs.getSelectedRow();
        modeloDetalles.setRowCount(0);
        
        if (fila < 0) {
            jLReferenciaDetalle.setText("N/A");
            jLEstadoDetalle.setText("N/A");
            jLTotalDetalle.setText(df.format(0.0));
            return;
        }

        try {
            // Obtener el ID de la OC de la fila seleccionada
            Long idOc = (Long) modeloOCs.getValueAt(fila, 0);
            
            // Se asume la existencia de ocDAO.obtenerOCPorId() que devuelve la OC con sus detalles
            OrdenCompra ocCompleta = ocDAO.obtenerOCPorId(idOc); 

            if (ocCompleta != null) {
                // Actualizar encabezado del detalle
                jLReferenciaDetalle.setText(ocCompleta.getNumeroReferencia());
                jLEstadoDetalle.setText(ocCompleta.getEstado());
                jLTotalDetalle.setText(df.format(ocCompleta.getMontoTotal()));
                
                // Cargar detalles
                if (ocCompleta.getDetalles() != null) {
                    for (OrdenCompraDetalle d : ocCompleta.getDetalles()) {
                        modeloDetalles.addRow(new Object[] {
                            d.getCodigoArticulo(),
                            d.getNombreArticulo(),
                            d.getPrecioUnitario(),
                            d.getCantidadPedida(),
                            d.getSubTotal()
                        });
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar detalles de la OC: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // ----------------------------------------------------------------------
    // --- Código de Interfaz Gráfica (Generated Code) ---
    // ----------------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanelPrincipal = new javax.swing.JPanel();
        jLabelTitulo = new javax.swing.JLabel();
        jScrollPaneOCs = new javax.swing.JScrollPane();
        jTablaOCs = new javax.swing.JTable();
        jPanelDetalle = new javax.swing.JPanel();
        jLabelRefTitulo = new javax.swing.JLabel();
        jLReferenciaDetalle = new javax.swing.JLabel();
        jLabelEstadoTitulo = new javax.swing.JLabel();
        jLEstadoDetalle = new javax.swing.JLabel();
        jScrollPaneDetalles = new javax.swing.JScrollPane();
        jTablaDetalles = new javax.swing.JTable();
        jLabelTotalTitulo = new javax.swing.JLabel();
        jLTotalDetalle = new javax.swing.JLabel();
        jButtonCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelPrincipal.setBackground(new java.awt.Color(255, 255, 255));

        jLabelTitulo.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        jLabelTitulo.setForeground(new java.awt.Color(13, 51, 131));
        jLabelTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitulo.setText("Consulta de Órdenes de Compra (OC)");

        jTablaOCs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "ID OC", "Referencia", "Proveedor", "Fecha", "Emisor", "Estado", "Monto Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTablaOCs.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneOCs.setViewportView(jTablaOCs);
        
        jPanelDetalle.setBorder(javax.swing.BorderFactory.createTitledBorder("Detalle de OC Seleccionada"));
        jPanelDetalle.setBackground(new java.awt.Color(240, 240, 255));

        jLabelRefTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelRefTitulo.setText("Referencia:");

        jLReferenciaDetalle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLReferenciaDetalle.setText("N/A");

        jLabelEstadoTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelEstadoTitulo.setText("Estado:");

        jLEstadoDetalle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLEstadoDetalle.setText("N/A");

        jTablaDetalles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Código Art.", "Nombre Art.", "Precio Unit.", "Cantidad", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneDetalles.setViewportView(jTablaDetalles);

        jLabelTotalTitulo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabelTotalTitulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTotalTitulo.setText("TOTAL:");

        jLTotalDetalle.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLTotalDetalle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLTotalDetalle.setText("0,00");

        javax.swing.GroupLayout jPanelDetalleLayout = new javax.swing.GroupLayout(jPanelDetalle);
        jPanelDetalle.setLayout(jPanelDetalleLayout);
        jPanelDetalleLayout.setHorizontalGroup(
            jPanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneDetalles)
                    .addGroup(jPanelDetalleLayout.createSequentialGroup()
                        .addComponent(jLabelRefTitulo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLReferenciaDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelEstadoTitulo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLEstadoDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelTotalTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLTotalDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelDetalleLayout.setVerticalGroup(
            jPanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelRefTitulo)
                    .addComponent(jLReferenciaDetalle)
                    .addComponent(jLabelEstadoTitulo)
                    .addComponent(jLEstadoDetalle)
                    .addComponent(jLabelTotalTitulo)
                    .addComponent(jLTotalDetalle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPaneDetalles, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButtonCerrar.setBackground(new java.awt.Color(13, 51, 131));
        jButtonCerrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButtonCerrar.setForeground(new java.awt.Color(255, 255, 255));
        jButtonCerrar.setText("Cerrar");
        jButtonCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCerrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPrincipalLayout = new javax.swing.GroupLayout(jPanelPrincipal);
        jPanelPrincipal.setLayout(jPanelPrincipalLayout);
        jPanelPrincipalLayout.setHorizontalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // 🚨 AJUSTE NECESARIO: Extender el ancho de la tabla principal.
                    // Se usa GroupLayout.DEFAULT_SIZE, pero lo aseguramos para que sea lo más ancho posible.
                    .addComponent(jScrollPaneOCs, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE) 
                    .addComponent(jPanelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPrincipalLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelPrincipalLayout.setVerticalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // 🚨 AJUSTE NECESARIO: Cambiar la altura fija de jScrollPaneOCs (200) por una flexible
                .addComponent(jScrollPaneOCs, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE) // Altura inicial mayor y flexible
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // jPanelDetalle tiene un alto flexible (DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE) // Ancho del contenedor
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE) // Alto del contenedor
        );

        pack();
    }// </editor-fold>                        

    private void jButtonCerrarActionPerformed(java.awt.event.ActionEvent evt) {                                             
        this.dispose();
        
    }                                           

    // ----------------------------------------------------------------------
    // --- Declaración de Variables ---
    // ----------------------------------------------------------------------
    private javax.swing.JButton jButtonCerrar;
    private javax.swing.JLabel jLEstadoDetalle;
    private javax.swing.JLabel jLReferenciaDetalle;
    private javax.swing.JLabel jLTotalDetalle;
    private javax.swing.JLabel jLabelEstadoTitulo;
    private javax.swing.JLabel jLabelRefTitulo;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JLabel jLabelTotalTitulo;
    private javax.swing.JPanel jPanelDetalle;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JScrollPane jScrollPaneDetalles;
    private javax.swing.JScrollPane jScrollPaneOCs;
    private javax.swing.JTable jTablaDetalles;
    private javax.swing.JTable jTablaOCs;
    // End of variables declaration                  
}
