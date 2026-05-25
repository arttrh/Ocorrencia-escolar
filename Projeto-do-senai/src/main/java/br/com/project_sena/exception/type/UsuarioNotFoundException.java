package br.com.project_sena.exception.type;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(String message) {
        super(message);
    }
}
