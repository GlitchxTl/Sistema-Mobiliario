package Modelo;

import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UbicacionDAO {

    public boolean crear(Ubicacion u) throws SQLException {
        u.setCapacidad(u.getAltura() * u.getAnchura() * u.getProfundidad());
        
        
        String sql = "INSERT INTO ubicacion (nombre, altura, anchura, profundidad, capacidad, capacidad_restante, descripcion, deshabilitado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, util.util.capitalizar (u.getNombre()));
            ps.setDouble(2, u.getAltura());
            ps.setDouble(3, u.getAnchura());
            ps.setDouble(4, u.getProfundidad());
            ps.setDouble(5, u.getCapacidad());
            ps.setDouble(6, u.getCapacidad()); // Inicializar capacidad_restante
            ps.setString(7, util.util.capitalizar (u.getDescripcion()));
            ps.setBoolean(8, u.isDeshabilitado()); // Lectura del nuevo campo

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) u.setId_ubicacion(keys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean actualizar(Ubicacion u) throws SQLException {
        u.setCapacidad(u.getAltura() * u.getAnchura() * u.getProfundidad());
        
        
        String sql = "UPDATE ubicacion SET nombre=?, altura=?, anchura=?, profundidad=?, capacidad=?, descripcion=?, deshabilitado=? WHERE id_ubicacion=?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setDouble(2, u.getAltura());
            ps.setDouble(3, u.getAnchura());
            ps.setDouble(4, u.getProfundidad());
            ps.setDouble(5, u.getCapacidad());
            ps.setString(6, u.getDescripcion());
            ps.setBoolean(7, u.isDeshabilitado()); // Lectura del nuevo campo
            ps.setInt(8, u.getId_ubicacion());
            return ps.executeUpdate() > 0;
        }
    }

    
    public boolean eliminar(int id_ubicacion) throws SQLException {
        String sql = "DELETE FROM ubicacion WHERE id_ubicacion=?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id_ubicacion);
            return ps.executeUpdate() > 0;
        }
    }
    

    public boolean actualizarEstado(int idUbicacion, boolean deshabilitar) throws SQLException {
        String sql = "UPDATE ubicacion SET deshabilitado = ? WHERE id_ubicacion = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setBoolean(1, deshabilitar); 
            ps.setInt(2, idUbicacion);
            
            return ps.executeUpdate() > 0;
            
        }
    }
    


    public Ubicacion obtenerPorId(int id_ubicacion) throws SQLException {
        // Seleccionar 'deshabilitado'
        String sql = "SELECT id_ubicacion, nombre, altura, anchura, profundidad, capacidad, capacidad_restante, descripcion, deshabilitado FROM ubicacion WHERE id_ubicacion=?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id_ubicacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearUbicacion(rs);
            }
        }
        return null;
    }

    public List<Ubicacion> listar() throws SQLException {
        List<Ubicacion> res = new ArrayList<>();
        
        String sql = "SELECT id_ubicacion, nombre, altura, anchura, profundidad, capacidad, capacidad_restante, descripcion, deshabilitado FROM ubicacion ORDER BY nombre";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                res.add(mapearUbicacion(rs));
            }
        }
        return res;
    }
    
    public List<Ubicacion> buscar(String nombre) throws SQLException {
        List<Ubicacion> lista = new ArrayList<>();
        
        String sql = "SELECT id_ubicacion, nombre, altura, anchura, profundidad, capacidad, capacidad_restante, descripcion, deshabilitado FROM ubicacion WHERE nombre LIKE ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                   lista.add(mapearUbicacion(rs));
                }
            }
        }
        return lista;
    }
    
    public List<Ubicacion> listarConEspacioSuficiente(double espacioRequerido, int idUbicacionAExcluir) throws SQLException {
        List<Ubicacion> res = new ArrayList<>();
        
        String sql = "SELECT id_ubicacion, nombre, altura, anchura, profundidad, capacidad, capacidad_restante, descripcion, deshabilitado FROM ubicacion WHERE capacidad_restante >= ? AND id_ubicacion != ? AND deshabilitado = FALSE ORDER BY nombre";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setDouble(1, espacioRequerido);
            ps.setInt(2, idUbicacionAExcluir);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(mapearUbicacion(rs));
                }
            }
        }
        return res;
    }


     public int obtenerStockPorArticuloYUbicacion(int idArticulo, int idUbicacion) throws SQLException {
        
        String sql = "SELECT SUM(stock) FROM vw_stock_por_ubicacion WHERE id_articulo = ? AND id_ubicacion = ?";

        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idArticulo);
            ps.setInt(2, idUbicacion);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // El stock total
                }
            }
        }
        return 0;
    }
    

    private Ubicacion mapearUbicacion(ResultSet rs) throws SQLException {
        Ubicacion u = new Ubicacion(
            rs.getInt("id_ubicacion"),
            rs.getString("nombre"),
            rs.getDouble("altura"),
            rs.getDouble("anchura"),
            rs.getDouble("profundidad"),
            rs.getString("descripcion"),
            rs.getBoolean("deshabilitado") // Lectura del nuevo campo
        );
        u.setCapacidad(rs.getDouble("capacidad"));
        u.setCapacidadRestante(rs.getDouble("capacidad_restante"));
        return u;
    }


    public Integer obtenerIdPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id_ubicacion FROM ubicacion WHERE nombre = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_ubicacion");
            }
        }
        return null;
    }

    public Integer crearSiNoExisteYObtenerId(String nombre) throws SQLException {
        Integer id = obtenerIdPorNombre(nombre);
        if (id != null) return id;

        Ubicacion nueva = new Ubicacion();
        nueva.setNombre(nombre);
        nueva.setAltura(1);
        nueva.setAnchura(1);
        nueva.setProfundidad(1);
        nueva.setDescripcion("Creada automáticamente");
        nueva.setDeshabilitado(false); 
        if (crear(nueva)) return nueva.getId_ubicacion();
        return null;
    }
}
