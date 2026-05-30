package br.com.project_sena.application.core.usecase;
import br.com.project_sena.adapter.out.repository.mapper.UsuarioMapperEntity;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.port.out.UsuarioRepository;
import br.com.project_sena.exception.type.UsuarioNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private UsuarioMapperEntity usuarioMapperEntity;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, UsuarioMapperEntity usuarioMapperEntity) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapperEntity = usuarioMapperEntity;
    }

    public Usuario cadastrar(Usuario dados) {
        dados.setPassword(passwordEncoder.encode(dados.getPassword()));
        return usuarioRepository.save(dados);
    }

    public Usuario buscar(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException("ID do usuario nao encontrado: " + id));
        return usuario;
    }

    public Page<Usuario> listar(Pageable paginacao) {
        return usuarioRepository.findAllByUsuarioEnum(UsuarioEnum.ATIVO, paginacao);
    }

    public Page<Usuario> listarInvativos(Pageable paginacao) {
        return usuarioRepository.findAllByUsuarioEnum(UsuarioEnum.INVATIVO, paginacao);
    }

    public Usuario atualizar(Usuario usuario, Long id) {
        Usuario usuarioBucar = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException("ID do usuario nao encontrado: " + id));
        usuarioBucar.atualizarUsuario(usuario);
        return usuarioRepository.save(usuarioBucar);
    }

    public void excluir(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException("ID do usuario nao encontrado: " + id));
        usuario.excluir();
        usuarioRepository.save(usuario);
    }

    public void reativar(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException("ID do usuario nao encontrado: " + id));
        usuario.reativar(UsuarioEnum.ATIVO);
        usuarioRepository.save(usuario);
    }
}
