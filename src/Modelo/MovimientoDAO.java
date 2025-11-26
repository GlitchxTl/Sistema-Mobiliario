package Modelo;

import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoDAO {

    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();

    // -------------------------------------------------------------------
    // --- Insertar Movimiento ---
    // -------------------------------------------------------------------
    /**
     * Inserta un nuevo movimiento utilizando una conexión preexistente.
     * Esta firma es necesaria para permitir que la operación sea parte de una transacción
     * manejada por el controlador.
     * @param conn Conexión de la base de datos (debe ser abierta y gestionada externamente).
     * @param m Objeto Movimiento a insertar.
     * @return true si la inserción fue exitosa.
     * @throws SQLException 
     */
    public boolean insertarMovimiento(Connection conn, Movimiento m) throws SQLException {
        if (m == null) throw new SQLException("Movimiento nulo");
        if (conn == null) throw new SQLException("Conexión nula"); // Verificación adicional

        // ⭐ CAMBIO: Se usan costo_divisa y costo_bs en lugar de solo costo
        String sql = """
             INSERT INTO movimiento (
                 id_articulo, tipo, cantidad, id_usuario,
                 id_ubicacion_origen, id_ubicacion_destino,
                 motivo, entregado,
                 fecha_vencimiento, costo_divisa, costo_bs, donado, fecha
             ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
             """;

        // Se utiliza la conexión 'conn' pasada por parámetro.
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

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
            // Nota: Se asume que util.util.capitalizar existe
            ps.setString(8, util.util.capitalizar(m.getEntregado())); 


            if (m.getFechaVencimiento() != null)
                ps.setTimestamp(9, m.getFechaVencimiento());
            else
                ps.setNull(9, Types.TIMESTAMP);

            // ⭐ Lógica de Costos (Solo para ENTRADAS) ⭐
            // Se asume que getCosto() es el costo en divisa. getCostoBs() lo calcula/contiene.
            if ("ENTRADA".equalsIgnoreCase(m.getTipo()) && m.getCosto() != null) {
                // costo_divisa
                ps.setDouble(10, m.getCosto()); 
                // costo_bs (se usa el método que prioriza el valor ya seteado en el modelo)
                double costoBs = m.getCostoBs();
                if (costoBs >= 0) { // Si el valor es válido (>=0 o el valor seteado por el controlador)
                    ps.setDouble(11, costoBs);
                } else {
                    ps.setNull(11, Types.DOUBLE);
                }
            } else {
                // Para SALIDA y TRASLADO, los costos son NULL
                ps.setNull(10, Types.DOUBLE); // costo_divisa
                ps.setNull(11, Types.DOUBLE); // costo_bs
            }

            // El índice de donado se mueve al 12
            ps.setBoolean(12, m.isDonado());

            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------------------
    // ⭐ Obtener Movimiento por ID (Para cargar formulario) ⭐
    // -------------------------------------------------------------------
    public Movimiento obtenerPorId(int id) throws SQLException {
        // ⭐ CAMBIO: Se usa costo_divisa y costo_bs en el SELECT
        String sql = """
             SELECT 
                 id_movimiento, id_articulo, tipo, cantidad, id_usuario,
                 id_ubicacion_origen, id_ubicacion_destino, motivo, entregado,
                 fecha_vencimiento, costo_divisa, costo_bs, donado, fecha
             FROM movimiento
             WHERE id_movimiento = ?
             """;
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Nota: mapearResultSet ahora lee 'costo_divisa' y 'costo_bs'
                    return mapearResultSet(rs); 
                }
            }
        }
        return null;
    }
    
    // -------------------------------------------------------------------
    // ⭐ Buscar y Filtrar Movimientos (Para consultar) ⭐
    // -------------------------------------------------------------------
    
    /**
     * Método específico para buscar Entradas con filtros opcionales.
     */
    public List<Movimiento> buscarEntradas(Integer idArticulo, Integer idUbicacion) throws SQLException {
        return buscarMovimientos("ENTRADA", idArticulo, idUbicacion);
    }
    
    /**
     * Método específico para buscar Salidas con filtros opcionales.
     */
    public List<Movimiento> buscarSalidas(Integer idArticulo, Integer idUbicacion) throws SQLException {
        return buscarMovimientos("SALIDA", idArticulo, idUbicacion);
    }

    /**
     * Método específico para buscar Traslados con filtros opcionales.
     */
    public List<Movimiento> buscarTraslados(Integer idArticulo, Integer idUbicacion) throws SQLException {
        return buscarMovimientos("TRASLADO", idArticulo, idUbicacion);
    }


    /**
     * Método genérico para buscar movimientos por tipo y filtros opcionales.
     */
    public List<Movimiento> buscarMovimientos(String tipo, Integer idArticulo, Integer idUbicacion) throws SQLException {
        List<Movimiento> lista = new ArrayList<>();
        
        // Construcción dinámica del SQL
        StringBuilder sqlBuilder = new StringBuilder("""
            SELECT 
                m.id_movimiento, m.tipo, m.cantidad, m.fecha, m.entregado, 
                -- ⭐ CAMBIO: Se usa costo_divisa y se renombra a 'costo' para compatibilidad con mapearResultSet
                m.costo_divisa AS costo, m.costo_bs, 
                m.fecha_vencimiento, m.donado, m.motivo,
                m.id_articulo, m.id_ubicacion_destino, m.id_ubicacion_origen,
                a.nombre AS articulo,
                u1.nombre AS origen, u2.nombre AS destino
            FROM movimiento m
            JOIN articulo a ON m.id_articulo = a.id_articulo
            LEFT JOIN ubicacion u1 ON m.id_ubicacion_origen = u1.id_ubicacion
            LEFT JOIN ubicacion u2 ON m.id_ubicacion_destino = u2.id_ubicacion
            WHERE UPPER(m.tipo) = UPPER(?)
            """);

        // Filtros dinámicos
        if (idArticulo != null) {
            sqlBuilder.append(" AND m.id_articulo = ?");
        }
        
        if (idUbicacion != null) {
            if ("ENTRADA".equalsIgnoreCase(tipo) || "TRASLADO".equalsIgnoreCase(tipo)) {
                 sqlBuilder.append(" AND m.id_ubicacion_destino = ?"); 
            } else if ("SALIDA".equalsIgnoreCase(tipo)) {
                 sqlBuilder.append(" AND m.id_ubicacion_origen = ?");
            } 
        }
        
        sqlBuilder.append(" ORDER BY m.fecha DESC");
        
        // DEBUG
        System.out.println("--- DEBUG CONSULTA ---");
        System.out.println("SQL: " + sqlBuilder.toString());
        System.out.println("Filtros -> Tipo: " + tipo + " | ID Art: " + idArticulo + " | ID Ubic: " + idUbicacion);
        System.out.println("----------------------");


        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sqlBuilder.toString())) {
            
            int index = 1;
            ps.setString(index++, tipo);
            
            if (idArticulo != null) {
                ps.setInt(index++, idArticulo);
            }
            
            if (idUbicacion != null) {
                ps.setInt(index++, idUbicacion);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSetJoin(rs));
                }
            }
        }
        return lista;
    }

    // -------------------------------------------------------------------
    // ⭐ Actualizar Movimiento (Para modificar) ⭐
    // -------------------------------------------------------------------
    public boolean actualizarMovimiento(Movimiento m) throws SQLException {
        if (m == null) throw new SQLException("Movimiento nulo");

        // ⭐ CAMBIO: Se usan costo_divisa y costo_bs en el UPDATE
        String sql = """
             UPDATE movimiento SET
                 id_articulo = ?,
                 cantidad = ?,
                 id_ubicacion_destino = ?,
                 entregado = ?,
                 fecha_vencimiento = ?,
                 costo_divisa = ?,
                 costo_bs = ?,
                 donado = ?
             WHERE id_movimiento = ?
             """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, m.getIdArticulo());
            ps.setInt(2, m.getCantidad());
            
            if (m.getIdUbicacionDestino() == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, m.getIdUbicacionDestino());

            ps.setString(4, m.getEntregado());

            if (m.getFechaVencimiento() != null) ps.setTimestamp(5, m.getFechaVencimiento());
            else ps.setNull(5, Types.TIMESTAMP);

            // ⭐ Lógica de Costos para Actualización (Solo para ENTRADAS) ⭐
            int indexCosto = 6;
            if ("ENTRADA".equalsIgnoreCase(m.getTipo()) && m.getCosto() != null) {
                // costo_divisa
                ps.setDouble(indexCosto++, m.getCosto());
                // costo_bs
                double costoBs = m.getCostoBs(); // Prioriza el costoBolivar seteado
                if (costoBs >= 0) {
                    ps.setDouble(indexCosto++, costoBs);
                } else {
                    ps.setNull(indexCosto++, Types.DOUBLE);
                }
            } else {
                // Para SALIDA y TRASLADO, los costos son NULL
                ps.setNull(indexCosto++, Types.DOUBLE); // costo_divisa
                ps.setNull(indexCosto++, Types.DOUBLE); // costo_bs
            }
            
            ps.setBoolean(indexCosto++, m.isDonado());
            
            ps.setLong(indexCosto, m.getIdMovimiento()); 

            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------------------
    // --- Consultas por tipo de movimiento (Listado general) ---
    // -------------------------------------------------------------------
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
                 m.entregado, 
                 -- ⭐ CAMBIO: Se usa costo_divisa y se renombra a 'costo' para compatibilidad con mapearResultSet
                 m.costo_divisa AS costo, m.costo_bs, 
                 m.fecha_vencimiento, m.donado, m.motivo,
                 m.id_articulo, m.id_ubicacion_origen, m.id_ubicacion_destino,
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
                    lista.add(mapearResultSetJoin(rs));
                }
            }
        }
        return lista;
    }

    public List<Movimiento> listarTodos() throws SQLException {
        List<Movimiento> lista = new ArrayList<>();
        String sql = """
             SELECT 
                 m.id_movimiento, m.tipo, m.cantidad, m.fecha,
                 m.entregado, 
                 -- ⭐ CAMBIO: Se usa costo_divisa y se renombra a 'costo' para compatibilidad con mapearResultSet
                 m.costo_divisa AS costo, m.costo_bs, 
                 m.fecha_vencimiento, m.donado, m.motivo,
                 m.id_articulo, m.id_ubicacion_origen, m.id_ubicacion_destino,
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
                lista.add(mapearResultSetJoin(rs));
            }
        }
        return lista;
    }
    
    // -------------------------------------------------------------------
    // --- HELPERS para Mapeo ---
    // -------------------------------------------------------------------
    
    private Movimiento mapearResultSet(ResultSet rs) throws SQLException {
        Movimiento mov = new Movimiento();
        
        mov.setIdMovimiento(rs.getInt("id_movimiento")); 
        
        mov.setTipo(rs.getString("tipo"));
        mov.setCantidad(rs.getInt("cantidad"));
        mov.setIdArticulo(rs.getInt("id_articulo"));
        
        // Mapeo de IDs de ubicación
        int idOrigen = rs.getInt("id_ubicacion_origen");
        if (!rs.wasNull()) mov.setIdUbicacionOrigen(idOrigen);
        
        int idDestino = rs.getInt("id_ubicacion_destino");
        if (!rs.wasNull()) mov.setIdUbicacionDestino(idDestino);
        
        // Mapeo de otros campos
        mov.setEntregado(rs.getString("entregado"));
        mov.setMotivo(rs.getString("motivo"));
        
        // ⭐ MODIFICACIÓN: Se lee el costo en divisa (costo)
        try {
             // Intenta leer el alias 'costo' (usado en los JOIN SELECTS)
             mov.setCosto(rs.getDouble("costo"));
             if (rs.wasNull()) mov.setCosto(null);
        } catch (SQLException e) {
             // Si falla, lee la columna directa 'costo_divisa' (usado en obtenerPorId)
             mov.setCosto(rs.getDouble("costo_divisa"));
             if (rs.wasNull()) mov.setCosto(null);
        }
        
        // ⭐ AÑADIDO: Mapeo del costo en Bolívares (costo_bs)
        mov.setCostoBolivar(rs.getDouble("costo_bs"));
        if (rs.wasNull()) mov.setCostoBolivar(null);
        
        mov.setDonado(rs.getBoolean("donado"));
        
        mov.setFechaVencimiento(rs.getTimestamp("fecha_vencimiento"));
        mov.setFechaHora(rs.getTimestamp("fecha"));
        
        return mov;
    }
    
    private Movimiento mapearResultSetJoin(ResultSet rs) throws SQLException {
        Movimiento mov = mapearResultSet(rs);
        mov.setNombreArticulo(rs.getString("articulo"));
        mov.setNombreUbicacionOrigen(rs.getString("origen"));
        mov.setNombreUbicacionDestino(rs.getString("destino"));
        return mov;
    }
}