package Controlador;

import Modelo.Articulo;
import Modelo.Movimiento;
import Modelo.MovimientoDAO;
import java.sql.SQLException;
import java.util.List;
import java.sql.Timestamp;

public class MovimientoControlador {

    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final ArticuloControlador articuloControl = new ArticuloControlador();

    // --- Registrar ENTRADA ---
    public boolean registrarEntrada(int idArticulo, int cantidad, int idUbicDestino,
                                    String entregado, boolean donado,
                                    Double costo, Timestamp fechaVencimiento) throws SQLException {
        Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
        if (art == null) throw new SQLException("Artículo no encontrado");

        Movimiento mov = new Movimiento();
        mov.setIdArticulo(idArticulo);
        mov.setTipo("ENTRADA");
        mov.setCantidad(cantidad);
        mov.setIdUbicacionDestino(idUbicDestino);
        mov.setEntregado(entregado);
        mov.setDonado(donado);

        if (!donado && costo != null) mov.setCosto(costo);
        if (fechaVencimiento != null) mov.setFechaVencimiento(fechaVencimiento);

        return movimientoDAO.insertarMovimiento(mov);
    }

    // --- Registrar SALIDA (CORREGIDO: 4 argumentos) ---
    public boolean registrarSalida(int idArticulo, int cantidad, int idUbicOrigen,
                                   String motivo) throws SQLException {
        Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
        if (art == null) throw new SQLException("Artículo no encontrado");

        Movimiento mov = new Movimiento();
        mov.setIdArticulo(idArticulo);
        mov.setTipo("SALIDA");
        mov.setCantidad(cantidad);
        mov.setIdUbicacionOrigen(idUbicOrigen);
        mov.setMotivo(motivo);
        // mov.setDescripcion(descripcion); <--- ELIMINADO

        return movimientoDAO.insertarMovimiento(mov);
    }

    // --- Registrar TRASLADO ---
    public boolean registrarTraslado(int idArticulo, int cantidad,
                                     int idUbicOrigen, int idUbicDestino,
                                     String entregado) throws SQLException {
        Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
        if (art == null) throw new SQLException("Artículo no encontrado");

        Movimiento mov = new Movimiento();
        mov.setIdArticulo(idArticulo);
        mov.setTipo("TRASLADO");
        mov.setCantidad(cantidad);
        mov.setIdUbicacionOrigen(idUbicOrigen);
        mov.setIdUbicacionDestino(idUbicDestino);
        mov.setEntregado(entregado);

        return movimientoDAO.insertarMovimiento(mov);
    }

    // --- Obtener listas de movimientos ---
    public List<Movimiento> obtenerEntradas() throws SQLException {
        return movimientoDAO.listarEntradas();
    }

    public List<Movimiento> obtenerSalidas() throws SQLException {
        return movimientoDAO.listarSalidas();
    }

    public List<Movimiento> obtenerTraslados() throws SQLException {
        return movimientoDAO.listarTraslados();
    }

    public List<Movimiento> obtenerTodos() throws SQLException {
        return movimientoDAO.listarTodos();
    }

    public boolean registrarMovimiento(Movimiento movimiento) throws SQLException {
        return movimientoDAO.insertarMovimiento(movimiento);
    }
}








