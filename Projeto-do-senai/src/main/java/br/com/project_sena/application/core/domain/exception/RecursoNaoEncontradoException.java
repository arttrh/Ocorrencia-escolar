package br.com.project_sena.application.core.domain.exception;

/** Marca as excecoes que o adaptador web traduz para 404. */
public abstract class RecursoNaoEncontradoException extends DomainException {

    protected RecursoNaoEncontradoException(String message) {
        super(message);
    }
}
