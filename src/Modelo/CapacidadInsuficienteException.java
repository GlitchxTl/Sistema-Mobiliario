package Modelo;

import java.util.List;

/**
 * Excepción personalizada que se lanza cuando se intenta registrar una entrada
 * y la ubicación de destino no tiene suficiente capacidad restante.
 * <p>
 * Transporta la información necesaria para que la Vista pueda 
 * informar al usuario y ofrecer sugerencias.
 */
// Usamos RuntimeException para no forzar "throws" en métodos intermedios.
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

    // Getters para que la Vista pueda construir el mensaje de error
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
