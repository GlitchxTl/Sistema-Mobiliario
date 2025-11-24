package Modelo;

public class Articulo {
    private int idArticulo;
    private String nombre;
    private String codigoBienNacional;
    private String categoria;
    private String detalles; 
    private double altura;
    private double anchura;
    private double profundidad;
    private double espacioUnitario;
    private boolean deshabilitado;

    public Articulo() {
        this.deshabilitado = false;
    }

    public Articulo(String nombre, String codigoBienNacional, String categoria, double altura, double anchura, double profundidad) {
        this();
        this.nombre = nombre;
        this.codigoBienNacional = codigoBienNacional;
        this.categoria = categoria;
        this.altura = altura;
        this.anchura = anchura;
        this.profundidad = profundidad;
        this.espacioUnitario = calcularEspacio();
        this.detalles = ""; 
    }
    
    public Articulo(String nombre, String codigoBienNacional, String categoria, String detalles, double altura, double anchura, double profundidad) {
        this(nombre, codigoBienNacional, categoria, altura, anchura, profundidad);
        this.detalles = detalles;
    }
    
    public double calcularEspacio() {
        return altura * anchura * profundidad;
    }

    private void recalcularEspacio() {
        this.espacioUnitario = calcularEspacio();
    }

    public int getIdArticulo() { return idArticulo; }
    public void setIdArticulo(int idArticulo) { this.idArticulo = idArticulo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigoBienNacional() { return codigoBienNacional; }
    public void setCodigoBienNacional(String codigoBienNacional) { this.codigoBienNacional = codigoBienNacional; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }

    public double getAltura() { return altura; }
    public void setAltura(double altura) { this.altura = altura; recalcularEspacio(); }

    public double getAnchura() { return anchura; }
    public void setAnchura(double anchura) { this.anchura = anchura; recalcularEspacio(); }

    public double getProfundidad() { return profundidad; }
    public void setProfundidad(double profundidad) { this.profundidad = profundidad; recalcularEspacio(); }

    public double getEspacioUnitario() { return espacioUnitario; }
    public void setEspacioUnitario(double espacioUnitario) { this.espacioUnitario = espacioUnitario; }

    public boolean isDeshabilitado() { return deshabilitado; }
    public void setDeshabilitado(boolean deshabilitado) { this.deshabilitado = deshabilitado; }

    @Override
    public String toString() {
        return nombre + " (" + codigoBienNacional + ")";
    }
}



