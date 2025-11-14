package Modelo;

/**
 * Modelo de Ubicación con dimensiones físicas.
 */
public class Ubicacion {
    private int id_ubicacion;
    private String nombre;
    private double altura;      // metros
    private double anchura;     // metros
    private double profundidad; // metros
    private double capacidad;   // metros cúbicos = altura * anchura * profundidad
    private double capacidadRestante; // calculada según artículos
    private String descripcion;

    public Ubicacion() {}

    public Ubicacion(int id_ubicacion, String nombre, double altura, double anchura, double profundidad, String descripcion) {
        this.id_ubicacion = id_ubicacion;
        this.nombre = nombre;
        this.altura = altura;
        this.anchura = anchura;
        this.profundidad = profundidad;
        // La capacidad se calcula antes de guardar en el DAO
        this.capacidad = altura * anchura * profundidad;
        this.descripcion = descripcion;
    }

    public Ubicacion(String nombre, double altura, double anchura, double profundidad, String descripcion) {
        this(0, nombre, altura, anchura, profundidad, descripcion);
    }

    // --- Getters y Setters ---
    public int getId_ubicacion() { return id_ubicacion; }
    public void setId_ubicacion(int id_ubicacion) { this.id_ubicacion = id_ubicacion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // NOTA: Se eliminaron las llamadas a recalcularCapacidad() en los setters. 
    // Esto se maneja en el DAO antes de la persistencia.
    public double getAltura() { return altura; }
    public void setAltura(double altura) { this.altura = altura; } 

    public double getAnchura() { return anchura; }
    public void setAnchura(double anchura) { this.anchura = anchura; }

    public double getProfundidad() { return profundidad; }
    public void setProfundidad(double profundidad) { this.profundidad = profundidad; }

    public double getCapacidad() { return capacidad; }
    public void setCapacidad(double capacidad) { this.capacidad = capacidad; }

    public double getCapacidadRestante() { return capacidadRestante; }
    public void setCapacidadRestante(double capacidadRestante) { this.capacidadRestante = capacidadRestante; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    private void recalcularCapacidad() {
        this.capacidad = altura * anchura * profundidad;
    }

    @Override
    public String toString() {
        return nombre + " (" + String.format("%.2f", capacidad) + " m³)";
    }
}

