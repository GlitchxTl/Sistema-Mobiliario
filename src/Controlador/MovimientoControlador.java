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
import java.sql.Connection; // Import necesario para transacciones
import util.ConexionBD; // Asumo que esta clase es usada para obtener la conexión

public class MovimientoControlador {

    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final ArticuloControlador articuloControl = new ArticuloControlador();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO(); 
    private final InventarioDAO inventarioDAO = new InventarioDAO(); 

    // -------------------------------------------------------------------
    // --- Registrar ENTRADA (CORREGIDO CON TRANSACCIÓN) ---
    // -------------------------------------------------------------------
    /**
     * Registra una nueva entrada de inventario de forma transaccional.
     * @param costoDivisa Costo en divisa (USD, EUR, etc.).
     * @param costoBolivar Costo histórico en Bolívares (Bs), al momento de la entrada.
     */
    public boolean registrarEntrada(int idArticulo, int cantidad, int idUbicDestino,
                                         String entregado, boolean donado,
                                         Double costoDivisa, Double costoBolivar, 
                                         Timestamp fechaVencimiento)
                                         throws Exception { // Se cambia a 'throws Exception' para ser consistente
        
        Connection conn = null;

        try {
            // 0. VALIDACIONES PREVIAS (No transaccionales)
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

            // 1. Inicializar la Transacción
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            // 2. Crear objeto Movimiento
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

            // 3. Insertar Movimiento (USANDO CONN)
            boolean movimientoRegistrado = movimientoDAO.insertarMovimiento(conn, mov);
            if (!movimientoRegistrado) {
                 throw new SQLException("Error al registrar el movimiento de entrada.");
            }
            
            // 4. Aumentar Stock en el inventario (USANDO CONN)
            boolean aumentoExitoso = inventarioDAO.aumentarStock(conn, idArticulo, idUbicDestino, cantidadDoble);
            
            if (!aumentoExitoso) {
                 throw new SQLException("Error Crítico: Movimiento registrado, pero FALLÓ la actualización del stock.");
            }

            // 5. Commit
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
            // Relanzamos la excepción
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

    // -------------------------------------------------------------------
    // --- Registrar SALIDA (SE MANTIENE SIN CAMBIOS) ---
    // -------------------------------------------------------------------
    /**
     * Registra una salida de inventario, asegurando la integridad transaccional
     * entre el registro del movimiento y el descuento del stock.
     */
    public boolean registrarSalida(int idArticulo, int cantidad, int idUbicOrigen,
                                         String motivo) throws Exception { 
        
        // Usamos una conexión local para manejar la transacción (commit/rollback)
        Connection conn = null;

        try {
            // 0. Inicializar la transacción
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false); // Deshabilita el auto-commit

            // Convertimos la cantidad a double para validación contra stock DECIMAL
            double cantidadDoble = (double) cantidad;

            // VALIDACIÓN DEL ARTÍCULO (Se mantiene como chequeo de seguridad)
            Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
            if (art == null) throw new SQLException("Artículo no encontrado");
            
            // 1. VALIDACIÓN DE STOCK SUFICIENTE
            Double stockActual = inventarioDAO.getStockPorUbicacion(idArticulo, idUbicOrigen);

            if (stockActual < cantidadDoble) {
                // Lanza una excepción de negocio que la Vista capturará
                throw new Exception(
                    String.format("Stock insuficiente. Cantidad disponible en ubicación: %.3f. Cantidad solicitada: %d.", 
                    stockActual, cantidad)
                );
            }
            
            // 2. REGISTRO DEL MOVIMIENTO
            Movimiento mov = new Movimiento();
            mov.setIdArticulo(idArticulo);
            mov.setTipo("SALIDA");
            mov.setCantidad(cantidad); 
            mov.setIdUbicacionOrigen(idUbicOrigen);
            mov.setMotivo(motivo);

            // ⭐ USO DE LA CONEXIÓN TRANSACCIONAL: CORREGIDO ANTERIORMENTE
            boolean movimientoRegistrado = movimientoDAO.insertarMovimiento(conn, mov);
            
            if (!movimientoRegistrado) {
                throw new Exception("Error al registrar el movimiento.");
            }
            
            // 3. DESCUENTO DEL INVENTARIO (Usando double)
            // ⭐ USO DE LA CONEXIÓN TRANSACCIONAL
            boolean descuentoExitoso = inventarioDAO.descontarStock(conn, idArticulo, idUbicOrigen, cantidadDoble);
            
            if (!descuentoExitoso) {
                // Esto podría ocurrir si el stock se actualizó a 0 justo antes, 
                // o si hay un error de concurrencia.
                throw new Exception("Error Crítico: El movimiento de Salida se registró, pero FALLÓ la actualización del inventario. La operación será deshecha.");
            }
            
            // 4. COMMIT: Si ambas operaciones fueron exitosas, se confirman los cambios
            conn.commit();
            return true;
            
        } catch (Exception e) {
            // ROLLBACK: Si cualquier paso falla, se deshacen todos los cambios
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace(); // Log del error de rollback
                }
            }
            // Relanzamos la excepción para que sea capturada en la Vista
            throw e; 
            
        } finally {
            // 5. CERRAR CONEXIÓN
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

    // -------------------------------------------------------------------
    // --- Registrar TRASLADO (CORREGIDO CON TRANSACCIÓN) ---
    // -------------------------------------------------------------------
    public boolean registrarTraslado(int idArticulo, int cantidad,
                                         int idUbicOrigen, int idUbicDestino,
                                         String entregado)
                                         throws Exception { // Se cambia a 'throws Exception' para ser consistente
        
        Connection conn = null;

        try {
            // 0. VALIDACIONES PREVIAS (No transaccionales)
            Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
            if (art == null) throw new SQLException("Artículo no encontrado");

            // Usamos double para la validación de capacidad/stock
            double cantidadDoble = (double) cantidad; 
            
            // VALIDACIÓN DE CAPACIDAD (en DESTINO)
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
            
            // VALIDACIÓN DE STOCK (en ORIGEN)
            Double stockActual = inventarioDAO.getStockPorUbicacion(idArticulo, idUbicOrigen);
            if (stockActual < cantidadDoble) {
                 throw new SQLException(
                    String.format("Stock insuficiente en Origen. Disponible: %.3f. Solicitado: %d.", 
                    stockActual, cantidad)
                );
            }

            // 1. Inicializar la Transacción
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false); 
            
            // 2. Descontar Stock del Origen (USANDO CONN)
            boolean descuentoExitoso = inventarioDAO.descontarStock(conn, idArticulo, idUbicOrigen, cantidadDoble);
            
            if (!descuentoExitoso) {
                 throw new SQLException("Error al descontar stock de la ubicación de origen.");
            }

            // 3. Aumentar Stock en el Destino (USANDO CONN)
            boolean aumentoExitoso = inventarioDAO.aumentarStock(conn, idArticulo, idUbicDestino, cantidadDoble);
            
            if (!aumentoExitoso) {
                 throw new SQLException("Error al aumentar stock en la ubicación de destino.");
            }

            // 4. Crear objeto movimiento
            Movimiento mov = new Movimiento();
            mov.setIdArticulo(idArticulo);
            mov.setTipo("TRASLADO");
            mov.setCantidad(cantidad); // Usamos int
            mov.setIdUbicacionOrigen(idUbicOrigen);
            mov.setIdUbicacionDestino(idUbicDestino);
            mov.setEntregado(entregado);

            // 5. Insertar Movimiento (USANDO CONN)
            boolean movimientoRegistrado = movimientoDAO.insertarMovimiento(conn, mov);
            
            if (!movimientoRegistrado) {
                 throw new SQLException("Error al registrar el movimiento de traslado.");
            }
            
            // 6. Commit
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
            // Relanzar excepción
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
    
    // -------------------------------------------------------------------
    // --- Actualizar ENTRADA (Ajustado para mantener int en setCantidad) ---
    // -------------------------------------------------------------------
    /**
     * Actualiza una entrada de inventario existente.
     */
    public boolean actualizarEntrada(long idMovimiento, int idArticulo, int nuevaCantidad, int idUbicDestino,
                                         String entregado, boolean donado,
                                         Double costoDivisa, Double costoBolivar, 
                                         Timestamp fechaVencimiento) 
                                         throws SQLException, CapacidadInsuficienteException {

        // ... Lógica de validación de capacidad (Se mantiene igual, no es transaccional) ...

        Articulo art = articuloControl.obtenerArticuloPorId(idArticulo);
        if (art == null) throw new SQLException("Artículo no encontrado con ID: " + idArticulo);

        // 1. Obtener la cantidad ANTERIOR (como double para cálculos de espacio)
        Movimiento movOriginal = movimientoDAO.obtenerPorId((int)idMovimiento);
        if (movOriginal == null) throw new SQLException("Movimiento original no encontrado.");

        double cantidadAnterior = movOriginal.getCantidad(); // Asumiendo que getCantidad() retorna double o se adapta. Si retorna int, forzar a double.
        double espacioAnterior = art.getEspacioUnitario() * cantidadAnterior;
        
        // 2. Calcular el NUEVO espacio requerido (usando double)
        double nuevaCantidadDoble = (double) nuevaCantidad;
        double espacioRequeridoNuevo = art.getEspacioUnitario() * nuevaCantidadDoble;
        
        // 3. VALIDAR CAPACIDAD 
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

        // 4. Crear objeto para actualización
        Movimiento mov = new Movimiento();
        mov.setIdMovimiento((int) idMovimiento);
        mov.setIdArticulo(idArticulo);
        mov.setTipo("ENTRADA");
        mov.setCantidad(nuevaCantidad); // MANTENIDO COMO INT
        mov.setIdUbicacionDestino(idUbicDestino);
        mov.setEntregado(entregado);
        mov.setDonado(donado);
        
        mov.setCosto(donado ? null : costoDivisa); 
        mov.setCostoBolivar(donado ? null : costoBolivar);
        
        mov.setFechaVencimiento(fechaVencimiento);
        
        // NOTA: Esta operación de actualización de movimiento requiere un manejo transaccional 
        // y de inventario más complejo (descontar anterior, aumentar nueva diferencia)
        // que no está implementado aquí, pero la llamada al DAO se mantiene sin conexión
        // ya que el método actualizarMovimiento no fue definido como transaccional en el DAO.
        return movimientoDAO.actualizarMovimiento(mov);
    }
    
    // -------------------------------------------------------------------
    // --- Actualizar TRASLADO (Ajustado para mantener int en setCantidad) --- 
    // -------------------------------------------------------------------
    public boolean actualizarTraslado(long idMovimiento, int idArticulo, int nuevaCantidad, 
                                         int idUbicOrigen, int idUbicDestino, String entregado) 
                                         throws SQLException, CapacidadInsuficienteException {

        // ... Lógica de validación de capacidad (Se mantiene igual, no es transaccional) ...

        Articulo art = articuloControl.obtenerArticuloPorId(idArticulo); 
        if (art == null) throw new SQLException("Artículo no encontrado con ID: " + idArticulo);

        // 1. Obtener el movimiento ORIGINAL
        Movimiento movOriginal = movimientoDAO.obtenerPorId((int)idMovimiento);
        if (movOriginal == null || !Objects.equals("TRASLADO", movOriginal.getTipo())) {
            throw new SQLException("Movimiento original no encontrado o no es un TRASLADO.");
        }
        
        // Usamos double para cálculos de stock/capacidad
        double nuevaCantidadDoble = (double) nuevaCantidad;
        
        // 2. Cálculo del espacio
        double cantidadAnterior = movOriginal.getCantidad(); // Asumiendo que getCantidad() retorna double o se adapta
        double espacioAnteriorEnDestino = 0; 
        if (movOriginal.getIdUbicacionDestino() != null) {
            espacioAnteriorEnDestino = art.getEspacioUnitario() * cantidadAnterior;
        }
        
        double espacioRequeridoNuevo = art.getEspacioUnitario() * nuevaCantidadDoble;
        
        // 3. VALIDACIÓN DE CAPACIDAD (en Destino)
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
        
        // 4. Crear objeto para actualización
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

    public boolean registrarMovimiento(Connection conn, Movimiento movimiento) throws SQLException {
        // Asumiendo que movimientoDAO.insertarMovimiento también fue modificado
        // para aceptar la conexión.
        return movimientoDAO.insertarMovimiento(conn, movimiento); 
    }
}