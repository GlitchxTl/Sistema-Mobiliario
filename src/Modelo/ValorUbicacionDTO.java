package Modelo;


public class ValorUbicacionDTO {

    private String nombreUbicacion;
    private Double valorTotal;


    public ValorUbicacionDTO(String nombreUbicacion, Double valorTotal) {
        this.nombreUbicacion = nombreUbicacion;
        this.valorTotal = valorTotal;
    }


    public String getNombreUbicacion() {
        return nombreUbicacion;
    }

    public Double getValorTotal() {
        return valorTotal;
    }


    public void setNombreUbicacion(String nombreUbicacion) {
        this.nombreUbicacion = nombreUbicacion;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
