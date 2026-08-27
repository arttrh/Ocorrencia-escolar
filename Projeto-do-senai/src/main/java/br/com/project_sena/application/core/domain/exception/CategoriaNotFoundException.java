package br.com.project_sena.application.core.domain.exception;

public class CategoriaNotFoundException extends RecursoNaoEncontradoException {

    public CategoriaNotFoundException(String message) {
        super(message);
    }
}
