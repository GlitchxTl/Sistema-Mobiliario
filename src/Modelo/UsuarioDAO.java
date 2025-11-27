package Modelo;

import util.ConexionBD;
import java.sql.*;


public class UsuarioDAO {


    public Usuario autenticar(String login, String password) throws SQLException {
        String sql = "SELECT u.*, r.nombre_rol AS rol " +
                     "FROM usuario u " +
                     "JOIN rol r ON u.id_rol = r.id_rol " +
                     "WHERE u.login = ? AND u.password_hash = SHA2(?, 256)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, password); 

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuario.setLogin(rs.getString("login"));
                    usuario.setIdRol(rs.getInt("id_rol"));
                    
                    try {
                        usuario.setRol(rs.getString("rol"));
                    } catch (Exception ignore) { /* si no existe el setter, lo ignora */ }
                    return usuario;
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Registra un nuevo usuario en la BD. Lanza SQLException si hay error.
     * @param usuario objeto Usuario (contiene password en plain text si tu BD aplica SHA2)
     * @return true si se insertó al menos una fila
     * @throws SQLException si ocurre un error de BD
     */
    public boolean registrarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nombre_usuario, login, password_hash, id_rol) VALUES (?, ?, SHA2(?, 256), ?)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getPasswordHash()); // se asume contraseña en claro aquí para que SHA2 la hashee
            stmt.setInt(4, usuario.getIdRol());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    /**
     * Verifica si existe un usuario por login. Lanza SQLException si hay error.
     * @param login nombre de usuario
     * @return true si existe, false si no
     * @throws SQLException si ocurre un error de BD
     */
    public boolean existeUsuario(String login) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE login = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                } else {
                    return false;
                }
            }
        }
    }
}




