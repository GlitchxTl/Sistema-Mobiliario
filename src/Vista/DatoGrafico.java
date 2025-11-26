package Modelo;

public class DatoGrafico {
    private String nombre;
    private int valor;

    public DatoGrafico(String nombre, int valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getNombre() { return nombre; }
    public int getValor() { return valor; }
}
