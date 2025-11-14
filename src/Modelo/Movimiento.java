package Modelo;

import java.sql.Timestamp;

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
    private Double costo;
    private boolean donado;

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

    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }

    public boolean isDonado() { return donado; }
    public void setDonado(boolean donado) { this.donado = donado; }

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
}



