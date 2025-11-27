package Controlador;

import Modelo.Usuario;
import Modelo.UsuarioDAO;
import Vista.LoginVista;
import Vista.PrincipalVista;

import javax.swing.SwingWorker;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;


public class AuthControlador {

    private final UsuarioDAO usuarioDAO;
    private final LoginVista view; 

    public AuthControlador() {
        this.view = null;
        this.usuarioDAO = new UsuarioDAO();
    }

    public AuthControlador(LoginVista view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
        if (this.view != null) {
            this.view.setAuthControlador(this);
        }
    }

    public void autenticarUsuario(final String login, final String password) {
        if (login == null || login.trim().isEmpty() || password == null || password.isEmpty()) {
            if (view != null) view.showError("Usuario y contraseña son obligatorios");
            else System.err.println("Usuario y contraseña son obligatorios");
            return;
        }

        new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                return usuarioDAO.autenticar(login.trim(), password);
            }

            @Override
            protected void done() {
                try {
                    Usuario usuario = get();
                    if (usuario != null) {
                        PrincipalVista principal = new PrincipalVista(usuario);
                        principal.setVisible(true);
                        if (view != null) view.dispose();
                    } else {
                        if (view != null) view.showError("Credenciales incorrectas");
                        else System.err.println("Credenciales incorrectas");
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    if (view != null) view.showError("Operación interrumpida");
                    else System.err.println("Operación interrumpida");
                    ie.printStackTrace();
                } catch (ExecutionException ee) {
                    Throwable causa = ee.getCause();
                    String mensaje = (causa != null) ? causa.getMessage() : ee.getMessage();
                    if (view != null) view.showError("Error al autenticar: " + mensaje);
                    else System.err.println("Error al autenticar: " + mensaje);
                    if (causa != null) causa.printStackTrace();
                    else ee.printStackTrace();
                } catch (Exception ex) {
                    if (view != null) view.showError("Error inesperado: " + ex.getMessage());
                    else System.err.println("Error inesperado: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    
    public RegistrationResult registrarUsuario(String nombre, String login, String password, int idRol) {
        
        nombre = (nombre == null) ? "" : nombre.trim();
        login = (login == null) ? "" : login.trim();
        password = (password == null) ? "" : password;

        
        if (nombre.isEmpty()) {
            return RegistrationResult.fail("El nombre completo es obligatorio.");
        }
        if (login.isEmpty()) {
            return RegistrationResult.fail("El usuario es obligatorio.");
        }
        if (!login.matches("^[A-Za-z0-9_]{3,30}$")) {
            return RegistrationResult.fail("El nombre de usuario debe tener 3-30 caracteres y solo letras, números o guion bajo.");
        }
        if (password.length() < 6) {
            return RegistrationResult.fail("La contraseña debe tener al menos 6 caracteres.");
        }

        // acceso al DAO
        try {
            if (usuarioDAO.existeUsuario(login)) {
                return RegistrationResult.fail("El usuario ya existe.");
            }

            Usuario nuevo = new Usuario();
            nuevo.setNombreUsuario(nombre);
            nuevo.setLogin(login);
            nuevo.setPasswordHash(password);

            
            if (idRol <= 0) idRol = 1;
            nuevo.setIdRol(idRol);

            boolean creado = usuarioDAO.registrarUsuario(nuevo);
            if (creado) {
                return RegistrationResult.ok("Usuario registrado exitosamente.");
            } else {
                return RegistrationResult.fail("No se pudo registrar el usuario (operación fallida).");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return RegistrationResult.fail("Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return RegistrationResult.fail("Error inesperado: " + e.getMessage());
        }
    }

    public boolean registrarUsuarioBool(String nombre, String login, String password) {
        
        return registrarUsuario(nombre, login, password, 1).isSuccess();
    }
}





