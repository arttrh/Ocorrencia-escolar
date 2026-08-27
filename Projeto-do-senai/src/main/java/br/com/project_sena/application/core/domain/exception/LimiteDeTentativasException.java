package br.com.project_sena.application.core.domain.exception;

/** Lancada quando o rate limiter recusa uma tentativa de login. */
public class LimiteDeTentativasException extends DomainException {

    private final long segundosParaLiberar;

    public LimiteDeTentativasException(String message, long segundosParaLiberar) {
        super(message);
        this.segundosParaLiberar = segundosParaLiberar;
    }

    public long getSegundosParaLiberar() {
        return segundosParaLiberar;
    }
}
