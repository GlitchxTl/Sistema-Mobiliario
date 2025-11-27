package Modelo;

import util.ConexionBD; 
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class ReporteDAO {
    

    public List<LogAuditoriaSistema> obtenerLogAuditoriaSistema() {
        List<LogAuditoriaSistema> listaLog = new ArrayList<>();
        
        
        String sql = "SELECT "
                   + "    LT.fecha_hora AS FechaHora, "
                   + "    US.nombre_usuario AS Usuario, "
                   + "    LT.accion AS AccionRealizada, "
                   + "    LT.tabla_afectada AS RecursoAfectado, "
                   + "    LT.id_registro_afectado AS IDRegistro, "
                   + "    LT.descripcion_detalle AS Detalle "
                   + "FROM "
                   + "    log_transacciones LT "
                   + "LEFT JOIN "
                   + "    usuario US ON LT.id_usuario = US.id_usuario "
                   + "ORDER BY "
                   + "    LT.fecha_hora DESC";

        try (Connection con = ConexionBD.conectar();
             Statement st = con.createStatement(); 
             ResultSet rs = st.executeQuery(sql)) {

            
            if (!rs.isBeforeFirst() && rs.getRow() == 0) { 
                 System.out.println("DEBUG DAO: La base de datos devolvió cero registros para log_transacciones.");
            }
            
            while (rs.next()) {
                Timestamp fechaHoraSql = rs.getTimestamp("FechaHora");
                LocalDateTime fechaHora = (fechaHoraSql != null) ? fechaHoraSql.toLocalDateTime() : null;
                
                String nombreUsuario = rs.getString("Usuario");
                
                listaLog.add(new LogAuditoriaSistema(
                    fechaHora,
                    (nombreUsuario == null) ? "Usuario Eliminado/Sistema" : nombreUsuario,
                    rs.getString("AccionRealizada"),
                    rs.getString("RecursoAfectado"),
                    rs.getInt("IDRegistro"), 
                    rs.getString("Detalle")
                ));
            }
            
            System.out.println("DEBUG DAO: Registros mapeados exitosamente: " + listaLog.size());

        } catch (Exception e) {
            
            System.err.println("FATAL ERROR - No se pudo obtener el Log de Auditoría:");
            e.printStackTrace();
        }
        return listaLog;
    }
}
