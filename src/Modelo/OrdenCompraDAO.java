package Modelo;

import Modelo.OrdenCompra;
import Modelo.OrdenCompraDetalle;
import util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.sql.Date; 


public class OrdenCompraDAO {

    public OrdenCompraDAO() {
        
    }


    public List<OrdenCompra> listarOrdenesCompra() throws SQLException {
        List<OrdenCompra> lista = new ArrayList<>();
        final String SQL = "SELECT oc.*, u.nombre_usuario AS nombre_usuario_emisor FROM ORDEN_COMPRA oc "
                         + "JOIN USUARIO u ON oc.id_usuario_emisor = u.id_usuario ORDER BY oc.fecha_emision DESC";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
               
                OrdenCompra oc = new OrdenCompra(
                    rs.getString("nombre_proveedor"),
                    rs.getLong("id_usuario_emisor"), 
                    rs.getString("numero_referencia")
                );
                
                oc.setIdOrdenCompra(rs.getLong("id_orden_compra"));
                oc.setNombreUsuarioEmisor(rs.getString("nombre_usuario_emisor"));
                oc.setEstado(rs.getString("estado"));
                oc.setMontoTotal(rs.getDouble("monto_total"));
                
                
                Date sqlDate = rs.getDate("fecha_emision");
                if (sqlDate != null) {
                    oc.setFechaEmision(sqlDate.toLocalDate());
                }
                
                lista.add(oc);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Órdenes de Compra: " + e.getMessage());
            throw e;
        }
        return lista;
    }


    public OrdenCompra obtenerOCPorId(Long idOrdenCompra) throws SQLException {
        OrdenCompra orden = null;
        Connection conn = null;

        try {
            conn = ConexionBD.conectar();
            if (conn == null) {
                throw new SQLException("Error: La conexión a la base de datos es nula.");
            }

            // 1. Obtener Encabezado de la Orden de Compra
            final String SQL_HEADER = "SELECT oc.*, u.nombre_usuario AS nombre_usuario_emisor FROM ORDEN_COMPRA oc "
                                    + "JOIN USUARIO u ON oc.id_usuario_emisor = u.id_usuario WHERE oc.id_orden_compra = ?";
            
            try (PreparedStatement psHeader = conn.prepareStatement(SQL_HEADER)) {
                psHeader.setLong(1, idOrdenCompra);
                try (ResultSet rs = psHeader.executeQuery()) {
                    if (rs.next()) {
                        // CORRECCIÓN: Ajustar el orden de los parámetros del constructor (proveedor, emisor, referencia)
                        orden = new OrdenCompra(
                            rs.getString("nombre_proveedor"), 
                            rs.getLong("id_usuario_emisor"),  
                            rs.getString("numero_referencia") 
                        );
                        orden.setIdOrdenCompra(rs.getLong("id_orden_compra"));
                        orden.setNombreUsuarioEmisor(rs.getString("nombre_usuario_emisor"));
                        orden.setEstado(rs.getString("estado"));
                        orden.setMontoTotal(rs.getDouble("monto_total"));
                        
                        
                        Date sqlDate = rs.getDate("fecha_emision");
                        if (sqlDate != null) {
                            orden.setFechaEmision(sqlDate.toLocalDate());
                        }
                    } else {
                        return null; 
                    }
                }
            }

            
            if (orden != null) {
                final String SQL_DETAILS = "SELECT d.id_detalle, d.id_articulo_fk, d.cantidad_pedida, d.precio_unitario, d.subtotal, a.codigo, a.nombre AS nombre_articulo FROM ORDEN_COMPRA_DETALLE d "
                                        + "JOIN ARTICULO a ON d.id_articulo_fk = a.id_articulo WHERE d.id_orden_compra_fk = ?";
                List<OrdenCompraDetalle> detalles = new ArrayList<>();
                
                try (PreparedStatement psDetails = conn.prepareStatement(SQL_DETAILS)) {
                    psDetails.setLong(1, idOrdenCompra);
                    try (ResultSet rs = psDetails.executeQuery()) {
                        while (rs.next()) {
                            // Uso del constructor que ahora existe en OrdenCompraDetalle.java
                            // Orden: idDetalle, codigo, nombre_articulo, cantidad, precio_unitario
                            OrdenCompraDetalle detalle = new OrdenCompraDetalle(
                                rs.getLong("id_detalle"),
                                rs.getString("codigo"), 
                                rs.getString("nombre_articulo"),
                                rs.getInt("cantidad_pedida"),
                                rs.getDouble("precio_unitario")
                            );
                            detalle.setIdOrdenCompra(idOrdenCompra);
                            detalle.setIdArticulo(rs.getLong("id_articulo_fk"));
                            detalle.setSubTotal(rs.getDouble("subtotal"));
                            detalles.add(detalle);
                        }
                    }
                }
                orden.setDetalles(detalles);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener Orden de Compra por ID: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return orden;
    }



    public Long guardarOrdenCompra(OrdenCompra orden) throws SQLException {
        Connection conn = null;
        Long idGenerado = null;

        try {
            conn = ConexionBD.conectar();
             if (conn == null) {
                throw new SQLException("Error: La conexión a la base de datos es nula. No se puede ejecutar la transacción.");
            }
            
            conn.setAutoCommit(false); 

            
            idGenerado = guardarEncabezado(conn, orden);
            orden.setIdOrdenCompra(idGenerado);

            
            if (orden.getDetalles() != null && !orden.getDetalles().isEmpty()) {
                for (OrdenCompraDetalle detalle : orden.getDetalles()) {
                    detalle.setIdOrdenCompra(idGenerado);
                    guardarDetalle(conn, detalle);
                }
            }
            
            
            actualizarMontoTotal(conn, idGenerado);

            conn.commit(); 
            return idGenerado;

        } catch (SQLException e) {
            System.err.println("Error en transacción de OrdenCompra: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); 
                conn.close();
            }
        }
    }


    private Long guardarEncabezado(Connection conn, OrdenCompra orden) throws SQLException {
        Long idGenerado = null;
        
        final String SQL = "INSERT INTO ORDEN_COMPRA (nombre_proveedor, id_usuario_emisor, numero_referencia, estado, monto_total, fecha_emision) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, orden.getNombreProveedor());  
            ps.setLong(2, orden.getIdUsuarioEmisor());
            ps.setString(3, orden.getNumeroReferencia());
            ps.setString(4, orden.getEstado());
            ps.setDouble(5, orden.getMontoTotal() != null ? orden.getMontoTotal() : 0.00);
            
            // Manejo seguro de fecha de emisión (usa LocalDate que ahora es compatible con OrdenCompra)
            LocalDate fechaEmision = orden.getFechaEmision();
            if (fechaEmision == null) {
                fechaEmision = LocalDate.now();
            }
            ps.setDate(6, java.sql.Date.valueOf(fechaEmision));
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idGenerado = rs.getLong(1);
                } else {
                    throw new SQLException("Fallo al obtener el ID generado del encabezado de la Orden de Compra.");
                }
            }
        }
        return idGenerado;
    }


    private void guardarDetalle(Connection conn, OrdenCompraDetalle detalle) throws SQLException {
        final String SQL = "INSERT INTO ORDEN_COMPRA_DETALLE (id_orden_compra_fk, id_articulo_fk, cantidad_pedida, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        
        double subtotal = detalle.getCantidadPedida() * detalle.getPrecioUnitario();
        detalle.setSubTotal(subtotal);

        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setLong(1, detalle.getIdOrdenCompra());
            ps.setLong(2, detalle.getIdArticulo());
            ps.setInt(3, detalle.getCantidadPedida());
            ps.setDouble(4, detalle.getPrecioUnitario());
            ps.setDouble(5, detalle.getSubTotal());  
            ps.executeUpdate();
        }
    }


    public void actualizarMontoTotal(Connection conn, Long idOrdenCompra) throws SQLException {
        final String SQL_SUMA = "SELECT SUM(subtotal) FROM ORDEN_COMPRA_DETALLE WHERE id_orden_compra_fk = ?";
        final String SQL_UPDATE = "UPDATE ORDEN_COMPRA SET monto_total = ? WHERE id_orden_compra = ?";
        
        Double nuevoTotal = 0.0;
        
        try (PreparedStatement psSuma = conn.prepareStatement(SQL_SUMA)) {
            psSuma.setLong(1, idOrdenCompra);
            try (ResultSet rs = psSuma.executeQuery()) {
                if (rs.next()) {
                    nuevoTotal = rs.getDouble(1);  
                    if (rs.wasNull()) {
                        nuevoTotal = 0.0;
                    }
                }
            }
        }

        try (PreparedStatement psUpdate = conn.prepareStatement(SQL_UPDATE)) {
            psUpdate.setDouble(1, nuevoTotal);
            psUpdate.setLong(2, idOrdenCompra);
            psUpdate.executeUpdate();
        }
    }
}