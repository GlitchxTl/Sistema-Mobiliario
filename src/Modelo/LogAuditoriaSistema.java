

package Modelo;

import java.time.LocalDateTime;

public class LogAuditoriaSistema {
    
    
    private LocalDateTime fechaHora;
    private String usuario;
    private String accionRealizada; 
    private String recursoAfectado; 
    private int idRegistroAfectado;
    private String detalle;

    
    public LogAuditoriaSistema(LocalDateTime fechaHora, String usuario, String accionRealizada, String recursoAfectado, int idRegistroAfectado, String detalle) {
        this.fechaHora = fechaHora;
        this.usuario = usuario;
        this.accionRealizada = accionRealizada;
        this.recursoAfectado = recursoAfectado;
        this.idRegistroAfectado = idRegistroAfectado;
        this.detalle = detalle;
    }

  
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getUsuario() { return usuario; }
    public String getAccionRealizada() { return accionRealizada; }
    public String getRecursoAfectado() { return recursoAfectado; }
    public int getIdRegistroAfectado() { return idRegistroAfectado; }
    public String getDetalle() { return detalle; }
    
    
}
