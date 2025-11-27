package Modelo;

import java.sql.Timestamp;
import util.GestorBcv;


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
    private Double costo; 
    private boolean donado;
    
    
    private Double costoBolivar; 
    
    
    private Double precioVenta; 

    

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

    
    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }

    public boolean isDonado() { return donado; }
    public void setDonado(boolean donado) { this.donado = donado; }
    
    
    public Double getCostoBolivar() { return costoBolivar; }
    public void setCostoBolivar(Double costoBolivar) { this.costoBolivar = costoBolivar; }
    
    
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }


    
    public double getPrecioVentaBs() {
        
        if (this.precioVenta == null || this.precioVenta <= 0) {
            return -1.0;
        }
        
        double tasa = GestorBcv.getInstance().getTasaActual();
        if (tasa <= 0) {
            return -1.0;
        }
        return this.precioVenta * tasa;
    }
    
   
    public double getCostoBs() {
        
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