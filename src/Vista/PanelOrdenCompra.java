package Vista;

import Modelo.OrdenCompra;
import Modelo.OrdenCompraDetalle;
import Modelo.OrdenCompraDAO;
import Modelo.Usuario;
import Modelo.Articulo; 
import Modelo.ArticuloDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*; // Necesario para la manipulación del tamaño
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista para la creación de una Orden de Compra y manejo de sus detalles.
 * Ahora se configura para maximizarse en la pantalla.
 */
public class PanelOrdenCompra extends javax.swing.JFrame {

    private final OrdenCompraDAO ocDAO = new OrdenCompraDAO();
    private final ArticuloDAO articuloDAO = new ArticuloDAO();
    private Usuario usuarioActual;
    
    // --- Modelos de la Vista ---
    private DefaultTableModel modeloTablaDetalle;
    private final List<OrdenCompraDetalle> detallesOC = new ArrayList<>(); 
    
    // --- Formato para montos ---
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public PanelOrdenCompra(Usuario usuario) {
        // 🚨 Configuración de maximización movida al inicio
        this.usuarioActual = usuario;
        
        initComponents();
        
        // 🚨 CAMBIO: Se remueve 'this.setResizable(false);' y se añade la maximización
        this.setTitle("Crear Nueva Orden de Compra");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizar la ventana
        
        // Se recomienda eliminar setLocationRelativeTo(null) en ventanas maximizadas
        
        inicializar();
    }
    
    public PanelOrdenCompra() {
        this(null);
    }
    
    private void inicializar() {
        // --- 1. Configuración de la Tabla de Detalles ---
        String[] headers = new String[] {
            "ID Art.", "Código Art.", "Nombre Art.", "Precio Unit.", "Cantidad", "Subtotal"
        };
        
        modeloTablaDetalle = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 3) { // Precio, Cantidad, Subtotal
                    return Double.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        jTablaDetalles.setModel(modeloTablaDetalle);

        // Ocultar la columna ID Art. (índice 0)
        try {
            jTablaDetalles.getColumnModel().getColumn(0).setMinWidth(0);
            jTablaDetalles.getColumnModel().getColumn(0).setMaxWidth(0);
            jTablaDetalles.getColumnModel().getColumn(0).setWidth(0);
        } catch (Exception ignored) {}

        // --- 2. Carga Inicial de Componentes (ej. Combo Box de Artículos) ---
        cargarArticulosEnComboBox();
    }
    
    private void cargarArticulosEnComboBox() {
        jCBArticulo.removeAllItems();
        try {
            // Se asume la existencia de ArticuloDAO.listarArticulos()
            List<Articulo> listaArticulos = articuloDAO.listarArticulos(); 
            jCBArticulo.addItem("-- Seleccione Artículo --");
            for (Articulo a : listaArticulos) {
                // Se asume que Articulo tiene getCodigoBienNacional() y getNombre()
                jCBArticulo.addItem(a.getCodigoBienNacional() + " - " + a.getNombre());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar artículos.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // ----------------------------------------------------------------------
    // --- Lógica de la OC (Mantenida) ---
    // ----------------------------------------------------------------------

    /**
     * Agrega un artículo a la lista temporal (detallesOC) y actualiza la tabla.
     */
    private void agregarArticuloADetalle() {
        try {
            // 1. Validar la selección del ComboBox
            String articuloSeleccionado = (String) jCBArticulo.getSelectedItem();
            if (articuloSeleccionado == null || articuloSeleccionado.startsWith("--")) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un artículo.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 2. Obtener datos del formulario de detalle
            int cantidad = parseIntSafe(jFTCantidad);
            double precio = parseDoubleSafe(jFTPrecioUnitario);
            
            if (cantidad <= 0 || precio <= 0) {
                JOptionPane.showMessageDialog(this, "Cantidad y Precio deben ser mayores a cero.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Lógica para obtener el ID del artículo a partir del ComboBox
            String codigo = articuloSeleccionado.split(" - ")[0].trim();
            Articulo articulo = articuloDAO.buscarPorCodigo(codigo); 
            
            if (articulo == null) {
                JOptionPane.showMessageDialog(this, "Error al obtener datos del artículo.", "Error de Lógica", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 3. Crear el objeto Detalle
            OrdenCompraDetalle nuevoDetalle = new OrdenCompraDetalle();
            nuevoDetalle.setIdArticulo(articulo.getIdArticulo()); 
            nuevoDetalle.setCodigoArticulo(articulo.getCodigoBienNacional()); // Campo auxiliar
            nuevoDetalle.setNombreArticulo(articulo.getNombre()); // Campo auxiliar
            nuevoDetalle.setCantidadPedida(cantidad);
            nuevoDetalle.setPrecioUnitario(precio);
            nuevoDetalle.setSubTotal(cantidad * precio);
            
            // 4. Añadir a la lista temporal y actualizar la tabla
            detallesOC.add(nuevoDetalle);
            actualizarTablaDetalle();
            
            // 5. Limpiar campos de detalle para el siguiente artículo
            limpiarCamposDetalle();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar artículo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Elimina el detalle seleccionado de la lista temporal y actualiza la tabla.
     */
    private void removerArticuloDeDetalle() {
        int fila = jTablaDetalles.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar el artículo seleccionado del detalle?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            detallesOC.remove(fila);
            actualizarTablaDetalle();
        }
    }
    
    /**
     * Actualiza la JTable de detalles a partir de la lista temporal y calcula el total.
     */
    private void actualizarTablaDetalle() {
        // La tabla se limpia y se repuebla a partir de la lista detallesOC (Mantiene los datos)
        modeloTablaDetalle.setRowCount(0);
        double total = 0.0;
        
        for (OrdenCompraDetalle d : detallesOC) {
            total += d.getSubTotal();
            modeloTablaDetalle.addRow(new Object[] {
                d.getIdArticulo(),
                d.getCodigoArticulo(),
                d.getNombreArticulo(),
                d.getPrecioUnitario(),
                d.getCantidadPedida(),
                d.getSubTotal()
            });
        }
        
        jLTotalOC.setText(df.format(total));
    }
    
    /**
     * Intenta guardar la Orden de Compra completa en la base de datos.
     */
    private void guardarOrdenCompra() {
        if (detallesOC.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La orden de compra no tiene artículos.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // 1. Obtener datos del encabezado
            String proveedor = safeGet(jFTProveedor);
            String referencia = safeGet(jFTReferencia);
            
            if (proveedor.isEmpty() || referencia.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El Proveedor y la Referencia son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 2. Crear objeto OrdenCompra
            OrdenCompra oc = new OrdenCompra();
            oc.setNombreProveedor(proveedor);
            oc.setNumeroReferencia(referencia);
            oc.setIdUsuarioEmisor(usuarioActual.getIdUsuario()); 
            oc.setEstado("PENDIENTE");
            oc.setDetalles(detallesOC);

            // 3. Llamar al DAO para guardar (transaccional)
            // Se asume la existencia de ocDAO.guardarOrdenCompra()
            Long idGenerado = ocDAO.guardarOrdenCompra(oc);

            if (idGenerado != null) {
                JOptionPane.showMessageDialog(this, "Orden de Compra No. " + referencia + " creada con éxito (ID: " + idGenerado + ").", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Tras guardar, se asume que se limpia la pantalla para una nueva OC
                limpiarFormularioCompleto();
            } else {
                 JOptionPane.showMessageDialog(this, "Error desconocido al guardar la OC.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al crear la Orden de Compra: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void limpiarFormularioCompleto() {
        limpiarCamposEncabezado();
        limpiarCamposDetalle();
        detallesOC.clear();
        modeloTablaDetalle.setRowCount(0);
        jLTotalOC.setText(df.format(0.0));
    }
    
    private void limpiarCamposEncabezado() {
        jFTProveedor.setText("");
        jFTReferencia.setText("");
    }
    
    private void limpiarCamposDetalle() {
        jFTCantidad.setText("");
        jFTPrecioUnitario.setText("");
        jCBArticulo.setSelectedIndex(0);
    }
    
    // ----------------------------------------------------------------------
    // --- Métodos de Utilidad (Mantenidos) ---
    // ----------------------------------------------------------------------
    
    private String safeGet(JFormattedTextField f) {
        try { return (f.getText() == null) ? "" : f.getText().trim(); } catch (Exception e) { return ""; }
    }
    
    private int parseIntSafe(JFormattedTextField f) {
        return parseIntSafe(safeGet(f));
    }

    private int parseIntSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0 : Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

    private double parseDoubleSafe(JFormattedTextField f) {
        return parseDoubleSafe(safeGet(f));
    }
    private double parseDoubleSafe(String s) {
        try { return (s == null || s.trim().isEmpty()) ? 0.0 : Double.parseDouble(s.trim().replace(",", ".")); }
        catch (Exception e) { return 0.0; }
    }

    // ----------------------------------------------------------------------
    // --- Código de Interfaz Gráfica (Generated Code) ---
    // 🚨 AJUSTES: El uso de AbsoluteLayout se ha mantenido en jPanel4, pero
    // el layout principal (getContentPane) y jPanel1 deben estirarse.
    // ----------------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButtonVolver = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        
        // CAMPOS DE ENCABEZADO
        jLabelProveedor = new javax.swing.JLabel();
        jFTProveedor = new javax.swing.JFormattedTextField();
        jLabelReferencia = new javax.swing.JLabel();
        jFTReferencia = new javax.swing.JFormattedTextField();
        
        // CONTROLES DE DETALLE
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablaDetalles = new javax.swing.JTable();
        jLabelArticulo = new javax.swing.JLabel();
        jCBArticulo = new javax.swing.JComboBox<>();
        jLabelCantidad = new javax.swing.JLabel();
        jFTCantidad = new javax.swing.JFormattedTextField();
        jLabelPrecioUnitario = new javax.swing.JLabel();
        jFTPrecioUnitario = new javax.swing.JFormattedTextField();
        bAgregarDetalle = new javax.swing.JButton();
        bRemoverDetalle = new javax.swing.JButton();
        
        // TOTAL
        jLTotalTitulo = new javax.swing.JLabel();
        jLTotalOC = new javax.swing.JLabel();

        jPanel5 = new javax.swing.JPanel();
        bCrearOC = new javax.swing.JButton();
        bConsultarOC = new javax.swing.JButton();
        bLimpiar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        // 🚨 MODIFICACIÓN: jPanel1 usará BorderLayout para una mejor distribución vertical
        // El código original usaba AbsoluteLayout, esto lo cambiamos para que se estire
        jPanel1.setLayout(new java.awt.BorderLayout());
        // El contenido de jPanel1 se reubicará abajo

        // --- Panel Superior (Azul) ---
        jPanel2.setBackground(new java.awt.Color(13, 51, 131));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Crear Orden de Compra");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 0, 792, 110));

        jButtonVolver.setBackground(new java.awt.Color(13, 51, 131));
        jButtonVolver.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButtonVolver.setForeground(new java.awt.Color(255, 255, 255));
        jButtonVolver.setText("Volver");
        jButtonVolver.setBorder(null);
        jButtonVolver.addActionListener(this::jButtonVolverActionPerformed);
        jPanel2.add(jButtonVolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, 170, 40));

        // 🚨 AGREGAR jPanel2 al NORTE (NORTH) de jPanel1
        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);
        // El tamaño fijo de jPanel2 (110px de alto) se mantiene.

        // --- Panel Central (Formulario y Tabla) ---
        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        // Se mantiene AbsoluteLayout aquí para preservar la disposición de los campos a la izquierda.
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        // --- ENCABEZADO OC ---
        jLabelProveedor.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelProveedor.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelProveedor.setText("Proveedor (Nombre):");
        jPanel4.add(jLabelProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 180, 50));

        jFTProveedor.addActionListener(this::jFTProveedorActionPerformed);
        jPanel4.add(jFTProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 160, 50));

        jLabelReferencia.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelReferencia.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelReferencia.setText("Referencia/Nº OC:");
        jPanel4.add(jLabelReferencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 180, 50));

        jFTReferencia.addActionListener(this::jFTReferenciaActionPerformed);
        jPanel4.add(jFTReferencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 160, 50));
        
        // --- DETALLE OC (Controles para añadir) ---
        jLabelArticulo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelArticulo.setText("Artículo:");
        jPanel4.add(jLabelArticulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 180, 50));

        jCBArticulo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jCBArticulo.addActionListener(this::jCBArticuloActionPerformed);
        jPanel4.add(jCBArticulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 130, 160, 50));

        jLabelCantidad.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelCantidad.setText("Cantidad:");
        jPanel4.add(jLabelCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 180, 50));

        jFTCantidad.addActionListener(this::jFTCantidadActionPerformed);
        jPanel4.add(jFTCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 190, 160, 50));
        
        jLabelPrecioUnitario.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelPrecioUnitario.setText("Precio Unitario:");
        jPanel4.add(jLabelPrecioUnitario, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 180, 50));
        
        jFTPrecioUnitario.addActionListener(this::jFTPrecioUnitarioActionPerformed);
        jPanel4.add(jFTPrecioUnitario, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 250, 160, 50));

        bAgregarDetalle.setBackground(new java.awt.Color(0, 153, 51));
        bAgregarDetalle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        bAgregarDetalle.setForeground(new java.awt.Color(255, 255, 255));
        bAgregarDetalle.setText("Añadir Art.");
        bAgregarDetalle.addActionListener(this::bAgregarDetalleActionPerformed);
        jPanel4.add(bAgregarDetalle, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 170, 30));
        
        bRemoverDetalle.setBackground(new java.awt.Color(204, 0, 0));
        bRemoverDetalle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        bRemoverDetalle.setForeground(new java.awt.Color(255, 255, 255));
        bRemoverDetalle.setText("Quitar Sel.");
        bRemoverDetalle.addActionListener(this::bRemoverDetalleActionPerformed);
        jPanel4.add(bRemoverDetalle, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 310, 170, 30));


        // --- TABLA DE DETALLES ---
        jTablaDetalles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"ID Art.", "Nombre Art.", "Precio Unit.", "Cantidad", "Subtotal"}
        ));
        jScrollPane1.setViewportView(jTablaDetalles);

        // 🚨 AJUSTE DE RESTRICCIONES: El layout manager AbsoluteLayout no estira los componentes
        // verticalmente. Para que la tabla se estire, la añadiremos al CENTRO del panel principal.
        // Como no queremos reescribir todo el layout de jPanel4, lo haremos a través de un
        // contenedor auxiliar que use BorderLayout.
        
        // Usaremos el GroupLayout del JFrame para gestionar el jPanel4
        // En este ejemplo, mantendremos el AbsoluteLayout original de jPanel4, pero
        // reestructuraremos el resto para que la tabla se estire.
        
        // Ya que el código generado por NetBeans usa:
        // jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 0, 430, 280));
        // Esto tiene una altura fija (280) y una posición fija (370, 0).
        // Para que se estire, necesitamos que el componente jScrollPane1 sea el componente 'CENTER'
        // de un BorderLayout, o que sus restricciones en el GroupLayout permitan el estiramiento.
        
        // Dado que estamos limitados a cambiar solo GroupLayout o BorderLayout a nivel de contenedor principal:
        
        // 🚨 NUEVA ESTRUCTURA DENTRO DE JFRAME
        // 1. Reemplazamos el layout de jPanel4 por un BorderLayout temporal
        // 2. Colocamos el formulario (izquierda) y la tabla (centro)
        
        // 🛑 MANTENEMOS EL AbsoluteLayout EN jPanel4 para evitar una reescritura masiva de coordenadas.
        // En cambio, reescribiremos solo las restricciones de la tabla y los totales.
        
        // 🚨 RESTRICCIONES DE LA TABLA (jScrollPane1) Y TOTALES (jLTotalTitulo, jLTotalOC)
        // Se define una nueva altura y posición relativa para que ocupe el resto del espacio.
        // Como estamos en AbsoluteLayout, esto no es posible. 
        // Simplemente aumentaremos la altura predefinida de la tabla.

        jScrollPane1.setViewportView(jTablaDetalles);
        // Aumentamos la altura de 280 a un valor que se "estire" más dentro del layout.
        // Dado el uso de AbsoluteLayout, esta es la mejor solución sin cambiar el Layout Manager.
        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 0, 430, 450)); // Altura aumentada
        
        // --- TOTAL ---
        jLTotalTitulo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLTotalTitulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLTotalTitulo.setText("TOTAL OC:");
        // Ajustamos la posición vertical
        jPanel4.add(jLTotalTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 460, 100, 40)); 
        
        jLTotalOC.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLTotalOC.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLTotalOC.setText(df.format(0.0));
        // Ajustamos la posición vertical
        jPanel4.add(jLTotalOC, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 460, 310, 40));


        // --- PANEL DE BOTONES INFERIOR ---
        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 40, 5));

        bCrearOC.setBackground(new java.awt.Color(13, 51, 131));
        bCrearOC.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bCrearOC.setForeground(new java.awt.Color(255, 255, 255));
        bCrearOC.setText("REGISTRAR OC");
        bCrearOC.addActionListener(this::bCrearOCActionPerformed);
        jPanel5.add(bCrearOC);

        bConsultarOC.setBackground(new java.awt.Color(13, 51, 131));
        bConsultarOC.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bConsultarOC.setForeground(new java.awt.Color(255, 255, 255));
        bConsultarOC.setText("Consultar OC's");
        bConsultarOC.addActionListener(this::bConsultarOCActionPerformed);
        jPanel5.add(bConsultarOC);

        bLimpiar.setBackground(new java.awt.Color(13, 51, 131));
        bLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        bLimpiar.setText("Limpiar Todo");
        bLimpiar.addActionListener(this::bLimpiarActionPerformed);
        jPanel5.add(bLimpiar);

        // Ajustamos la posición vertical para que quede debajo de la tabla y totales.
        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 510, 800, 50)); 
        
        // El tamaño de jPanel4 original era 800x390. Lo extendemos para que quepa todo.
        // Como el layout principal se ha cambiado a BorderLayout, jPanel4 se colocará en el centro.

        // 🚨 AGREGAR jPanel4 al CENTRO (CENTER) de jPanel1
        jPanel1.add(jPanel4, java.awt.BorderLayout.CENTER);
        // Al estar en el CENTRO, jPanel4 se estirará a lo ancho y a lo alto.

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE) // Ancho estirable
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE) // Alto estirable
        );

        pack();
    }// </editor-fold>                        

    // ----------------------------------------------------------------------
    // --- Handlers de Eventos (Mantenidos) ---
    // ----------------------------------------------------------------------

    private void jButtonVolverActionPerformed(java.awt.event.ActionEvent evt) {                                             
        this.dispose();
        // Se asume la existencia de PrincipalVista
        // Asegúrate de que PrincipalVista también se maximice si es la ventana principal
        new PrincipalVista(this.usuarioActual).setVisible(true);
    }
    
    private void bCrearOCActionPerformed(java.awt.event.ActionEvent evt) {                                         
        guardarOrdenCompra();
    }
    
    private void bAgregarDetalleActionPerformed(java.awt.event.ActionEvent evt) {                                              
        agregarArticuloADetalle();
    }
    
    private void bRemoverDetalleActionPerformed(java.awt.event.ActionEvent evt) {                                               
        removerArticuloDeDetalle();
    }
    
    private void bLimpiarActionPerformed(java.awt.event.ActionEvent evt) {                                         
        limpiarFormularioCompleto();
    }
    
    /**
     * Muestra la nueva ventana de consulta/listado de Órdenes de Compra.
     */
    private void bConsultarOCActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // Abre la nueva vista de consulta
        // Se asume la existencia de VistaConsultaOrdenCompra
        VistaConsultaOrdenCompra consulta = new VistaConsultaOrdenCompra(this);
        consulta.setVisible(true);
    }
    
    private void jFTProveedorActionPerformed(java.awt.event.ActionEvent evt) {}
    private void jFTReferenciaActionPerformed(java.awt.event.ActionEvent evt) {}
    private void jCBArticuloActionPerformed(java.awt.event.ActionEvent evt) {}
    private void jFTCantidadActionPerformed(java.awt.event.ActionEvent evt) {}
    private void jFTPrecioUnitarioActionPerformed(java.awt.event.ActionEvent evt) {}

    // ----------------------------------------------------------------------
    // --- Declaración de Variables (Mantenidas) ---
    // ----------------------------------------------------------------------
    private javax.swing.JButton bAgregarDetalle;
    private javax.swing.JButton bConsultarOC;
    private javax.swing.JButton bCrearOC;
    private javax.swing.JButton bLimpiar;
    private javax.swing.JButton bRemoverDetalle;
    private javax.swing.JButton jButtonVolver;
    private javax.swing.JComboBox<String> jCBArticulo;
    private javax.swing.JFormattedTextField jFTCantidad;
    private javax.swing.JFormattedTextField jFTPrecioUnitario;
    private javax.swing.JFormattedTextField jFTProveedor;
    private javax.swing.JFormattedTextField jFTReferencia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelArticulo;
    private javax.swing.JLabel jLabelCantidad;
    private javax.swing.JLabel jLabelPrecioUnitario;
    private javax.swing.JLabel jLabelProveedor;
    private javax.swing.JLabel jLabelReferencia;
    private javax.swing.JLabel jLTotalOC;
    private javax.swing.JLabel jLTotalTitulo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTablaDetalles;
    // End of variables declaration                  
}