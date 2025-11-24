package Controlador;

import Modelo.ArticuloDAO;
import Modelo.Articulo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticuloControlador {
    private final ArticuloDAO articuloDAO = new ArticuloDAO();

    // MODIFICADO: Se añade int idUsuario
    public boolean crearArticulo(Articulo articulo, int idUsuario) {
        try {
            // Pasar idUsuario al DAO
            return articuloDAO.crearArticulo(articulo, idUsuario);
        } catch (SQLException e) {
            System.err.println("Error al crear artículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Articulo> obtenerTodosArticulos() {
        try {
            return articuloDAO.listarArticulos();
        } catch (SQLException e) {
            System.err.println("Error al obtener artículos: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // MODIFICADO: Se añade int idUsuario
    public boolean actualizarArticulo(Articulo articulo, int idUsuario) {
        try {
            // Pasar idUsuario al DAO
            return articuloDAO.actualizarArticulo(articulo, idUsuario);
        } catch (SQLException e) {
            System.err.println("Error al actualizar artículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarArticulo(String codigoBienNacional) {
        try {
            return articuloDAO.eliminarArticuloPorCodigo(codigoBienNacional);
        } catch (SQLException e) {
            System.err.println("Error al eliminar artículo por código: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarArticuloPorId(int idArticulo) {
        try {
            return articuloDAO.eliminarArticuloPorId(idArticulo);
        } catch (SQLException e) {
            System.err.println("Error al eliminar artículo por id: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Nuevo método para Soft Delete
    public boolean actualizarEstadoDeshabilitado(int idArticulo, boolean nuevoEstado) {
        try {
            return articuloDAO.actualizarEstado(idArticulo, nuevoEstado);
        } catch (SQLException e) {
            System.err.println("Error al actualizar el estado de deshabilitado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Articulo obtenerArticuloPorId(int id) {
        try {
            return articuloDAO.obtenerArticuloPorId(id);
        } catch (SQLException e) {
            System.err.println("Error al obtener artículo por id: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Articulo> buscarArticulos(String nombre, String codigoBien) {
        try {
            return articuloDAO.buscarArticulos(nombre, codigoBien);
        } catch (Exception e) {
            System.err.println("Error al buscar artículos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Articulo> buscarPorCategoria(String categoria) {
        try {
            return articuloDAO.buscarPorCategoria(categoria);
        } catch (Exception e) {
            System.err.println("Error al buscar por categoría: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Articulo> buscarArticulosCombinado(String nombre, String codigo, String categoria) {
        try {
            String catFiltro = categoria;
            if (categoria != null && (categoria.equalsIgnoreCase("Todas") || categoria.startsWith("Seleccione"))) {
                catFiltro = ""; 
            }
            
            return articuloDAO.buscarArticulosCombinado(nombre, codigo, catFiltro);
            
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda combinada: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}