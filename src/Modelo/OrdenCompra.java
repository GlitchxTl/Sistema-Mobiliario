package Modelo;

import java.time.LocalDate; // ¡Importante: Usamos java.time.LocalDate!
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio para una Orden de Compra (Encabezado).
 */
public class OrdenCompra {

    private Long idOrdenCompra;
    private Long idProveedor; // Clave foránea a la tabla Proveedor
    private Long idUsuarioEmisor; // Clave foránea a la tabla Usuario (quién la creó)
    private LocalDate fechaEmision; // TIPO DE DATO CORREGIDO
    private String numeroReferencia; // Puede ser el número secuencial generado
    private String estado; // Ej: "PENDIENTE", "AUTORIZADA", "RECIBIDA_COMPLETA", etc.
    private Double montoTotal; // Se calcula a partir de los detalles
    
    // Campo auxiliar / Principal (según tu vista) para mostrar el nombre del Proveedor
    private String nombreProveedor;
    
    // Campo auxiliar para mostrar el nombre del Usuario Emisor en Consultas/Listados
    private String nombreUsuarioEmisor; 
    
    // Lista de detalles de la orden (No se mapea directamente a la tabla OC)
    private List<OrdenCompraDetalle> detalles; 

    // Constructor vacío (necesario para ORMs/DAOs)
    public OrdenCompra() {
        // Inicialización de la lista de detalles para evitar NullPointerExceptions
        this.detalles = new ArrayList<>(); 
    }
    
    // CONSTRUCTOR ADAPTADO PARA LA VISTA: (Orden: proveedor, emisor_id, ref)
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

    // -------------------------------------------------------------
    // --- Getters y Setters ---
    // -------------------------------------------------------------

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
    
    // FIRMA CORREGIDA PARA LocalDate
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