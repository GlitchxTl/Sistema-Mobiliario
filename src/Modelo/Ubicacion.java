package Modelo;

/**
 * Modelo de Ubicación con dimensiones físicas.
 */
public class Ubicacion {
    private int id_ubicacion;
    private String nombre;
    private double altura;       // metros
    private double anchura;      // metros
    private double profundidad;  // metros
    private double capacidad;    // metros cúbicos = altura * anchura * profundidad
    private double capacidadRestante; // calculada según artículos
    private String descripcion;
    
    // ⭐ CAMBIO 1: Nuevo atributo para soft-delete ⭐
    private boolean deshabilitado; 

    public Ubicacion() {
        this.deshabilitado = false; // Por defecto, habilitada
    }

    public Ubicacion(int id_ubicacion, String nombre, double altura, double anchura, double profundidad, String descripcion) {
        this(); // Llama al constructor vacío para inicializar deshabilitado = false
        this.id_ubicacion = id_ubicacion;
        this.nombre = nombre;
        this.altura = altura;
        this.anchura = anchura;
        this.profundidad = profundidad;
        this.capacidad = altura * anchura * profundidad;
        this.descripcion = descripcion;
    }

    public Ubicacion(String nombre, double altura, double anchura, double profundidad, String descripcion) {
        this(0, nombre, altura, anchura, profundidad, descripcion);
    }
    
    // Constructor completo (usado por DAO al leer la DB)
    public Ubicacion(int id_ubicacion, String nombre, double altura, double anchura, double profundidad, String descripcion, boolean deshabilitado) {
        this(id_ubicacion, nombre, altura, anchura, profundidad, descripcion);
        this.deshabilitado = deshabilitado;
    }

    // --- Getters y Setters ---
    public int getId_ubicacion() { return id_ubicacion; }
    public void setId_ubicacion(int id_ubicacion) { this.id_ubicacion = id_ubicacion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

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
    
    // ⭐ CAMBIO 2: Getter y Setter para deshabilitado ⭐
    public boolean isDeshabilitado() { return deshabilitado; }
    public void setDeshabilitado(boolean deshabilitado) { this.deshabilitado = deshabilitado; }

    private void recalcularCapacidad() {
        this.capacidad = altura * anchura * profundidad;
    }

    @Override
    public String toString() {
        String estado = deshabilitado ? " (Deshabilitada)" : "";
        return nombre + " (" + String.format("%.2f", capacidad) + " m³)" + estado;
    }
}

