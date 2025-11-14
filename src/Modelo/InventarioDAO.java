package Modelo;

import util.ConexionBD;
import java.sql.*;

public class InventarioDAO {

    // Stock total de un artículo (suma de todas las ubicaciones)
    public int obtenerStockTotal(int idArticulo) throws SQLException {
        String sql = "SELECT COALESCE(SUM(stock),0) AS total FROM inventario WHERE id_articulo = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idArticulo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
                return 0;
            }
        }
    }

    // Stock en una ubicación concreta (0 si no existe)
    public int obtenerStockPorUbicacion(int idArticulo, int idUbicacion) throws SQLException {
        String sql = "SELECT stock FROM inventario WHERE id_articulo = ? AND id_ubicacion = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idArticulo);
            ps.setInt(2, idUbicacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("stock");
                return 0;
            }
        }
    }

    // Upsert: crea o actualiza stock para id_articulo + id_ubicacion
    public boolean upsertStock(int idArticulo, int idUbicacion, int nuevaCantidad) throws SQLException {
        // primero intentar UPDATE
        String sqlUpdate = "UPDATE inventario SET stock = ? WHERE id_articulo = ? AND id_ubicacion = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
            ps.setInt(1, nuevaCantidad);
            ps.setInt(2, idArticulo);
            ps.setInt(3, idUbicacion);
            int filas = ps.executeUpdate();
            if (filas > 0) return true;
        }

        // si no actualizó, INSERT
        String sqlInsert = "INSERT INTO inventario (id_articulo, id_ubicacion, stock) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps2 = con.prepareStatement(sqlInsert)) {
            ps2.setInt(1, idArticulo);
            ps2.setInt(2, idUbicacion);
            ps2.setInt(3, nuevaCantidad);
            int filas2 = ps2.executeUpdate();
            return filas2 > 0;
        }
    }
}



