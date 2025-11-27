package Controlador;

import Modelo.EstadisticasDAO;
import Modelo.DatoGrafico;
import Modelo.ValorUbicacionDTO;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;

public class EstadisticaControlador {

    private EstadisticasDAO modelo;

    public EstadisticaControlador() {
        this.modelo = new EstadisticasDAO();
    }


    public List<DatoGrafico> obtenerTopStock(int limite, boolean mayorStock) {
        // El DAO ya maneja el try-catch interno para obtenerTopStock.
        return modelo.obtenerTopStock(limite, mayorStock);
    }
    

    

    public List<ValorUbicacionDTO> obtenerValorPorUbicacion() {
        try {

            return modelo.obtenerValorTotalPorUbicacion();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de Base de Datos al obtener la estadística de valor por ubicación: " + e.getMessage(), 
                "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null; 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error inesperado al procesar la estadística: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null; 
        }
    }
}
