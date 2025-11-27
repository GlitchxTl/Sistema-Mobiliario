package Modelo;


public class GastoOperativo {

    private int anio;
    private int mes;
    private Double totalGastosMes; 
    private Long numTransacciones; 

    
    public GastoOperativo() {
    }

   
    public GastoOperativo(int anio, int mes, Double totalGastosMes, Long numTransacciones) {
        this.anio = anio;
        this.mes = mes;
        this.totalGastosMes = totalGastosMes;
        this.numTransacciones = numTransacciones;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public Double getTotalGastosMes() {
        return totalGastosMes;
    }

    public void setTotalGastosMes(Double totalGastosMes) {
        this.totalGastosMes = totalGastosMes;
    }

    public Long getNumTransacciones() {
        return numTransacciones;
    }

    public void setNumTransacciones(Long numTransacciones) {
        this.numTransacciones = numTransacciones;
    }
    

    @Override
    public String toString() {
        return "GastoOperativo{" + 
                "anio=" + anio +
                ", mes=" + mes +
                ", totalGastosMes=" + totalGastosMes +
                ", numTransacciones=" + numTransacciones +
                '}';
    }
}
