package Modelo;

import util.ConexionBD; 
import Modelo.ArticuloStock;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp; 

public class InventarioDAO {
    

    public InventarioDAO() {}


    public Double getStockPorUbicacion(int idArticulo, int idUbicacion) throws SQLException {
        String SQL = "SELECT stock FROM inventario WHERE id_articulo = ? AND id_ubicacion = ?";
        Double stock = 0.0;
        
        try (Connection connection = ConexionBD.conectar(); 
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            
            preparedStatement.setInt(1, idArticulo);
            preparedStatement.setInt(2, idUbicacion);
            
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                
                stock = rs.getDouble("stock"); 
            }
        }
        return stock;
    }
    
    public boolean existeStockArticuloEnUbicacion(int idArticulo, int idUbicacion) throws SQLException {
        String SQL = "SELECT 1 FROM inventario WHERE id_articulo = ? AND id_ubicacion = ?";
        try (Connection connection = ConexionBD.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            
            preparedStatement.setInt(1, idArticulo);
            preparedStatement.setInt(2, idUbicacion);
            
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        }
    }


    public boolean aumentarStock(Connection conn, int idArticulo, int idUbicacion, double cantidad) throws SQLException {
        if (conn == null) throw new SQLException("Conexión nula para aumentar stock");

        String UPDATE_SQL = "UPDATE inventario SET stock = stock + ?, fecha_actualizacion = CURRENT_TIMESTAMP() WHERE id_articulo = ? AND id_ubicacion = ?";
        String INSERT_SQL = "INSERT INTO inventario (id_articulo, id_ubicacion, stock, fecha_actualizacion) VALUES (?, ?, ?, CURRENT_TIMESTAMP())";

        // 1. Intentar actualizar el registro existente
        try (PreparedStatement updateStmt = conn.prepareStatement(UPDATE_SQL)) {
            updateStmt.setDouble(1, cantidad);
            updateStmt.setInt(2, idArticulo);
            updateStmt.setInt(3, idUbicacion);
            
            int filasAfectadas = updateStmt.executeUpdate();

            if (filasAfectadas > 0) {
                return true; 
            }
        }
        
        
        try (PreparedStatement insertStmt = conn.prepareStatement(INSERT_SQL)) {
            insertStmt.setInt(1, idArticulo);
            insertStmt.setInt(2, idUbicacion);
            insertStmt.setDouble(3, cantidad);
            
            int filasAfectadas = insertStmt.executeUpdate();
            return filasAfectadas > 0; 
        }
    }



    public boolean descontarStock(Connection conn, int idArticulo, int idUbicacion, double cantidad) throws SQLException {
        if (conn == null) throw new SQLException("Conexión nula para descontar stock");

        String UPDATE_SQL = "UPDATE inventario SET stock = stock - ?, fecha_actualizacion = CURRENT_TIMESTAMP() WHERE id_articulo = ? AND id_ubicacion = ?";
        
        // Usa la conexión pasada por parámetro (conn)
        try (PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_SQL)) {
            
            preparedStatement.setDouble(1, cantidad);
            preparedStatement.setInt(2, idArticulo);
            preparedStatement.setInt(3, idUbicacion);
            
            int filasAfectadas = preparedStatement.executeUpdate();
            return filasAfectadas > 0;
        }
    }
    

    public List<ArticuloStock> listarArticulosBajoStock(int umbralStockBajo) throws SQLException {
        List<ArticuloStock> listaArticulos = new ArrayList<>();
        

        String SQL = "SELECT a.nombre AS NombreArticulo, COALESCE(CAST(SUM(i.stock) AS SIGNED), 0) AS TotalStock " +
                     "FROM articulo a " +
                     "LEFT JOIN inventario i ON a.id_articulo = i.id_articulo " +
                     "GROUP BY a.nombre " +
                     "HAVING TotalStock <= ?";
        
        try (Connection connection = ConexionBD.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            
            preparedStatement.setInt(1, umbralStockBajo);
            
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                listaArticulos.add(new ArticuloStock(rs.getString("NombreArticulo"), rs.getInt("TotalStock")));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar artículos con bajo stock: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return listaArticulos;
    }


    public List<ArticuloStock> getTopNArticulosConMasStock(int topN) throws SQLException {
        List<ArticuloStock> listaArticulos = new ArrayList<>();
        String SQL = "SELECT NombreArticulo, TotalStock FROM V_InventarioGeneral ORDER BY TotalStock DESC LIMIT ?";
        
        try (Connection connection = ConexionBD.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            
             preparedStatement.setInt(1, topN);
             ResultSet rs = preparedStatement.executeQuery();
             while (rs.next()) {
                
                 listaArticulos.add(new ArticuloStock(rs.getString("NombreArticulo"), rs.getInt("TotalStock")));
             }
        }
        return listaArticulos;
    }

    
    public List<ArticuloStock> getTopNArticulosConMenosStock(int topN) throws SQLException {
        List<ArticuloStock> listaArticulos = new ArrayList<>();
        String SQL = "SELECT NombreArticulo, TotalStock FROM V_InventarioGeneral ORDER BY TotalStock ASC LIMIT ?";
        
        try (Connection connection = ConexionBD.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            
             preparedStatement.setInt(1, topN);
             ResultSet rs = preparedStatement.executeQuery();
             while (rs.next()) {
                 
                 listaArticulos.add(new ArticuloStock(rs.getString("NombreArticulo"), rs.getInt("TotalStock")));
             }
        }
        return listaArticulos;
    }
}