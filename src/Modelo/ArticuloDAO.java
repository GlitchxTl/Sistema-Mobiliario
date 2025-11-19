package Modelo;

import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticuloDAO {

    public boolean crearArticulo(Articulo articulo) throws SQLException {
        String sql = "INSERT INTO articulo (nombre, codigo_bien_nacional, categoria, altura, anchura, profundidad, espacio_unitario, deshabilitado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, articulo.getNombre());
            ps.setString(2, articulo.getCodigoBienNacional());
            ps.setString(3, articulo.getCategoria());
            ps.setDouble(4, articulo.getAltura());
            ps.setDouble(5, articulo.getAnchura());
            ps.setDouble(6, articulo.getProfundidad());
            ps.setDouble(7, articulo.getEspacioUnitario());
            ps.setBoolean(8, articulo.isDeshabilitado());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) articulo.setIdArticulo(keys.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public List<Articulo> listarArticulos() throws SQLException {
        List<Articulo> articulos = new ArrayList<>();
        String sql = "SELECT id_articulo, nombre, codigo_bien_nacional, categoria, altura, anchura, profundidad, espacio_unitario, deshabilitado FROM articulo";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                articulos.add(mapearArticulo(rs));
            }
        }
        return articulos;
    }

    public boolean actualizarArticulo(Articulo articulo) throws SQLException {
        String sql = "UPDATE articulo SET nombre=?, codigo_bien_nacional=?, categoria=?, altura=?, anchura=?, profundidad=?, espacio_unitario=?, deshabilitado=? WHERE id_articulo=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, articulo.getNombre());
            ps.setString(2, articulo.getCodigoBienNacional());
            ps.setString(3, articulo.getCategoria());
            ps.setDouble(4, articulo.getAltura());
            ps.setDouble(5, articulo.getAnchura());
            ps.setDouble(6, articulo.getProfundidad());
            ps.setDouble(7, articulo.getEspacioUnitario());
            ps.setBoolean(8, articulo.isDeshabilitado());
            ps.setInt(9, articulo.getIdArticulo());
            return ps.executeUpdate() > 0;
        }
    }

    // Mantienes el método de eliminación "hard" por si es necesario para otros fines,
    // pero el botón de la vista ya no lo usa.
    public boolean eliminarArticuloPorCodigo(String codigoBienNacional) throws SQLException {
        String sql = "DELETE FROM articulo WHERE codigo_bien_nacional=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigoBienNacional);
            return ps.executeUpdate() > 0;
        }
    }
    
    // Mantienes el método de eliminación "hard" por si es necesario para otros fines.
    public boolean eliminarArticuloPorId(int idArticulo) throws SQLException { 
        String sql = "DELETE FROM articulo WHERE id_articulo=?";
        try (Connection conn = ConexionBD.conectar(); 
             PreparedStatement ps = conn.prepareStatement(sql)) { 
            ps.setInt(1, idArticulo); 
            return ps.executeUpdate() > 0; 
        } 
    }

    // ⭐ Método para Soft Delete (Actualizar estado deshabilitado) ⭐
    public boolean actualizarEstado(int idArticulo, boolean deshabilitar) throws SQLException {
        String sql = "UPDATE articulo SET deshabilitado = ? WHERE id_articulo = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setBoolean(1, deshabilitar); 
            ps.setInt(2, idArticulo);
            
            return ps.executeUpdate() > 0;
            
        }
    }

    public Articulo obtenerArticuloPorId(int idArticulo) throws SQLException {
        String sql = "SELECT id_articulo, nombre, codigo_bien_nacional, categoria, altura, anchura, profundidad, espacio_unitario, deshabilitado FROM articulo WHERE id_articulo=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idArticulo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearArticulo(rs);
            }
        }
        return null;
    }

    public List<Articulo> buscarArticulos(String nombre, String codigoBien) {
        List<Articulo> lista = new ArrayList<>();
        String sql = "SELECT id_articulo, nombre, codigo_bien_nacional, categoria, altura, anchura, profundidad, espacio_unitario, deshabilitado FROM articulo WHERE nombre LIKE ? OR codigo_bien_nacional LIKE ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nombre + "%");
            stmt.setString(2, "%" + codigoBien + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapearArticulo(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Articulo> buscarPorCategoria(String categoria) {
        List<Articulo> lista = new ArrayList<>();
        String sql = "SELECT id_articulo, nombre, codigo_bien_nacional, categoria, altura, anchura, profundidad, espacio_unitario, deshabilitado FROM articulo WHERE categoria=?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapearArticulo(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Articulo> buscarArticulosCombinado(String nombre, String codigoBien, String categoria) throws SQLException {
        List<Articulo> lista = new ArrayList<>();
        List<Object> params = new ArrayList<>(); 
        
        StringBuilder sql = new StringBuilder("SELECT id_articulo, nombre, codigo_bien_nacional, categoria, altura, anchura, profundidad, espacio_unitario, deshabilitado FROM articulo WHERE 1=1");

        if (nombre != null && !nombre.trim().isEmpty()) {
            sql.append(" AND nombre LIKE ?");
            params.add("%" + nombre + "%");
        }
        if (codigoBien != null && !codigoBien.trim().isEmpty()) {
            sql.append(" AND codigo_bien_nacional LIKE ?");
            params.add("%" + codigoBien + "%");
        }
        if (categoria != null && !categoria.trim().isEmpty()) {
            sql.append(" AND categoria = ?");
            params.add(categoria);
        }

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int i = 1;
            for (Object param : params) {
                stmt.setObject(i++, param);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearArticulo(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return lista;
    }

    private Articulo mapearArticulo(ResultSet rs) throws SQLException {
        Articulo a = new Articulo();
        a.setIdArticulo(rs.getInt("id_articulo"));
        a.setNombre(rs.getString("nombre"));
        a.setCodigoBienNacional(rs.getString("codigo_bien_nacional"));
        a.setCategoria(rs.getString("categoria"));
        a.setAltura(rs.getDouble("altura"));
        a.setAnchura(rs.getDouble("anchura"));
        a.setProfundidad(rs.getDouble("profundidad"));
        a.setEspacioUnitario(rs.getDouble("espacio_unitario"));
        a.setDeshabilitado(rs.getBoolean("deshabilitado")); 
        return a;
    }
}




