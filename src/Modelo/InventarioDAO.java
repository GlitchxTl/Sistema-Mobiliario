package Modelo;

import Modelo.ArticuloStock; // Necesitarás crear esta clase modelo
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    private String jdbcURL = "jdbc:mysql://localhost:3306/tu_base_de_datos"; // ¡ACTUALIZA TU URL!
    private String jdbcUsername = "tu_usuario"; // ¡ACTUALIZA TU USUARIO!
    private String jdbcPassword = "tu_password"; // ¡ACTUALIZA TU CONTRASEÑA!

    public InventarioDAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar el driver JDBC: " + e.getMessage());
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    // Método para obtener el Top N de artículos con más stock
    public List<ArticuloStock> getTopNArticulosConMasStock(int topN) throws SQLException {
        List<ArticuloStock> listaArticulos = new ArrayList<>();
        // Query de la vista V_InventarioGeneral para ordenar por TotalStock descendente
        String SQL = "SELECT NombreArticulo, TotalStock FROM V_InventarioGeneral ORDER BY TotalStock DESC LIMIT ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, topN);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                listaArticulos.add(new ArticuloStock(rs.getString("NombreArticulo"), rs.getInt("TotalStock")));
            }
        }
        return listaArticulos;
    }

    // Método para obtener el Top N de artículos con menos stock
    public List<ArticuloStock> getTopNArticulosConMenosStock(int topN) throws SQLException {
        List<ArticuloStock> listaArticulos = new ArrayList<>();
        // Query de la vista V_InventarioGeneral para ordenar por TotalStock ascendente
        String SQL = "SELECT NombreArticulo, TotalStock FROM V_InventarioGeneral ORDER BY TotalStock ASC LIMIT ?";
        try (Connection connection = getConnection();
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



