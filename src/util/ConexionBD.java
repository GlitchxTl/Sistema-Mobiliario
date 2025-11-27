package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:mariadb://127.0.0.1:3307/gestion_activos";
    private static final String USER = "root";
    private static final String PASSWORD = "9876";

    public static Connection conectar() throws SQLException {
        try {
            
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            
            throw new SQLException("Driver JDBC no encontrado: " + e.getMessage(), e);
        }
        
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


