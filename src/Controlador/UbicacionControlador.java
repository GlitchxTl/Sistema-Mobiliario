package Controlador;

import Modelo.Ubicacion;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controlador simple para Ubicaciones.
 * Mantiene una lista estática en memoria para persistir mientras la JVM esté viva.
 * Puede reemplazarse fácilmente por un DAO con conexión a BD.
 */
public class UbicacionControlador {

    // Repositorio en memoria
    private static final List<Ubicacion> LIST = new ArrayList<>();
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    public UbicacionControlador() {
        // Datos iniciales opcionales
        if (LIST.isEmpty()) {
            LIST.add(new Ubicacion(NEXT_ID.getAndIncrement(), "Bodega A", 2.5, 4.0, 10.0, "Bodega principal"));
            LIST.add(new Ubicacion(NEXT_ID.getAndIncrement(), "Estantería 1", 1.0, 2.0, 2.5, "Estantería en pasillo 1"));
        }
    }

    /**
     * Devuelve una copia de la lista de ubicaciones.
     */
    public List<Ubicacion> listarTodas() {
        synchronized (LIST) {
            return new ArrayList<>(LIST);
        }
    }

    /**
     * Busca ubicaciones cuyo nombre contenga una parte del texto proporcionado (no sensible a mayúsculas/minúsculas).
     * Si el texto está vacío, devuelve todas.
     */
    public List<Ubicacion> buscarPorNombre(String nombreParcial) {
        synchronized (LIST) {
            if (nombreParcial == null || nombreParcial.trim().isEmpty()) {
                return new ArrayList<>(LIST);
            }
            String criterio = nombreParcial.trim().toLowerCase();
            List<Ubicacion> resultado = new ArrayList<>();
            for (Ubicacion u : LIST) {
                if (u.getNombre() != null && u.getNombre().toLowerCase().contains(criterio)) {
                    resultado.add(u);
                }
            }
            return resultado;
        }
    }

    /**
     * Crea una nueva ubicación si no existe una con el mismo nombre.
     * La capacidad total se calcula automáticamente (altura * anchura * profundidad).
     */
    public boolean crearUbicacion(Ubicacion u) {
        if (u == null) return false;
        synchronized (LIST) {
            for (Ubicacion ex : LIST) {
                if (ex.getNombre().equalsIgnoreCase(u.getNombre())) return false;
            }
            u.setId_ubicacion(NEXT_ID.getAndIncrement());
            recalcularCapacidad(u);
            LIST.add(u);
            return true;
        }
    }

    /**
     * Actualiza una ubicación existente.
     */
    public boolean actualizarUbicacion(Ubicacion u) {
        if (u == null || u.getId_ubicacion() <= 0) return false;
        synchronized (LIST) {
            for (int i = 0; i < LIST.size(); i++) {
                Ubicacion actual = LIST.get(i);
                if (actual.getId_ubicacion() == u.getId_ubicacion()) {
                    // Evita duplicar nombres
                    for (Ubicacion ex : LIST) {
                        if (ex.getId_ubicacion() != u.getId_ubicacion() &&
                            ex.getNombre().equalsIgnoreCase(u.getNombre())) {
                            return false;
                        }
                    }
                    recalcularCapacidad(u);
                    LIST.set(i, u);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Elimina una ubicación por su id_ubicacion.
     */
    public boolean eliminarUbicacionPorId(int id_ubicacion) {
        synchronized (LIST) {
            return LIST.removeIf(u -> u.getId_ubicacion() == id_ubicacion);
        }
    }

    /**
     * Devuelve la lista de nombres de todas las ubicaciones.
     */
    public List<String> obtenerNombres() {
        synchronized (LIST) {
            List<String> nombres = new ArrayList<>();
            for (Ubicacion u : LIST) {
                nombres.add(u.getNombre());
            }
            return nombres;
        }
    }

    /**
     * Devuelve una ubicación por su id_ubicacion.
     */
    public Ubicacion obtenerPorId(int id_ubicacion) {
        synchronized (LIST) {
            for (Ubicacion u : LIST) {
                if (u.getId_ubicacion() == id_ubicacion) return u;
            }
            return null;
        }
    }

    /**
     * Recalcula la capacidad (m³) en función de altura × anchura × profundidad.
     */
    private void recalcularCapacidad(Ubicacion u) {
        double nuevaCapacidad = u.getAltura() * u.getAnchura() * u.getProfundidad();
        u.setCapacidad(nuevaCapacidad);
    }
}

