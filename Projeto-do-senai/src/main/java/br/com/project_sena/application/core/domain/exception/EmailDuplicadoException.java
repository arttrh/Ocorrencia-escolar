package br.com.project_sena.application.core.domain.exception;

public class EmailDuplicadoException extends RegraDeNegocioException {

    public EmailDuplicadoException(String message) {
        super(message);
    }
}
