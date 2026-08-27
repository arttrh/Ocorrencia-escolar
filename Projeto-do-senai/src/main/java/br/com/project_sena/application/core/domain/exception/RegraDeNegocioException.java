package br.com.project_sena.application.core.domain.exception;

/** Marca as excecoes que o adaptador web traduz para 400. */
public class RegraDeNegocioException extends DomainException {

    public RegraDeNegocioException(String message) {
        super(message);
    }
}
