package br.com.project_sena.application.core.domain.exception;

/**
 * Falha de autenticacao. Mensagem propositalmente generica: nao revela se o erro foi
 * o login inexistente ou a senha errada, para nao permitir enumeracao de usuarios.
 */
public class CredenciaisInvalidasException extends DomainException {

    public CredenciaisInvalidasException() {
        super("Login ou senha invalidos");
    }

    public CredenciaisInvalidasException(String message) {
        super(message);
    }
}
