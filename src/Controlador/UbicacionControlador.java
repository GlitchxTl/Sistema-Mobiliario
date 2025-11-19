package Controlador;

import Modelo.Ubicacion;
import Modelo.UbicacionDAO; 
import java.sql.SQLException; 
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para Ubicaciones.
 * Delega todas las operaciones de persistencia al UbicacionDAO.
 */
public class UbicacionControlador {

    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();

    /**
     * Devuelve la lista de todas las ubicaciones desde la base de datos.
     * (Renombrado para ser más claro para la Vista)
     */
    public List<Ubicacion> obtenerTodasUbicaciones() {
        try {
            return ubicacionDAO.listar(); 
        } catch (SQLException e) {
            System.err.println("Error al listar ubicaciones: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); 
        }
    }
    
    // ⭐ CAMBIO CLAVE: Nuevo método para Soft Delete/Habilitar ⭐
    public boolean actualizarEstadoDeshabilitado(int idUbicacion, boolean nuevoEstado) {
        try {
            return ubicacionDAO.actualizarEstado(idUbicacion, nuevoEstado);
        } catch (SQLException e) {
            System.err.println("Error al actualizar el estado de deshabilitado de ubicación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Ubicacion> buscarPorNombre(String nombreParcial) {
        try {
            if (nombreParcial == null || nombreParcial.trim().isEmpty()) {
                return ubicacionDAO.listar(); 
            }
            return ubicacionDAO.buscar(nombreParcial.trim()); 
        } catch (SQLException e) {
            System.err.println("Error al buscar ubicaciones por nombre: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean crearUbicacion(Ubicacion u) {
        if (u == null) return false;
        try {
            return ubicacionDAO.crear(u); 
        } catch (SQLException e) {
            System.err.println("Error al crear ubicación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarUbicacion(Ubicacion u) {
        if (u == null || u.getId_ubicacion() <= 0) return false;
        try {
            return ubicacionDAO.actualizar(u); 
        } catch (SQLException e) {
            System.err.println("Error al actualizar ubicación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarUbicacionPorId(int id_ubicacion) {
        try {
            return ubicacionDAO.eliminar(id_ubicacion); 
        } catch (SQLException e) {
            System.err.println("Error al eliminar ubicación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<String> obtenerNombres() {
        List<String> nombres = new ArrayList<>();
        try {
            List<Ubicacion> lista = ubicacionDAO.listar(); 
            for (Ubicacion u : lista) {
                nombres.add(u.getNombre());
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombres de ubicaciones: " + e.getMessage());
            e.printStackTrace();
        }
        return nombres;
    }

    public Ubicacion obtenerPorId(int id_ubicacion) {
        try {
            return ubicacionDAO.obtenerPorId(id_ubicacion); 
        } catch (SQLException e) {
            System.err.println("Error al obtener ubicación por ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}