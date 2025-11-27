package Controlador;

import Modelo.Articulo;
import Modelo.Movimiento;
import Modelo.MovimientoDAO;
import Modelo.Ubicacion;
import Modelo.UbicacionDAO;
import Modelo.InventarioDAO; 
import Modelo.CapacidadInsuficienteException;
import java.sql.SQLException;
import java.util.List;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;
import java.sql.Connection; 
import util.ConexionBD; 

public class MovimientoControlador {

    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final ArticuloControlador articuloControl = new ArticuloControlador();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO(); 
    private final InventarioDAO inventarioDAO = new InventarioDAO(); 

    
    public boolean registrarEntrada(int idArticulo, int cantidad, int idUbicDestino,
                                         String entregado, boolean donado,
                                         Double costoDivisa, Double costoBolivar, 
                                         Timestamp fechaVencimiento)
                                         throws Exception { // Se cambia a 'throws Exception' para ser consistente
        
        Connection conn = null;

        try {
           
            Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
            if (art == null) throw new SQLException("Artículo no encontrado con ID: " + idArticulo);

            Ubicacion ubicDestino = ubicacionDAO.obtenerPorId(idUbicDestino);
            if (ubicDestino == null) throw new SQLException("Ubicación de destino no encontrada con ID: " + idUbicDestino);

            double cantidadDoble = (double) cantidad; 
            double capacidadRestante = ubicDestino.getCapacidadRestante();
            double espacioRequerido = art.getEspacioUnitario() * cantidadDoble;

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

            
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            
            Movimiento mov = new Movimiento();
            mov.setIdArticulo(idArticulo);
            mov.setTipo("ENTRADA");
            mov.setCantidad(cantidad); 
            mov.setIdUbicacionDestino(idUbicDestino);
            mov.setEntregado(entregado);
            mov.setDonado(donado);

            if (!donado) {
                mov.setCosto(costoDivisa);
                mov.setCostoBolivar(costoBolivar); 
            } else {
                mov.setCosto(null);
                mov.setCostoBolivar(null);
            }
            
            if (fechaVencimiento != null) mov.setFechaVencimiento(fechaVencimiento);

            
            boolean movimientoRegistrado = movimientoDAO.insertarMovimiento(conn, mov);
            if (!movimientoRegistrado) {
                 throw new SQLException("Error al registrar el movimiento de entrada.");
            }
            
            
            boolean aumentoExitoso = inventarioDAO.aumentarStock(conn, idArticulo, idUbicDestino, cantidadDoble);
            
            if (!aumentoExitoso) {
                 throw new SQLException("Error Crítico: Movimiento registrado, pero FALLÓ la actualización del stock.");
            }

            
            conn.commit();
            return true;
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Entrada revertida debido a: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            
            throw e; 
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    
    public boolean registrarSalida(int idArticulo, int cantidad, int idUbicOrigen,
                                         String motivo) throws Exception { 
        
        // Usamos una conexión local para manejar la transacción (commit/rollback)
        Connection conn = null;

        try {
            
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false); // Deshabilita el auto-commit

            // Convertimos la cantidad a double para validación contra stock DECIMAL
            double cantidadDoble = (double) cantidad;

            
            Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
            if (art == null) throw new SQLException("Artículo no encontrado");
            
            
            Double stockActual = inventarioDAO.getStockPorUbicacion(idArticulo, idUbicOrigen);

            if (stockActual < cantidadDoble) {
                
                throw new Exception(
                    String.format("Stock insuficiente. Cantidad disponible en ubicación: %.3f. Cantidad solicitada: %d.", 
                    stockActual, cantidad)
                );
            }
            
            
            Movimiento mov = new Movimiento();
            mov.setIdArticulo(idArticulo);
            mov.setTipo("SALIDA");
            mov.setCantidad(cantidad); 
            mov.setIdUbicacionOrigen(idUbicOrigen);
            mov.setMotivo(motivo);

            
            boolean movimientoRegistrado = movimientoDAO.insertarMovimiento(conn, mov);
            
            if (!movimientoRegistrado) {
                throw new Exception("Error al registrar el movimiento.");
            }
            
            
            boolean descuentoExitoso = inventarioDAO.descontarStock(conn, idArticulo, idUbicOrigen, cantidadDoble);
            
            if (!descuentoExitoso) {
                
                throw new Exception("Error Crítico: El movimiento de Salida se registró, pero FALLÓ la actualización del inventario. La operación será deshecha.");
            }
            
            
            conn.commit();
            return true;
            
        } catch (Exception e) {
            
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace(); 
                }
            }
           
            throw e; 
            
        } finally {
            
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaurar estado
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace(); // Log del error al cerrar
                }
            }
        }
    }

    
    public boolean registrarTraslado(int idArticulo, int cantidad,
                                         int idUbicOrigen, int idUbicDestino,
                                         String entregado)
                                         throws Exception { // Se cambia a 'throws Exception' para ser consistente
        
        Connection conn = null;

        try {
            
            Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
            if (art == null) throw new SQLException("Artículo no encontrado");

            
            double cantidadDoble = (double) cantidad; 
            
            
            Ubicacion ubicDestino = ubicacionDAO.obtenerPorId(idUbicDestino);
            if (ubicDestino == null) throw new SQLException("Ubicación de destino no encontrada con ID: " + idUbicDestino);

            double capacidadRestante = ubicDestino.getCapacidadRestante();
            double espacioRequerido = art.getEspacioUnitario() * cantidadDoble;

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
            
            
            Double stockActual = inventarioDAO.getStockPorUbicacion(idArticulo, idUbicOrigen);
            if (stockActual < cantidadDoble) {
                 throw new SQLException(
                    String.format("Stock insuficiente en Origen. Disponible: %.3f. Solicitado: %d.", 
                    stockActual, cantidad)
                );
            }

            
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false); 
            
            
            boolean descuentoExitoso = inventarioDAO.descontarStock(conn, idArticulo, idUbicOrigen, cantidadDoble);
            
            if (!descuentoExitoso) {
                 throw new SQLException("Error al descontar stock de la ubicación de origen.");
            }

            
            boolean aumentoExitoso = inventarioDAO.aumentarStock(conn, idArticulo, idUbicDestino, cantidadDoble);
            
            if (!aumentoExitoso) {
                 throw new SQLException("Error al aumentar stock en la ubicación de destino.");
            }

            
            Movimiento mov = new Movimiento();
            mov.setIdArticulo(idArticulo);
            mov.setTipo("TRASLADO");
            mov.setCantidad(cantidad); // Usamos int
            mov.setIdUbicacionOrigen(idUbicOrigen);
            mov.setIdUbicacionDestino(idUbicDestino);
            mov.setEntregado(entregado);

            
            boolean movimientoRegistrado = movimientoDAO.insertarMovimiento(conn, mov);
            
            if (!movimientoRegistrado) {
                 throw new SQLException("Error al registrar el movimiento de traslado.");
            }
            
            
            conn.commit();
            return true;
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Traslado revertido debido a: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            
            throw e; 
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
    
    
    public boolean actualizarEntrada(long idMovimiento, int idArticulo, int nuevaCantidad, int idUbicDestino,
                                         String entregado, boolean donado,
                                         Double costoDivisa, Double costoBolivar, 
                                         Timestamp fechaVencimiento) 
                                         throws SQLException, CapacidadInsuficienteException {

        

        Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
        if (art == null) throw new SQLException("Artículo no encontrado con ID: " + idArticulo);

        
        Movimiento movOriginal = movimientoDAO.obtenerPorId((int)idMovimiento);
        if (movOriginal == null) throw new SQLException("Movimiento original no encontrado.");

        double cantidadAnterior = movOriginal.getCantidad(); // Asumiendo que getCantidad() retorna double o se adapta. Si retorna int, forzar a double.
        double espacioAnterior = art.getEspacioUnitario() * cantidadAnterior;
        
        
        double nuevaCantidadDoble = (double) nuevaCantidad;
        double espacioRequeridoNuevo = art.getEspacioUnitario() * nuevaCantidadDoble;
        
        
        if (espacioRequeridoNuevo > espacioAnterior || !Objects.equals(movOriginal.getIdUbicacionDestino(), idUbicDestino)) {
            
            Ubicacion ubicDestino = ubicacionDAO.obtenerPorId(idUbicDestino);
            if (ubicDestino == null) throw new SQLException("Ubicación de destino no encontrada con ID: " + idUbicDestino);

            double capacidadRestanteAjustada = ubicDestino.getCapacidadRestante();
            if (Objects.equals(movOriginal.getIdUbicacionDestino(), idUbicDestino)) {
                capacidadRestanteAjustada += espacioAnterior;
            }
            
            if (espacioRequeridoNuevo > capacidadRestanteAjustada) {
                 List<Ubicacion> sugerencias = ubicacionDAO.listarConEspacioSuficiente(
                     espacioRequeridoNuevo, idUbicDestino
                 );
                 throw new CapacidadInsuficienteException(
                     "La actualización excede la capacidad disponible ajustada.",
                     ubicDestino.getNombre(),
                     ubicDestino.getCapacidadRestante(),
                     espacioRequeridoNuevo,
                     sugerencias
                 );
            }
        }

        
        Movimiento mov = new Movimiento();
        mov.setIdMovimiento((int) idMovimiento);
        mov.setIdArticulo(idArticulo);
        mov.setTipo("ENTRADA");
        mov.setCantidad(nuevaCantidad); 
        mov.setIdUbicacionDestino(idUbicDestino);
        mov.setEntregado(entregado);
        mov.setDonado(donado);
        
        mov.setCosto(donado ? null : costoDivisa); 
        mov.setCostoBolivar(donado ? null : costoBolivar);
        
        mov.setFechaVencimiento(fechaVencimiento);
        
        
        return movimientoDAO.actualizarMovimiento(mov);
    }
    
    
    public boolean actualizarTraslado(long idMovimiento, int idArticulo, int nuevaCantidad, 
                                         int idUbicOrigen, int idUbicDestino, String entregado) 
                                         throws SQLException, CapacidadInsuficienteException {

        

        Articulo art = articuloControl.obtenerArticuloPorId(idArticulo); 
        if (art == null) throw new SQLException("Artículo no encontrado con ID: " + idArticulo);

        
        Movimiento movOriginal = movimientoDAO.obtenerPorId((int)idMovimiento);
        if (movOriginal == null || !Objects.equals("TRASLADO", movOriginal.getTipo())) {
            throw new SQLException("Movimiento original no encontrado o no es un TRASLADO.");
        }
        
        
        double nuevaCantidadDoble = (double) nuevaCantidad;
        
        
        double cantidadAnterior = movOriginal.getCantidad(); // Asumiendo que getCantidad() retorna double o se adapta
        double espacioAnteriorEnDestino = 0; 
        if (movOriginal.getIdUbicacionDestino() != null) {
            espacioAnteriorEnDestino = art.getEspacioUnitario() * cantidadAnterior;
        }
        
        double espacioRequeridoNuevo = art.getEspacioUnitario() * nuevaCantidadDoble;
        
        
        boolean necesitaValidarCapacidad = (nuevaCantidadDoble > cantidadAnterior || !Objects.equals(movOriginal.getIdUbicacionDestino(), idUbicDestino));

        if (necesitaValidarCapacidad) {
            
            Ubicacion ubicDestino = ubicacionDAO.obtenerPorId(idUbicDestino);
            if (ubicDestino == null) throw new SQLException("Ubicación de destino no encontrada con ID: " + idUbicDestino);

            double capacidadRestanteAjustada = ubicDestino.getCapacidadRestante();
            
            if (Objects.equals(movOriginal.getIdUbicacionDestino(), idUbicDestino)) {
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
        
        
        Movimiento mov = new Movimiento();
        mov.setIdMovimiento((int) idMovimiento);
        mov.setIdArticulo(idArticulo);
        mov.setTipo("TRASLADO");
        mov.setCantidad(nuevaCantidad); // MANTENIDO COMO INT
        mov.setIdUbicacionOrigen(idUbicOrigen);
        mov.setIdUbicacionDestino(idUbicDestino);
        mov.setEntregado(entregado);
        
        return movimientoDAO.actualizarMovimiento(mov);
    }

    

    public Movimiento obtenerMovimientoPorId(long id) throws SQLException {
        return movimientoDAO.obtenerPorId((int)id); 
    }


    public List<Movimiento> buscarEntradas(Integer idArticulo, Integer idUbicacion) {
        try {
            return movimientoDAO.buscarEntradas(idArticulo, idUbicacion);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); 
        }
    }
    

    public List<Movimiento> buscarSalidas(Integer idArticulo, Integer idUbicacion) {
        try {
            return movimientoDAO.buscarSalidas(idArticulo, idUbicacion);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    

    public List<Movimiento> buscarTraslados(Integer idArticulo, Integer idUbicacion) {
        try {
            return movimientoDAO.buscarTraslados(idArticulo, idUbicacion);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
   
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

    public boolean registrarMovimiento(Connection conn, Movimiento movimiento) throws SQLException {
        
        return movimientoDAO.insertarMovimiento(conn, movimiento); 
    }
}