package br.com.project_sena.application.core.domain.exception;

/**
 * Raiz de todas as excecoes de negocio.
 *
 * <p>Vive no dominio (e nao em {@code br.com.project_sena.exception}) porque quem as
 * lanca e' o nucleo da aplicacao. A traducao para status HTTP e' responsabilidade do
 * adaptador web, nao do dominio.</p>
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
