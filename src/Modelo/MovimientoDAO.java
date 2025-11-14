package Modelo;

import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para registrar y consultar movimientos de artículos.
 * Compatible con la estructura actual de la tabla 'movimiento'.
 */
public class MovimientoDAO {

    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();

    /**
     * Inserta un movimiento en la tabla movimiento.
     */
    public boolean insertarMovimiento(Movimiento m) throws SQLException {
        if (m == null) throw new SQLException("Movimiento nulo");

        String sql = """
              INSERT INTO movimiento (
                  id_articulo, tipo, cantidad, id_usuario,
                  id_ubicacion_origen, id_ubicacion_destino,
                  motivo, entregado,
                  fecha_vencimiento, costo, donado, fecha
              ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
              """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, m.getIdArticulo());
            ps.setString(2, m.getTipo());
            ps.setInt(3, m.getCantidad());

            if (m.getIdUsuario() <= 0) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, m.getIdUsuario());

            if (m.getIdUbicacionOrigen() == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, m.getIdUbicacionOrigen());

            if (m.getIdUbicacionDestino() == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, m.getIdUbicacionDestino());

            ps.setString(7, m.getMotivo());

            // Parámetro 8: entregado
            ps.setString(8, m.getEntregado());

            // Parámetro 9: fecha_vencimiento
            if (m.getFechaVencimiento() != null)
                ps.setTimestamp(9, m.getFechaVencimiento());
            else
                ps.setNull(9, Types.TIMESTAMP);

            // Parámetro 10: costo
            if (m.getCosto() != null)
                ps.setDouble(10, m.getCosto());
            else
                ps.setNull(10, Types.DOUBLE);

            // Parámetro 11: donado
            ps.setBoolean(11, m.isDonado());

            return ps.executeUpdate() > 0;
        }
    }

    // --- Consultas por tipo de movimiento ---
    public List<Movimiento> listarEntradas() throws SQLException {
        return listarPorTipo("ENTRADA");
    }

    public List<Movimiento> listarSalidas() throws SQLException {
        return listarPorTipo("SALIDA");
    }

    public List<Movimiento> listarTraslados() throws SQLException {
        return listarPorTipo("TRASLADO");
    }

    public List<Movimiento> listarPorTipo(String tipo) throws SQLException {
        List<Movimiento> lista = new ArrayList<>();
        String sql = """
            SELECT 
                m.id_movimiento, m.tipo, m.cantidad, m.fecha,
                m.entregado, m.costo, m.fecha_vencimiento,
                a.nombre AS articulo,
                u1.nombre AS origen, u2.nombre AS destino
            FROM movimiento m
            JOIN articulo a ON m.id_articulo = a.id_articulo
            LEFT JOIN ubicacion u1 ON m.id_ubicacion_origen = u1.id_ubicacion
            LEFT JOIN ubicacion u2 ON m.id_ubicacion_destino = u2.id_ubicacion
            WHERE m.tipo = ?
            ORDER BY m.fecha DESC
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movimiento mov = new Movimiento();
                    mov.setIdMovimiento(rs.getInt("id_movimiento"));
                    mov.setTipo(rs.getString("tipo"));
                    mov.setCantidad(rs.getInt("cantidad"));
                    mov.setNombreArticulo(rs.getString("articulo"));
                    mov.setNombreUbicacionOrigen(rs.getString("origen"));
                    mov.setNombreUbicacionDestino(rs.getString("destino"));
                    
                    // Mapeo de campos
                    mov.setEntregado(rs.getString("entregado"));
                    
                    // Manejo de Costo (Double, puede ser NULL)
                    double costo = rs.getDouble("costo");
                    if (!rs.wasNull()) {
                        mov.setCosto(costo);
                    } else {
                        mov.setCosto(null);
                    }
                    
                    // Manejo de Fecha de Vencimiento (Timestamp, puede ser NULL)
                    mov.setFechaVencimiento(rs.getTimestamp("fecha_vencimiento"));
                    
                    mov.setFechaHora(rs.getTimestamp("fecha"));
                    lista.add(mov);
                }
            }
        }
        return lista;
    }

    /** Lista todos los movimientos, sin filtrar por tipo */
    public List<Movimiento> listarTodos() throws SQLException {
        List<Movimiento> lista = new ArrayList<>();
        String sql = """
            SELECT 
                m.id_movimiento, m.tipo, m.cantidad, m.fecha,
                m.entregado, m.costo, m.fecha_vencimiento,
                a.nombre AS articulo,
                u1.nombre AS origen, u2.nombre AS destino
            FROM movimiento m
            JOIN articulo a ON m.id_articulo = a.id_articulo
            LEFT JOIN ubicacion u1 ON m.id_ubicacion_origen = u1.id_ubicacion
            LEFT JOIN ubicacion u2 ON m.id_ubicacion_destino = u2.id_ubicacion
            ORDER BY m.fecha DESC
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Movimiento mov = new Movimiento();
                mov.setIdMovimiento(rs.getInt("id_movimiento"));
                mov.setTipo(rs.getString("tipo"));
                mov.setCantidad(rs.getInt("cantidad"));
                mov.setNombreArticulo(rs.getString("articulo"));
                mov.setNombreUbicacionOrigen(rs.getString("origen"));
                mov.setNombreUbicacionDestino(rs.getString("destino"));
                
                // Mapeo de campos
                mov.setEntregado(rs.getString("entregado"));
                
                // Manejo de Costo (Double, puede ser NULL)
                double costo = rs.getDouble("costo");
                if (!rs.wasNull()) {
                    mov.setCosto(costo);
                } else {
                    mov.setCosto(null);
                }
                
                // Manejo de Fecha de Vencimiento (Timestamp, puede ser NULL)
                mov.setFechaVencimiento(rs.getTimestamp("fecha_vencimiento"));
                
                mov.setFechaHora(rs.getTimestamp("fecha"));
                lista.add(mov);
            }
        }
        return lista;
    }

    public List<Object[]> obtenerResumenPorArticulo(int idArticulo) throws SQLException {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT año, total_movimientos FROM resumen_movimientos WHERE id_articulo = ? ORDER BY año DESC";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idArticulo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{ rs.getInt("año"), rs.getInt("total_movimientos") });
                }
            }
        }
        return lista;
    }
}






