package Modelo;

/**
 * Representa el valor total del inventario para una ubicación específica.
 */
public class ValorUbicacionDTO {

    private String nombreUbicacion;
    private Double valorTotal;

    // Constructor
    public ValorUbicacionDTO(String nombreUbicacion, Double valorTotal) {
        this.nombreUbicacion = nombreUbicacion;
        this.valorTotal = valorTotal;
    }

    // Getters
    public String getNombreUbicacion() {
        return nombreUbicacion;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    // Opcional: Setter (si necesitas mutabilidad)
    public void setNombreUbicacion(String nombreUbicacion) {
        this.nombreUbicacion = nombreUbicacion;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
