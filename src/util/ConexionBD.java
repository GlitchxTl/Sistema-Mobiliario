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
            // Registrar driver, si falta lanzará ClassNotFoundException
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Convertimos a SQLException para que los llamadores solo manejen SQLException
            throw new SQLException("Driver JDBC no encontrado: " + e.getMessage(), e);
        }
        // Si el driver está presente, obtener la conexión (puede lanzar SQLException)
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


