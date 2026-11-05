package br.com.project_sena.application.core.usecase;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.port.out.UsuarioRepository;
import br.com.project_sena.exception.type.EmailESenha.EmailDuplicadoException;
import br.com.project_sena.exception.type.Usuario.UsuarioNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void alterarSenha(String email, String atual, String nova) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (!passwordEncoder.matches(atual, usuario.getPassword()))
            throw new BadCredentialsException("Senha atual inválida");
        usuario.setPassword(passwordEncoder.encode(nova));
        usuarioRepository.save(usuario);
    }

    public Usuario cadastrar(Usuario dados) {
        dados.setPassword(passwordEncoder.encode(dados.getPassword()));
        if (usuarioRepository.existsByEmail(dados.getEmail())) {
            throw new EmailDuplicadoException("Email ja existe no sistema");
        }
        return usuarioRepository.save(dados);
    }

    public Usuario buscar(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
            () -> new UsuarioNotFoundException("ID do usuario nao encontrado: " + id));
        return usuario;
    }

    public Page<Usuario> listar(Pageable paginacao) {
        return usuarioRepository.findAllByUsuarioEnum(UsuarioEnum.ATIVO, paginacao);
    }

    public Page<Usuario> listarInvativos(Pageable paginacao) {
        return usuarioRepository.findAllByUsuarioEnum(UsuarioEnum.INVATIVO, paginacao);
    }

    public Usuario atualizar(Usuario dados, Long id) {
        Usuario usuarioBucar = usuarioRepository.findById(id).orElseThrow(
            () -> new UsuarioNotFoundException("ID do usuario nao encontrado: " + id));
        if (!usuarioBucar.getEmail().equals(dados.getEmail())
            && usuarioRepository.existsByEmail(dados.getEmail()))
            throw new EmailDuplicadoException("Email já existe no sistema");
        if (dados.getName() != null && !dados.getName().isBlank())
            usuarioBucar.setName(dados.getName());
        usuarioBucar.atualizarUsuario(dados.getPerfil(), dados.getEmail(), dados.getPassword());
        if (dados.getPassword() != null && !dados.getPassword().isBlank())
            usuarioBucar.setPassword(passwordEncoder.encode(dados.getPassword()));
        return usuarioRepository.save(usuarioBucar);
    }

    public void excluir(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
            () -> new UsuarioNotFoundException("ID do usuario nao encontrado: " + id));
        usuario.excluir(UsuarioEnum.INVATIVO);
        usuarioRepository.save(usuario);
    }

    public void reativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
            () -> new UsuarioNotFoundException("ID do usuario nao encontrado: " + id));
        usuario.reativar(UsuarioEnum.ATIVO);
        usuarioRepository.save(usuario);
    }
}
