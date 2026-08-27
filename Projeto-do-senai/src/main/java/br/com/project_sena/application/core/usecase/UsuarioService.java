package br.com.project_sena.application.core.usecase;

import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.exception.EmailDuplicadoException;
import br.com.project_sena.application.core.domain.exception.SenhaException;
import br.com.project_sena.application.core.domain.exception.UsuarioNotFoundException;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.core.domain.vo.PoliticaSenha;
import br.com.project_sena.application.port.in.UsuarioUseCase;
import br.com.project_sena.application.port.in.command.AlterarPerfilCommand;
import br.com.project_sena.application.port.in.command.AlterarSenhaCommand;
import br.com.project_sena.application.port.in.command.AtualizarUsuarioCommand;
import br.com.project_sena.application.port.in.command.CadastrarUsuarioCommand;
import br.com.project_sena.application.port.out.CriptografiaPort;
import br.com.project_sena.application.port.out.TransacaoPort;
import br.com.project_sena.application.port.out.UsuarioRepository;

/**
 * Use case de usuarios.
 *
 * <p>Classe sem anotacao de framework: e' instanciada como bean em
 * {@code config.BeanConfiguration}. Isso mantem o nucleo compilavel e testavel sem
 * Spring no classpath.</p>
 */
public class UsuarioService implements UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final CriptografiaPort criptografia;
    private final TransacaoPort transacao;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          CriptografiaPort criptografia,
                          TransacaoPort transacao) {
        this.usuarioRepository = usuarioRepository;
        this.criptografia = criptografia;
        this.transacao = transacao;
    }

    @Override
    public Usuario cadastrar(CadastrarUsuarioCommand command) {
        PoliticaSenha.validar(command.password());
        return transacao.executar(() -> {
            Usuario usuario = Usuario.novo(
                    command.name(), command.login(), null, command.perfil());

            // Checagem dentro da transacao: fora dela, duas requisicoes simultaneas
            // passariam as duas pelo existsByLogin antes de qualquer insert.
            if (usuarioRepository.existsByLogin(usuario.getLogin())) {
                throw new EmailDuplicadoException(
                        "Ja existe um usuario com o login " + usuario.getLogin());
            }
            usuario.definirSenhaCodificada(criptografia.codificar(command.password()));
            return usuarioRepository.save(usuario);
        });
    }

    @Override
    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario nao encontrado: " + id));
    }

    @Override
    public Pagina<Usuario> listar(UsuarioEnum status, PaginaRequest paginaRequest) {
        return usuarioRepository.findByStatus(status, paginaRequest);
    }

    @Override
    public Usuario atualizar(AtualizarUsuarioCommand command) {
        return transacao.executar(() -> {
            Usuario usuario = buscar(command.id());
            if (command.login() != null && !command.login().isBlank()
                    && usuarioRepository.existsByLoginAndIdNot(
                            command.login().trim().toLowerCase(), command.id())) {
                throw new EmailDuplicadoException(
                        "Ja existe outro usuario com o login " + command.login());
            }
            usuario.atualizarDados(command.name(), command.login(), command.perfil());
            return usuarioRepository.save(usuario);
        });
    }

    @Override
    public Usuario alterarPerfil(AlterarPerfilCommand command) {
        return transacao.executar(() -> {
            Usuario usuario = buscar(command.id());
            usuario.alterarPerfil(command.perfil());
            return usuarioRepository.save(usuario);
        });
    }

    @Override
    public void alterarSenha(AlterarSenhaCommand command) {
        PoliticaSenha.validar(command.novaSenha());
        transacao.executar(() -> {
            Usuario usuario = buscar(command.id());
            if (!criptografia.confere(command.senhaAtual(), usuario.getPassword())) {
                throw new SenhaException("Senha atual incorreta");
            }
            if (criptografia.confere(command.novaSenha(), usuario.getPassword())) {
                throw new SenhaException("A nova senha deve ser diferente da atual");
            }
            usuario.definirSenhaCodificada(criptografia.codificar(command.novaSenha()));
            return usuarioRepository.save(usuario);
        });
    }

    @Override
    public void inativar(Long id) {
        transacao.executar(() -> {
            Usuario usuario = buscar(id);
            usuario.inativar();
            return usuarioRepository.save(usuario);
        });
    }

    @Override
    public Usuario reativar(Long id) {
        return transacao.executar(() -> {
            Usuario usuario = buscar(id);
            usuario.reativar();
            return usuarioRepository.save(usuario);
        });
    }
}
