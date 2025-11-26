package Modelo; 

import util.ConexionBD; // Usamos tu clase de conexión
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para gestionar los reportes de Gastos Operativos.
 * ADAPTADO para obtener DATOS AGREGADOS MENSUALES desde la vista v_gastos_operativos.
 */
public class GastoOperativoDAO {

    // -------------------------------------------------------------
    // --- Constructor ---
    // -------------------------------------------------------------
    
    public GastoOperativoDAO() {
    }

    // -------------------------------------------------------------
    // --- Método para obtener Gastos (Reporte Mensual) ---
    // -------------------------------------------------------------

    /**
     * Obtiene los datos agregados (Año, Mes, Total Gastado, # Transacciones) 
     * desde la vista V_GASTOS_OPERATIVOS, que ahora es un reporte mensual.
     * @return Lista de objetos GastoOperativo (ahora representando un reporte mensual).
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<GastoOperativo> obtenerGastosOperativos() throws SQLException {
        List<GastoOperativo> gastosReporte = new ArrayList<>();
        
        // 🚨 CAMBIO CRÍTICO: La consulta debe coincidir con las 4 columnas de la vista AGREGADA
        final String SQL = 
            "SELECT anio, mes, total_gastos_mes, num_transacciones " +
            "FROM v_gastos_operativos ORDER BY anio DESC, mes DESC"; 

        // Usamos try-with-resources para asegurar el cierre de recursos automáticamente
        try (Connection conn = ConexionBD.conectar(); // Usando tu utilidad de conexión
             PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                // 🚨 CAMBIO CRÍTICO: Mapeamos a los nuevos campos de reporte
                GastoOperativo reporteMensual = new GastoOperativo(
                    rs.getInt("anio"),
                    rs.getInt("mes"),
                    rs.getDouble("total_gastos_mes"),
                    rs.getLong("num_transacciones")
                );
                gastosReporte.add(reporteMensual);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los gastos operativos desde la vista: " + e.getMessage());
            // Relanzar la excepción
            throw e; 
        }
        return gastosReporte;
    }
}
