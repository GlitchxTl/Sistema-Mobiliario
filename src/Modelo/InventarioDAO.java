package Modelo;

import util.ConexionBD; // Importe de la clase de utilidad de conexión
import Modelo.ArticuloStock;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp; 

public class InventarioDAO {
    
    // Constructor vacío, la conexión se obtiene a través de ConexionBD.
    public InventarioDAO() {}

    // =========================================================================
    // === MÉTODOS DE CONSULTA Y ACTUALIZACIÓN (Usan stock DECIMAL) ============
    // =========================================================================

    public Double getStockPorUbicacion(int idArticulo, int idUbicacion) throws SQLException {
        String SQL = "SELECT stock FROM inventario WHERE id_articulo = ? AND id_ubicacion = ?";
        Double stock = 0.0;
        
        try (Connection connection = ConexionBD.conectar(); 
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            
            preparedStatement.setInt(1, idArticulo);
            preparedStatement.setInt(2, idUbicacion);
            
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                // Usa getDouble para el campo 'stock' de la tabla, que es DECIMAL(12,3)
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

    /**
     * Aumenta el stock de un artículo en una ubicación. Si el registro no existe, 
     * lo inserta (UPSERT).
     * @param conn Conexión de la base de datos (para manejo de transacciones).
     * @param idArticulo ID del artículo.
     * @param idUbicacion ID de la ubicación.
     * @param cantidad Cantidad a aumentar (double).
     * @return true si se actualizó o insertó el stock.
     * @throws SQLException 
     */
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
                return true; // Actualización exitosa
            }
        }
        
        // 2. Si no se actualizó (porque no existe), intentar insertar el nuevo registro
        try (PreparedStatement insertStmt = conn.prepareStatement(INSERT_SQL)) {
            insertStmt.setInt(1, idArticulo);
            insertStmt.setInt(2, idUbicacion);
            insertStmt.setDouble(3, cantidad);
            
            int filasAfectadas = insertStmt.executeUpdate();
            return filasAfectadas > 0; // Inserción exitosa
        }
    }


    /**
     * Descuenta stock de un artículo en una ubicación usando una conexión
     * de base de datos ya existente.
     * * @param conn Conexión de la base de datos (para manejo de transacciones).
     * @param idArticulo ID del artículo.
     * @param idUbicacion ID de la ubicación.
     * @param cantidad Cantidad a descontar.
     * @return true si se descontó el stock.
     * @throws SQLException 
     */
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
    
    // =========================================================================
    // === MÉTODOS DE REPORTE (Ajustado para rs.getInt("TotalStock")) =========
    // =========================================================================

    /**
     * Obtiene una lista de artículos cuyo stock total (suma del inventario por ubicación) 
     * es menor o igual al umbral especificado.
     * * @param umbralStockBajo El valor máximo de stock (int) para considerar un artículo como "bajo stock".
     * @return Una lista de objetos ArticuloStock con stock bajo.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    public List<ArticuloStock> listarArticulosBajoStock(int umbralStockBajo) throws SQLException {
        List<ArticuloStock> listaArticulos = new ArrayList<>();
        
        // Se calcula la suma del stock de inventario por artículo, se utiliza LEFT JOIN 
        // para incluir artículos sin registros de inventario (stock 0), y se filtra por el umbral.
        // Se castea la suma a INT (o SIGNED/INTEGER dependiendo de la DB) para que coincida 
        // con el modelo ArticuloStock (int stock).
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

    // Método para obtener el Top N de artículos con más stock
    public List<ArticuloStock> getTopNArticulosConMasStock(int topN) throws SQLException {
        List<ArticuloStock> listaArticulos = new ArrayList<>();
        String SQL = "SELECT NombreArticulo, TotalStock FROM V_InventarioGeneral ORDER BY TotalStock DESC LIMIT ?";
        
        try (Connection connection = ConexionBD.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            
             preparedStatement.setInt(1, topN);
             ResultSet rs = preparedStatement.executeQuery();
             while (rs.next()) {
                 // ⭐ AJUSTE REQUERIDO: Usamos getInt() para TotalStock.
                 listaArticulos.add(new ArticuloStock(rs.getString("NombreArticulo"), rs.getInt("TotalStock")));
             }
        }
        return listaArticulos;
    }

    // Método para obtener el Top N de artículos con menos stock
    public List<ArticuloStock> getTopNArticulosConMenosStock(int topN) throws SQLException {
        List<ArticuloStock> listaArticulos = new ArrayList<>();
        String SQL = "SELECT NombreArticulo, TotalStock FROM V_InventarioGeneral ORDER BY TotalStock ASC LIMIT ?";
        
        try (Connection connection = ConexionBD.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            
             preparedStatement.setInt(1, topN);
             ResultSet rs = preparedStatement.executeQuery();
             while (rs.next()) {
                 // ⭐ AJUSTE REQUERIDO: Usamos getInt() para TotalStock.
                 listaArticulos.add(new ArticuloStock(rs.getString("NombreArticulo"), rs.getInt("TotalStock")));
             }
        }
        return listaArticulos;
    }
}