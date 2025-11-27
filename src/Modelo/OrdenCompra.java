package Modelo;

import java.time.LocalDate; 
import java.util.ArrayList;
import java.util.List;


public class OrdenCompra {

    private Long idOrdenCompra;
    private Long idProveedor; 
    private Long idUsuarioEmisor; 
    private LocalDate fechaEmision; 
    private String numeroReferencia; 
    private String estado; 
    private Double montoTotal; 
    
    
    private String nombreProveedor;
    
    
    private String nombreUsuarioEmisor; 
    
    
    private List<OrdenCompraDetalle> detalles; 

    // Constructor vacío (necesario para ORMs/DAOs)
    public OrdenCompra() {
        
        this.detalles = new ArrayList<>(); 
    }
    
    
    public OrdenCompra(String nombreProveedor, Long idUsuarioEmisor, String numeroReferencia) {
        this(); // Llama al constructor vacío para inicializar detalles
        this.nombreProveedor = nombreProveedor;
        this.idUsuarioEmisor = idUsuarioEmisor;
        this.numeroReferencia = numeroReferencia;
        this.fechaEmision = LocalDate.now(); // Usando LocalDate
        this.estado = "PENDIENTE";
        this.montoTotal = 0.0;
        this.idProveedor = null; 
    }


    public Long getIdOrdenCompra() { return idOrdenCompra; }
    public void setIdOrdenCompra(Long idOrdenCompra) { this.idOrdenCompra = idOrdenCompra; }

    public Long getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Long idProveedor) { this.idProveedor = idProveedor; }

    public Long getIdUsuarioEmisor() { return idUsuarioEmisor; }
    public void setIdUsuarioEmisor(Long idUsuarioEmisor) { this.idUsuarioEmisor = idUsuarioEmisor; }

    public void setIdUsuarioEmisor(Integer idUsuarioEmisor) {
        if (idUsuarioEmisor != null) {
            this.idUsuarioEmisor = idUsuarioEmisor.longValue();
        } else {
            this.idUsuarioEmisor = null;
        }
    }
    
    public String getNombreUsuarioEmisor() { return nombreUsuarioEmisor; }
    public void setNombreUsuarioEmisor(String nombreUsuarioEmisor) { this.nombreUsuarioEmisor = nombreUsuarioEmisor; }
    
    
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getNumeroReferencia() { return numeroReferencia; }
    public void setNumeroReferencia(String numeroReferencia) { this.numeroReferencia = numeroReferencia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public List<OrdenCompraDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<OrdenCompraDetalle> detalles) { this.detalles = detalles; }
}