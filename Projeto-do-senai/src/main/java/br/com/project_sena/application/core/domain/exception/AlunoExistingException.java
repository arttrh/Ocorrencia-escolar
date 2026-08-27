package br.com.project_sena.application.core.domain.exception;

public class AlunoExistingException extends RegraDeNegocioException {

    public AlunoExistingException(String message) {
        super(message);
    }
}
