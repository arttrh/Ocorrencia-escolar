package br.com.project_sena.application.port.in;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.port.in.command.CredenciaisCommand;

/**
 * Porta de entrada de autenticacao.
 *
 * <p>Nao devolve {@code ResponseEntity}: o use case entrega um resultado de dominio e
 * quem decide status HTTP e' o controller.</p>
 */
public interface AutenticarUsuarioUseCase {

    Resultado autenticar(CredenciaisCommand credenciais);

    /** Usado pelo filtro de seguranca para reidratar o usuario a partir do token. */
    Usuario carregarPorLogin(String login);

    record Resultado(String token, Long usuarioId, String login, PerfilEnum perfil) {
    }
}
