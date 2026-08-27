package br.com.project_sena.application.port.out;

import java.util.Optional;

import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;

public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByLogin(String login);

    boolean existsByLogin(String login);

    /** @return true se outro usuario (id diferente) ja usa esse login */
    boolean existsByLoginAndIdNot(String login, Long id);

    Pagina<Usuario> findByStatus(UsuarioEnum status, PaginaRequest paginaRequest);
}
