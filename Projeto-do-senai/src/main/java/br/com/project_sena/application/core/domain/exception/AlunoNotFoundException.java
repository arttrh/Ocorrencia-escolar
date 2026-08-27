package br.com.project_sena.application.core.domain.exception;

public class AlunoNotFoundException extends RecursoNaoEncontradoException {

    public AlunoNotFoundException(String message) {
        super(message);
    }
}
