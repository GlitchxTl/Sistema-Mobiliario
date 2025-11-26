package Modelo;

/**
 * Entidad de dominio para una línea de Detalle de una Orden de Compra.
 */
public class OrdenCompraDetalle {

    private Long idDetalle;
    private Long idOrdenCompra; // Clave foránea a OrdenCompra
    private Long idArticulo;    // Clave foránea a Articulo
    private Integer cantidadPedida;
    private Integer cantidadRecibida;
    private Double precioUnitario;
    private Double subTotal; // cantidadPedida * precioUnitario
    
    // Campos auxiliares para Vistas/Reportes
    private String nombreArticulo;
    private String codigoArticulo;

    // Constructor vacío
    public OrdenCompraDetalle() {
        this.cantidadRecibida = 0; // Inicialmente 0
    }
    
    /**
     * CONSTRUCTOR AÑADIDO: Requerido por el DAO para mapear los resultados de la consulta.
     */
    public OrdenCompraDetalle(Long idDetalle, String codigoArticulo, String nombreArticulo, Integer cantidadPedida, Double precioUnitario) {
        this(); // Inicializa campos por defecto
        this.idDetalle = idDetalle;
        this.codigoArticulo = codigoArticulo;
        this.nombreArticulo = nombreArticulo;
        this.cantidadPedida = cantidadPedida;
        this.precioUnitario = precioUnitario;
    }

    // -------------------------------------------------------------
    // --- Getters y Setters ---
    // -------------------------------------------------------------
    
    public Long getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Long idDetalle) { this.idDetalle = idDetalle; }

    public Long getIdOrdenCompra() { return idOrdenCompra; }
    public void setIdOrdenCompra(Long idOrdenCompra) { this.idOrdenCompra = idOrdenCompra; }

    public Long getIdArticulo() { return idArticulo; }
    public void setIdArticulo(Long idArticulo) { this.idArticulo = idArticulo; }

    public void setIdArticulo(Integer idArticulo) {
        if (idArticulo != null) {
            this.idArticulo = idArticulo.longValue();
        } else {
            this.idArticulo = null;
        }
    }
    
    public Integer getCantidadPedida() { return cantidadPedida; }
    public void setCantidadPedida(Integer cantidadPedida) { this.cantidadPedida = cantidadPedida; }

    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Integer cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Double getSubTotal() { return subTotal; }
    public void setSubTotal(Double subTotal) { this.subTotal = subTotal; }

    public String getNombreArticulo() { return nombreArticulo; }
    public void setNombreArticulo(String nombreArticulo) { this.nombreArticulo = nombreArticulo; }

    public String getCodigoArticulo() { return codigoArticulo; }
    public void setCodigoArticulo(String codigoArticulo) { this.codigoArticulo = codigoArticulo; }
}