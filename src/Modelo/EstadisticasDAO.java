package Modelo; 

import Modelo.DatoGrafico; 
import Modelo.ValorUbicacionDTO; 
import util.ConexionBD; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.util.ArrayList;
import java.util.List;

public class EstadisticasDAO {


    public List<DatoGrafico> obtenerTopStock(int limite, boolean mayorStock) {
        List<DatoGrafico> lista = new ArrayList<>();
        
        String orden = mayorStock ? "DESC" : "ASC";
        
        // Consultamos tu VISTA V_InventarioGeneral
        String sql = "SELECT NombreArticulo, TotalStock FROM V_InventarioGeneral ORDER BY TotalStock " + orden + " LIMIT ?";

        try (Connection con = ConexionBD.conectar(); 
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
    

    public List<ValorUbicacionDTO> obtenerValorTotalPorUbicacion() throws SQLException {
        
        List<ValorUbicacionDTO> resultados = new ArrayList<>();
        Connection conn = null;
        
        String SQL_QUERY = 
            
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


        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_QUERY);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String nombreUbicacion = rs.getString("NombreUbicacion");
                Double valorTotal = rs.getDouble("ValorTotalPorUbicacion");

                
                if (rs.wasNull()) {
                    valorTotal = 0.0;
                }
                
                resultados.add(new ValorUbicacionDTO(nombreUbicacion, valorTotal));
            }
            
        } 
        return resultados;
    }
}
