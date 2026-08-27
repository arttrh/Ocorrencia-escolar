package br.com.project_sena.application.core.domain.exception;

public class UsuarioNotFoundException extends RecursoNaoEncontradoException {

    public UsuarioNotFoundException(String message) {
        super(message);
    }
}
