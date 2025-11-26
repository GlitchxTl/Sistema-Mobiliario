package Modelo;

import java.sql.Timestamp;
import util.GestorBcv;

/**
 * Representa un movimiento de inventario (entrada, salida o traslado).
 * Compatible con la estructura actual de la tabla 'movimiento'.
 */
public class Movimiento {

    private int idMovimiento;
    private int idArticulo;
    private String nombreArticulo;
    private String tipo; // ENTRADA, SALIDA, TRASLADO
    private int cantidad;
    private Timestamp fechaHora;
    private int idUsuario;
    private Integer idUbicacionOrigen;
    private Integer idUbicacionDestino;
    private String nombreUbicacionOrigen;
    private String nombreUbicacionDestino;
    private String motivo;
    private String descripcion;
    private String entregado;
    private Timestamp fechaVencimiento;
    private Double costo; // Costo en divisa ($), campo original
    private boolean donado;
    
    // ⭐ CAMPO AÑADIDO: Para almacenar el costo en Bolívares (bs) al registrar la entrada.
    private Double costoBolivar; 
    
    // ⭐ CAMPO AÑADIDO: Se incluye para que los métodos getPrecioVentaBs() solicitados compilen.
    // En un modelo de dominio estricto, este campo podría pertenecer a la clase Articulo.
    private Double precioVenta; 

    // --- Getters y Setters ---

    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }

    public int getIdArticulo() { return idArticulo; }
    public void setIdArticulo(int idArticulo) { this.idArticulo = idArticulo; }

    public String getNombreArticulo() { return nombreArticulo; }
    public void setNombreArticulo(String nombreArticulo) { this.nombreArticulo = nombreArticulo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public Timestamp getFechaHora() { return fechaHora; }
    public void setFechaHora(Timestamp fechaHora) { this.fechaHora = fechaHora; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdUbicacionOrigen() { return idUbicacionOrigen; }
    public void setIdUbicacionOrigen(Integer idUbicacionOrigen) { this.idUbicacionOrigen = idUbicacionOrigen; }

    public Integer getIdUbicacionDestino() { return idUbicacionDestino; }
    public void setIdUbicacionDestino(Integer idUbicacionDestino) { this.idUbicacionDestino = idUbicacionDestino; }

    public String getNombreUbicacionOrigen() { return nombreUbicacionOrigen; }
    public void setNombreUbicacionOrigen(String nombreUbicacionOrigen) { this.nombreUbicacionOrigen = nombreUbicacionOrigen; }

    public String getNombreUbicacionDestino() { return nombreUbicacionDestino; }
    public void setNombreUbicacionDestino(String nombreUbicacionDestino) { this.nombreUbicacionDestino = nombreUbicacionDestino; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEntregado() { return entregado; }
    public void setEntregado(String entregado) { this.entregado = entregado; }

    public Timestamp getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Timestamp fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    // Costo original (Divisa)
    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }

    public boolean isDonado() { return donado; }
    public void setDonado(boolean donado) { this.donado = donado; }
    
    // ⭐ GETTER Y SETTER PARA EL NUEVO CAMPO costoBolivar ⭐
    public Double getCostoBolivar() { return costoBolivar; }
    public void setCostoBolivar(Double costoBolivar) { this.costoBolivar = costoBolivar; }
    
    // GETTER Y SETTER PARA EL CAMPO precioVenta
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }


    // --- MÉTODOS DE CÁLCULO DE BOLÍVARES (Bs) ---

    /**
     * Calcula el precio de venta en Bolívares (Bs) usando la tasa de cambio actual.
     * @return El precio de venta en Bolívares, o -1.0 si la tasa no es válida o el precio no está definido.
     */
    public double getPrecioVentaBs() {
        // Se asume que this.precioVenta está en Divisa (USD, EUR, etc.)
        if (this.precioVenta == null || this.precioVenta <= 0) {
            return -1.0;
        }
        
        double tasa = GestorBcv.getInstance().getTasaActual();
        if (tasa <= 0) {
            return -1.0;
        }
        return this.precioVenta * tasa;
    }
    
    /**
     * Calcula el costo del movimiento en Bolívares (Bs) usando la tasa de cambio actual.
     * Si el campo costoBolivar está guardado, lo devuelve directamente (para usar el costo histórico).
     * Si no está guardado, lo calcula usando la tasa actual y el costo en divisa.
     * @return El costo en Bolívares, o -1.0 si la tasa no es válida o el costo no está definido.
     */
    public double getCostoBs() {
        // ⭐ PRIORIZA el costoBolivar guardado (si existe) para el registro histórico
        if (this.costoBolivar != null && this.costoBolivar > 0) {
            return this.costoBolivar;
        }
        
        // Si no hay costoBolivar guardado, calcula el costo con la tasa actual (comportamiento de fallback)
        if (this.costo == null || this.costo <= 0) {
             return -1.0;
        }
        
        double tasa = GestorBcv.getInstance().getTasaActual();
        if (tasa <= 0) {
            return -1.0;
        }
        return this.costo * tasa;
    }
    
    
    // --- MÉTODOS AUXILIARES ---

    @Override
    public String toString() {
        return "Movimiento{" +
                "id=" + idMovimiento +
                ", articulo='" + nombreArticulo + '\'' +
                ", tipo='" + tipo + '\'' +
                ", cantidad=" + cantidad +
                ", origen=" + nombreUbicacionOrigen +
                ", destino=" + nombreUbicacionDestino +
                ", fecha=" + fechaHora +
                '}';
    }
    
    public static String capitalizar (String texto) {
        if (texto == null || texto.isBlank()) return texto;
        texto = texto.trim();
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }
}