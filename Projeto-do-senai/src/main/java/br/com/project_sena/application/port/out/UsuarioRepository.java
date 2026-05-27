package br.com.project_sena.application.port.out;

import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    Page<Usuario> findAllByUsuarioEnum(UsuarioEnum status, Pageable pageable);

    void delete(Long id);

    UserDetails loadUserByUsername(String login);

    void reativar(Long id);
}
