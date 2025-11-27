package Controlador;


public class RegistrationResult {
    private final boolean success;
    private final String message;

    public RegistrationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public static RegistrationResult ok(String message) {
        return new RegistrationResult(true, message);
    }

    public static RegistrationResult fail(String message) {
        return new RegistrationResult(false, message);
    }
}


