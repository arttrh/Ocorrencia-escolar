package br.com.project_sena.application.core.usecase;

import br.com.project_sena.application.core.domain.exception.CredenciaisInvalidasException;
import br.com.project_sena.application.core.domain.exception.LimiteDeTentativasException;
import br.com.project_sena.application.core.domain.exception.UsuarioInativoException;
import br.com.project_sena.application.core.domain.exception.UsuarioNotFoundException;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.port.in.AutenticarUsuarioUseCase;
import br.com.project_sena.application.port.in.command.CredenciaisCommand;
import br.com.project_sena.application.port.out.CriptografiaPort;
import br.com.project_sena.application.port.out.RateLimiterPort;
import br.com.project_sena.application.port.out.TokenPort;
import br.com.project_sena.application.port.out.UsuarioRepository;

/**
 * Autenticacao por login e senha.
 *
 * <p>Mudancas de seguranca em relacao a versao anterior:</p>
 * <ul>
 *   <li>o rate limit e' <em>consumido</em> aqui (antes o bucket era obtido no controller
 *       e o resultado jogado fora, deixando o login sem protecao real);</li>
 *   <li>usuario inexistente e senha errada devolvem a mesma mensagem, evitando enumeracao
 *       de contas;</li>
 *   <li>quando o login nao existe a senha ainda e' comparada contra um hash descartavel,
 *       para que o tempo de resposta nao denuncie a existencia da conta;</li>
 *   <li>usuario inativo nao recebe token.</li>
 * </ul>
 */
public class AutenticacaoService implements AutenticarUsuarioUseCase {

    /** Hash BCrypt de uma senha aleatoria, usado so' para gastar tempo de CPU. */
    private static final String HASH_FALSO =
            "$2a$10$7EqJtq98hPqEX7fNZaFWoO1a1Zp3sJ4kQhVQ8oQ0Y8u3JQY2vJm1a";

    private final UsuarioRepository usuarioRepository;
    private final CriptografiaPort criptografia;
    private final TokenPort tokenPort;
    private final RateLimiterPort rateLimiter;

    public AutenticacaoService(UsuarioRepository usuarioRepository,
                               CriptografiaPort criptografia,
                               TokenPort tokenPort,
                               RateLimiterPort rateLimiter) {
        this.usuarioRepository = usuarioRepository;
        this.criptografia = criptografia;
        this.tokenPort = tokenPort;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public Resultado autenticar(CredenciaisCommand credenciais) {
        String login = normalizar(credenciais.login());

        RateLimiterPort.Veredito veredito = rateLimiter.consumir(chaveDeLimite(credenciais, login));
        if (!veredito.permitido()) {
            throw new LimiteDeTentativasException(
                    "Muitas tentativas de login. Tente novamente em "
                            + veredito.segundosParaLiberar() + " segundos.",
                    veredito.segundosParaLiberar());
        }

        Usuario usuario = usuarioRepository.findByLogin(login).orElse(null);
        if (usuario == null) {
            criptografia.confere(credenciais.senha(), HASH_FALSO);
            throw new CredenciaisInvalidasException();
        }
        if (!criptografia.confere(credenciais.senha(), usuario.getPassword())) {
            throw new CredenciaisInvalidasException();
        }
        if (!usuario.isAtivo()) {
            throw new UsuarioInativoException("Usuario inativo. Procure o administrador.");
        }

        return new Resultado(
                tokenPort.gerarToken(usuario),
                usuario.getId(),
                usuario.getLogin(),
                usuario.getPerfil());
    }

    @Override
    public Usuario carregarPorLogin(String login) {
        return usuarioRepository.findByLogin(normalizar(login))
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario nao encontrado: " + login));
    }

    /**
     * Limita por origem <em>e</em> login: forca bruta em uma conta e' barrada mesmo
     * distribuida entre IPs, e uma rede compartilhada nao bloqueia todos os usuarios.
     */
    private String chaveDeLimite(CredenciaisCommand credenciais, String login) {
        String origem = credenciais.origem() == null ? "desconhecida" : credenciais.origem();
        return origem + "|" + login;
    }

    private static String normalizar(String login) {
        return login == null ? "" : login.trim().toLowerCase();
    }
}
