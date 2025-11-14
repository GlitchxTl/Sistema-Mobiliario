package Modelo;

import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Ubicaciones — maneja persistencia en BD.
 */
public class UbicacionDAO {

    public boolean crear(Ubicacion u) throws SQLException {
        u.setCapacidad(u.getAltura() * u.getAnchura() * u.getProfundidad());
        
        // CORRECCIÓN: Al crear, capacidad_restante debe ser igual a capacidad.
        String sql = "INSERT INTO ubicacion (nombre, altura, anchura, profundidad, capacidad, capacidad_restante, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNombre());
            ps.setDouble(2, u.getAltura());
            ps.setDouble(3, u.getAnchura());
            ps.setDouble(4, u.getProfundidad());
            ps.setDouble(5, u.getCapacidad());
            ps.setDouble(6, u.getCapacidad()); // Inicializar capacidad_restante
            ps.setString(7, u.getDescripcion());

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
        
        // CORRECCIÓN: Si cambian las dimensiones (y por ende la capacidad),
        // capacidad_restante debería re-evaluarse. Aquí solo actualizamos
        // los campos sin tocar capacidad_restante, confiando en que el
        // trigger maneje movimientos. Si se cambia la capacidad, la lógica
        // de capacidad_restante debe ser manejada manualmente o por un trigger de UPDATE.
        // Si no hay trigger de UPDATE, el valor quedará desfasado si cambian dimensiones.
        String sql = "UPDATE ubicacion SET nombre=?, altura=?, anchura=?, profundidad=?, capacidad=?, descripcion=? WHERE id_ubicacion=?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setDouble(2, u.getAltura());
            ps.setDouble(3, u.getAnchura());
            ps.setDouble(4, u.getProfundidad());
            ps.setDouble(5, u.getCapacidad());
            ps.setString(6, u.getDescripcion());
            ps.setInt(7, u.getId_ubicacion());
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

    public Ubicacion obtenerPorId(int id_ubicacion) throws SQLException {
        String sql = "SELECT * FROM ubicacion WHERE id_ubicacion=?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id_ubicacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ubicacion u = new Ubicacion(
                        rs.getInt("id_ubicacion"),
                        rs.getString("nombre"),
                        rs.getDouble("altura"),
                        rs.getDouble("anchura"),
                        rs.getDouble("profundidad"),
                        rs.getString("descripcion")
                    );
                    u.setCapacidad(rs.getDouble("capacidad"));
                    // CORRECCIÓN: Leer capacidad restante de la DB, no calcularla.
                    u.setCapacidadRestante(rs.getDouble("capacidad_restante"));
                    return u;
                }
            }
        }
        return null;
    }

    public List<Ubicacion> listar() throws SQLException {
        List<Ubicacion> res = new ArrayList<>();
        String sql = "SELECT * FROM ubicacion ORDER BY nombre";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Ubicacion u = new Ubicacion(
                    rs.getInt("id_ubicacion"),
                    rs.getString("nombre"),
                    rs.getDouble("altura"),
                    rs.getDouble("anchura"),
                    rs.getDouble("profundidad"),
                    rs.getString("descripcion")
                );
                u.setCapacidad(rs.getDouble("capacidad"));
                // CORRECCIÓN: Leer capacidad restante de la DB, no calcularla.
                u.setCapacidadRestante(rs.getDouble("capacidad_restante"));
                res.add(u);
            }
        }
        return res;
    }

    /**
     * Obtiene el ID de una ubicación a partir de su nombre.
     */
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

    /**
     * Crea la ubicación si no existe y devuelve su ID.
     */
    public Integer crearSiNoExisteYObtenerId(String nombre) throws SQLException {
        Integer id = obtenerIdPorNombre(nombre);
        if (id != null) return id;

        Ubicacion nueva = new Ubicacion();
        nueva.setNombre(nombre);
        nueva.setAltura(1);
        nueva.setAnchura(1);
        nueva.setProfundidad(1);
        nueva.setDescripcion("Creada automáticamente");
        if (crear(nueva)) return nueva.getId_ubicacion();
        return null;
    }

    // ELIMINADO: Este método ya no es necesario, el trigger se encarga de esto.
    // private double calcularCapacidadRestante(int idUbicacion, double capacidadTotal) {...}
    
    public List<Ubicacion> buscar(String nombre) throws SQLException {
        List<Ubicacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM ubicacion WHERE nombre LIKE ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ubicacion u = new Ubicacion();
                    u.setId_ubicacion(rs.getInt("id_ubicacion"));
                    u.setNombre(rs.getString("nombre"));
                    u.setCapacidad(rs.getDouble("capacidad"));
                    // CORRECCIÓN: Leer capacidad restante de la DB, no calcularla.
                    u.setCapacidadRestante(rs.getDouble("capacidad_restante"));
                    u.setDescripcion(rs.getString("descripcion"));
                    lista.add(u);
                }
            }
        }
        return lista;
    }
}
