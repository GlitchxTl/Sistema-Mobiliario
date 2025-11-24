package Modelo; // ⚠️ Ajustar el paquete si la clase DAO está en otro lugar

import Modelo.DatoGrafico; // Para la estadística de Stock
import Modelo.ValorUbicacionDTO; // Para la estadística de Valor
import util.ConexionBD; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Asegúrate de importar SQLException
import java.util.ArrayList;
import java.util.List;

public class EstadisticasDAO {

    // ==========================================================
    // MÉTODO 1: Obtener Top Stock (EXISTENTE)
    // ==========================================================
    public List<DatoGrafico> obtenerTopStock(int limite, boolean mayorStock) {
        List<DatoGrafico> lista = new ArrayList<>();
        
        String orden = mayorStock ? "DESC" : "ASC";
        
        // Consultamos tu VISTA V_InventarioGeneral
        String sql = "SELECT NombreArticulo, TotalStock FROM V_InventarioGeneral ORDER BY TotalStock " + orden + " LIMIT ?";

        try (Connection con = ConexionBD.conectar(); // ⚠️ Ajusta esta línea a tu método de conexión
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, limite);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new DatoGrafico(
                        rs.getString("NombreArticulo"),
                        rs.getInt("TotalStock")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    // ==========================================================
    // MÉTODO 2: Obtener Valor Total por Ubicación (NUEVO)
    // ==========================================================
    public List<ValorUbicacionDTO> obtenerValorTotalPorUbicacion() throws SQLException {
        
        List<ValorUbicacionDTO> resultados = new ArrayList<>();
        Connection conn = null; // Usaremos la conexión de tu método existente
        
        String SQL_QUERY = 
            // Reutilizamos la lógica del StockNeto y SaldosConsolidados 
            "WITH StockNeto AS (" +
                " SELECT M.id_articulo, M.id_ubicacion_destino AS id_ubicacion, " +
                " SUM(CASE WHEN M.tipo IN ('ENTRADA', 'TRASLADO') THEN M.cantidad ELSE 0 END) AS CantidadEntrada, " +
                " 0 AS CantidadSalida " +
                " FROM Movimiento M " +
                " WHERE M.id_ubicacion_destino IS NOT NULL " +
                " GROUP BY M.id_articulo, M.id_ubicacion_destino " +
                " UNION ALL " +
                " SELECT M.id_articulo, M.id_ubicacion_origen AS id_ubicacion, " +
                " 0 AS CantidadEntrada, " +
                " SUM(CASE WHEN M.tipo IN ('SALIDA', 'TRASLADO') THEN M.cantidad ELSE 0 END) AS CantidadSalida " +
                " FROM Movimiento M " +
                " WHERE M.id_ubicacion_origen IS NOT NULL " +
                " GROUP BY M.id_articulo, M.id_ubicacion_origen " +
            "), " +
            "SaldosConsolidados AS (" +
                " SELECT id_articulo, id_ubicacion, " +
                " SUM(CantidadEntrada) - SUM(CantidadSalida) AS TotalStock " +
                " FROM StockNeto " +
                " GROUP BY id_articulo, id_ubicacion " +
            ") " +
            // Cálculo final del valor total por ubicación
            "SELECT " +
                " U.nombre AS NombreUbicacion, " +
                " SUM(SC.TotalStock * A.costo) AS ValorTotalPorUbicacion " + 
            "FROM " +
                " SaldosConsolidados SC " +
            "JOIN " +
                " articulo A ON SC.id_articulo = A.id_articulo " +
            "JOIN " +
                " ubicacion U ON SC.id_ubicacion = U.id_ubicacion " +
            "WHERE " +
                " SC.TotalStock > 0 " + 
            "GROUP BY " +
                " U.nombre " +
            "ORDER BY " +
                " ValorTotalPorUbicacion DESC";


        // Usamos try-with-resources y la conexión de tu DAO, pero eliminamos el 'conn'
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_QUERY);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String nombreUbicacion = rs.getString("NombreUbicacion");
                Double valorTotal = rs.getDouble("ValorTotalPorUbicacion");

                // Manejo de valores nulos si la DB devuelve null
                if (rs.wasNull()) {
                    valorTotal = 0.0;
                }
                
                resultados.add(new ValorUbicacionDTO(nombreUbicacion, valorTotal));
            }
            
        } // El try-with-resources cierra PreparedStatement, ResultSet y Connection
        return resultados;
    }
}
