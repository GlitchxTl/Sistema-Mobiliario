package Modelo;

/**
 * Clase MODELO adaptada para contener el REPORTE MENSUAL de Gastos Operativos.
 * Los campos reflejan las columnas agregadas de la vista SQL 'v_gastos_operativos'
 * (Año, Mes, Total Gastado, y Número de Transacciones).
 */
public class GastoOperativo {

    private int anio;
    private int mes;
    private Double totalGastosMes; // Corresponde a SUM(COALESCE(M.costo_divisa, M.costo_bs))
    private Long numTransacciones; // Corresponde a COUNT(M.id_movimiento)

    // Constructor vacío
    public GastoOperativo() {
    }

    // Constructor para el DAO (Recibe los datos del reporte mensual)
    public GastoOperativo(int anio, int mes, Double totalGastosMes, Long numTransacciones) {
        this.anio = anio;
        this.mes = mes;
        this.totalGastosMes = totalGastosMes;
        this.numTransacciones = numTransacciones;
    }

    // -------------------------------------------------------------
    // --- Getters y Setters ---
    // -------------------------------------------------------------

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
    
    // Los campos de gasto individual (idGasto, fechaHora, descripcion, etc.) 
    // han sido eliminados para coincidir con la vista agregada 'v_gastos_operativos'.

    // -------------------------------------------------------------
    // --- Método toString (Útil para depuración) ---
    // -------------------------------------------------------------
    
    @Override
    public String toString() {
        return "GastoOperativo{" + // Mantengo el nombre de la clase, aunque ahora representa un reporte
                "anio=" + anio +
                ", mes=" + mes +
                ", totalGastosMes=" + totalGastosMes +
                ", numTransacciones=" + numTransacciones +
                '}';
    }
}
