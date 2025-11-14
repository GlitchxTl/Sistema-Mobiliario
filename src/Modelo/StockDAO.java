package Modelo;

import util.ConexionBD;
import java.sql.*;

public class StockDAO{
    public int obtenerStock(int idArticulo, int idUbicacion) throws SQLException {
        String sql = "SELECT cantidad_actual FROM stock_ubicacion "
                   + "WHERE id_articulo = ? AND id_ubicacion = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idArticulo);
            stmt.setInt(2, idUbicacion);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cantidad_actual");
                }
            }
        }
        return 0; // Si no existe registro, stock es 0
    }
    
    public void actualizarStock(int idArticulo, int idUbicacion, int cantidadDelta) throws SQLException {
        String updateSql = "UPDATE stock_ubicacion SET cantidad_actual = cantidad_actual + ? "
                         + "WHERE id_articulo = ? AND id_ubicacion = ?";
        
        String insertSql = "INSERT INTO stock_ubicacion (id_articulo, id_ubicacion, cantidad_actual) "
                         + "VALUES (?, ?, ?) "
                         + "ON DUPLICATE KEY UPDATE cantidad_actual = cantidad_actual + ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            
            stmt.setInt(1, idArticulo);
            stmt.setInt(2, idUbicacion);
            stmt.setInt(3, cantidadDelta);
            stmt.setInt(4, cantidadDelta);
            
            stmt.executeUpdate();
        }
    }
}
