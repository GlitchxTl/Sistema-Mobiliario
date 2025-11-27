package Modelo;

import java.util.List;


public class CapacidadInsuficienteException extends RuntimeException {

    private final List<Ubicacion> sugerencias;
    private final double capacidadRestante;
    private final double espacioRequerido;
    private final String nombreUbicacion;

    public CapacidadInsuficienteException(String message, String nombreUbicacion, double capacidadRestante, double espacioRequerido, List<Ubicacion> sugerencias) {
        super(message);
        this.nombreUbicacion = nombreUbicacion;
        this.capacidadRestante = capacidadRestante;
        this.espacioRequerido = espacioRequerido;
        this.sugerencias = sugerencias;
    }

    
    public List<Ubicacion> getSugerencias() {
        return sugerencias;
    }

    public double getCapacidadRestante() {
        return capacidadRestante;
    }

    public double getEspacioRequerido() {
        return espacioRequerido;
    }

    public String getNombreUbicacion() {
        return nombreUbicacion;
    }
}
