package Controlador;

import Modelo.Articulo;
import Modelo.Movimiento;
import Modelo.MovimientoDAO;
import Modelo.Ubicacion;
import Modelo.UbicacionDAO;
import Modelo.CapacidadInsuficienteException;
import java.sql.SQLException;
import java.util.List;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;

public class MovimientoControlador {

    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final ArticuloControlador articuloControl = new ArticuloControlador();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO(); 

    // -------------------------------------------------------------------
    // --- Registrar ENTRADA ---
    // -------------------------------------------------------------------
    public boolean registrarEntrada(int idArticulo, int cantidad, int idUbicDestino,
                                    String entregado, boolean donado,
                                    Double costo, Timestamp fechaVencimiento)
                                    throws SQLException, CapacidadInsuficienteException {
        
        Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
        if (art == null) throw new SQLException("Artículo no encontrado con ID: " + idArticulo);

        // --- VALIDACIÓN DE CAPACIDAD ---
        Ubicacion ubicDestino = ubicacionDAO.obtenerPorId(idUbicDestino);
        if (ubicDestino == null) throw new SQLException("Ubicación de destino no encontrada con ID: " + idUbicDestino);

        double capacidadRestante = ubicDestino.getCapacidadRestante();
        double espacioRequerido = art.getEspacioUnitario() * cantidad;

        if (espacioRequerido > capacidadRestante) {
            List<Ubicacion> sugerencias = ubicacionDAO.listarConEspacioSuficiente(
                espacioRequerido,
                idUbicDestino 
            );
            throw new CapacidadInsuficienteException(
                "Capacidad insuficiente en la ubicación de destino.",
                ubicDestino.getNombre(),
                capacidadRestante,
                espacioRequerido,
                sugerencias
            );
        }

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

    // -------------------------------------------------------------------
    // --- Registrar SALIDA ---
    // -------------------------------------------------------------------
    public boolean registrarSalida(int idArticulo, int cantidad, int idUbicOrigen,
                                   String motivo) throws SQLException {
        Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
        if (art == null) throw new SQLException("Artículo no encontrado");
        
        // NOTA: Aquí faltaría la validación de STOCK en Origen.

        Movimiento mov = new Movimiento();
        mov.setIdArticulo(idArticulo);
        mov.setTipo("SALIDA");
        mov.setCantidad(cantidad);
        mov.setIdUbicacionOrigen(idUbicOrigen);
        mov.setMotivo(motivo);

        return movimientoDAO.insertarMovimiento(mov);
    }

    // -------------------------------------------------------------------
    // --- Registrar TRASLADO ---
    // -------------------------------------------------------------------
    public boolean registrarTraslado(int idArticulo, int cantidad,
                                     int idUbicOrigen, int idUbicDestino,
                                     String entregado)
                                     throws SQLException, CapacidadInsuficienteException {
        
        Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
        if (art == null) throw new SQLException("Artículo no encontrado");

        // --- VALIDACIÓN DE CAPACIDAD (en DESTINO) ---
        Ubicacion ubicDestino = ubicacionDAO.obtenerPorId(idUbicDestino);
        if (ubicDestino == null) throw new SQLException("Ubicación de destino no encontrada con ID: " + idUbicDestino);

        double capacidadRestante = ubicDestino.getCapacidadRestante();
        double espacioRequerido = art.getEspacioUnitario() * cantidad;

        if (espacioRequerido > capacidadRestante) {
            List<Ubicacion> sugerencias = ubicacionDAO.listarConEspacioSuficiente(
                espacioRequerido,
                idUbicDestino
            );
            
            throw new CapacidadInsuficienteException(
                "Capacidad insuficiente en la ubicación de destino.",
                ubicDestino.getNombre(),
                capacidadRestante,
                espacioRequerido,
                sugerencias
            );
        }
        // NOTA: Aquí faltaría una validación de STOCK en Origen.

        Movimiento mov = new Movimiento();
        mov.setIdArticulo(idArticulo);
        mov.setTipo("TRASLADO");
        mov.setCantidad(cantidad);
        mov.setIdUbicacionOrigen(idUbicOrigen);
        mov.setIdUbicacionDestino(idUbicDestino);
        mov.setEntregado(entregado);

        return movimientoDAO.insertarMovimiento(mov);
    }
    
    // -------------------------------------------------------------------
    // --- Actualizar ENTRADA ---
    // -------------------------------------------------------------------
    public boolean actualizarEntrada(long idMovimiento, int idArticulo, int nuevaCantidad, int idUbicDestino,
                                     String entregado, boolean donado,
                                     Double costo, Timestamp fechaVencimiento) 
                                     throws SQLException, CapacidadInsuficienteException {

        Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
        if (art == null) throw new SQLException("Artículo no encontrado con ID: " + idArticulo);

        // 1. Obtener la cantidad ANTERIOR para cálculo de cambio de espacio
        Movimiento movOriginal = movimientoDAO.obtenerPorId((int)idMovimiento);
        if (movOriginal == null) throw new SQLException("Movimiento original no encontrado.");

        int cantidadAnterior = movOriginal.getCantidad();
        double espacioAnterior = art.getEspacioUnitario() * cantidadAnterior;
        
        // 2. Calcular el NUEVO espacio requerido
        double espacioRequeridoNuevo = art.getEspacioUnitario() * nuevaCantidad;
        
        // 3. VALIDAR CAPACIDAD (Solo si aumenta la cantidad o cambia la ubicación)
        if (espacioRequeridoNuevo > espacioAnterior || !movOriginal.getIdUbicacionDestino().equals(idUbicDestino)) {
            
            Ubicacion ubicDestino = ubicacionDAO.obtenerPorId(idUbicDestino);
            if (ubicDestino == null) throw new SQLException("Ubicación de destino no encontrada con ID: " + idUbicDestino);

            // Capacidad restante AJUSTADA: La capacidad actual + el espacio que liberará el movimiento antiguo
            double capacidadRestanteAjustada = ubicDestino.getCapacidadRestante();
            if (movOriginal.getIdUbicacionDestino().equals(idUbicDestino)) {
                capacidadRestanteAjustada += espacioAnterior;
            }
            
            if (espacioRequeridoNuevo > capacidadRestanteAjustada) {
                 List<Ubicacion> sugerencias = ubicacionDAO.listarConEspacioSuficiente(
                      espacioRequeridoNuevo, idUbicDestino
                 );
                 throw new CapacidadInsuficienteException(
                      "La actualización excede la capacidad disponible ajustada.",
                      ubicDestino.getNombre(),
                      ubicDestino.getCapacidadRestante(), // Mostramos la real actual
                      espacioRequeridoNuevo,
                      sugerencias
                 );
            }
        }

        // 4. Crear objeto para actualización
        Movimiento mov = new Movimiento();
        mov.setIdMovimiento((int) idMovimiento);
        mov.setIdArticulo(idArticulo);
        mov.setTipo("ENTRADA");
        mov.setCantidad(nuevaCantidad);
        mov.setIdUbicacionDestino(idUbicDestino);
        mov.setEntregado(entregado);
        mov.setDonado(donado);
        mov.setCosto(donado ? null : costo);
        mov.setFechaVencimiento(fechaVencimiento);
        
        return movimientoDAO.actualizarMovimiento(mov);
    }
    
    // -------------------------------------------------------------------
    // ⭐ --- Actualizar TRASLADO (NUEVO) --- ⭐
    // -------------------------------------------------------------------
    public boolean actualizarTraslado(long idMovimiento, int idArticulo, int nuevaCantidad, 
                                      int idUbicOrigen, int idUbicDestino, String entregado) 
                                      throws SQLException, CapacidadInsuficienteException {

        Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
        if (art == null) throw new SQLException("Artículo no encontrado con ID: " + idArticulo);

        // 1. Obtener el movimiento ORIGINAL
        Movimiento movOriginal = movimientoDAO.obtenerPorId((int)idMovimiento);
        if (movOriginal == null || !Objects.equals("TRASLADO", movOriginal.getTipo())) {
            throw new SQLException("Movimiento original no encontrado o no es un TRASLADO.");
        }
        
        // NOTA: La validación de STOCK en Origen debe ser manejada aquí o en el DAO.
        // Asumiendo que el DAO lo gestiona al revertir el movimiento original.

        // 2. Cálculo del espacio
        int cantidadAnterior = movOriginal.getCantidad();
        double espacioAnteriorEnDestino = 0; 
        if (movOriginal.getIdUbicacionDestino() != null) {
            espacioAnteriorEnDestino = art.getEspacioUnitario() * cantidadAnterior;
        }
        
        double espacioRequeridoNuevo = art.getEspacioUnitario() * nuevaCantidad;
        
        // 3. VALIDACIÓN DE CAPACIDAD (en Destino)
        boolean necesitaValidarCapacidad = (nuevaCantidad > cantidadAnterior || !movOriginal.getIdUbicacionDestino().equals(idUbicDestino));

        if (necesitaValidarCapacidad) {
            
            Ubicacion ubicDestino = ubicacionDAO.obtenerPorId(idUbicDestino);
            if (ubicDestino == null) throw new SQLException("Ubicación de destino no encontrada con ID: " + idUbicDestino);

            double capacidadRestanteAjustada = ubicDestino.getCapacidadRestante();
            
            // Si la UBICACIÓN DESTINO NO CAMBIA: Se suma el espacio que se "libera" en la misma ubicación.
            if (movOriginal.getIdUbicacionDestino().equals(idUbicDestino)) {
                capacidadRestanteAjustada += espacioAnteriorEnDestino;
            } 
            
            if (espacioRequeridoNuevo > capacidadRestanteAjustada) {
                List<Ubicacion> sugerencias = ubicacionDAO.listarConEspacioSuficiente(
                    espacioRequeridoNuevo, idUbicDestino
                );
                
                throw new CapacidadInsuficienteException(
                    "La actualización excede la capacidad disponible ajustada en el destino.",
                    ubicDestino.getNombre(),
                    ubicDestino.getCapacidadRestante(),
                    espacioRequeridoNuevo,
                    sugerencias
                );
            }
        }
        
        // 4. Crear objeto para actualización
        Movimiento mov = new Movimiento();
        mov.setIdMovimiento((int) idMovimiento);
        mov.setIdArticulo(idArticulo);
        mov.setTipo("TRASLADO");
        mov.setCantidad(nuevaCantidad);
        mov.setIdUbicacionOrigen(idUbicOrigen);
        mov.setIdUbicacionDestino(idUbicDestino);
        mov.setEntregado(entregado);
        
        return movimientoDAO.actualizarMovimiento(mov);
    }

    // -------------------------------------------------------------------
    // --- Métodos de Consulta ---
    // -------------------------------------------------------------------
    
    /**
     * Obtiene un movimiento por su ID, requerido para cargar el formulario.
     */
    public Movimiento obtenerMovimientoPorId(long id) throws SQLException {
        return movimientoDAO.obtenerPorId((int)id); 
    }

    /**
     * Busca entradas filtrando opcionalmente por Artículo y/o Ubicación.
     */
    public List<Movimiento> buscarEntradas(Integer idArticulo, Integer idUbicacion) {
        try {
            return movimientoDAO.buscarEntradas(idArticulo, idUbicacion);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); 
        }
    }
    
    /**
     * Busca salidas filtrando opcionalmente por Artículo y/o Ubicación de Origen.
     */
    public List<Movimiento> buscarSalidas(Integer idArticulo, Integer idUbicacion) {
        try {
            return movimientoDAO.buscarSalidas(idArticulo, idUbicacion);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca traslados filtrando opcionalmente por Artículo y/o Ubicación de Destino.
     */
    public List<Movimiento> buscarTraslados(Integer idArticulo, Integer idUbicacion) {
        try {
            return movimientoDAO.buscarTraslados(idArticulo, idUbicacion);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // --- Métodos para obtener listas de movimientos (Existentes) ---
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








