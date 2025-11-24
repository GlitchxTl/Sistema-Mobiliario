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

    /**
     * Obtiene los datos del Top N de artículos con mayor o menor stock.
     * @param limite El número de artículos a incluir en el Top.
     * @param mayorStock Si es true, obtiene el mayor stock (DESC); si es false, el menor (ASC).
     * @return Lista de DatoGrafico.
     */
    public List<DatoGrafico> obtenerTopStock(int limite, boolean mayorStock) {
        // El DAO ya maneja el try-catch interno para obtenerTopStock.
        return modelo.obtenerTopStock(limite, mayorStock);
    }
    
    // --- NUEVO MÉTODO DE CONTROLADOR ---
    
    /**
     * Obtiene el valor total del inventario agrupado por ubicación.
     * Muestra un mensaje de error si ocurre un problema de base de datos.
     * @return Lista de ValorUbicacionDTO o null si falla.
     */
    public List<ValorUbicacionDTO> obtenerValorPorUbicacion() {
        try {
            // Llama al nuevo método del DAO
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
