package Modelo; // O tu paquete de modelos

public class ArticuloStock {
    private String nombreArticulo;
    private int stock;

    public ArticuloStock(String nombreArticulo, int stock) {
        this.nombreArticulo = nombreArticulo;
        this.stock = stock;
    }

    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public int getStock() {
        return stock;
    }

    // Opcional: toString para depuración
    @Override
    public String toString() {
        return "ArticuloStock{" + "nombreArticulo='" + nombreArticulo + '\'' + ", stock=" + stock + '}';
    }
}
