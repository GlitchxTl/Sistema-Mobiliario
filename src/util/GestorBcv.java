package util;

public class GestorBcv {

    private static GestorBcv instancia;
    private double tasaActual = -1.0; 

    private GestorBcv() { }

    public static synchronized GestorBcv getInstance() {
        if (instancia == null) {
            instancia = new GestorBcv();
        }
        return instancia;
    }

    public double getTasaActual() {
        return tasaActual;
    }

    public void setTasaActual(double tasa) {
        this.tasaActual = tasa;
    }
}