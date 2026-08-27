package br.com.project_sena.adapter.out.persistence;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.entity.UsuarioEntity;
import br.com.project_sena.adapter.out.persistence.jpa.UsuarioJpaRepository;
import br.com.project_sena.adapter.out.persistence.mapper.UsuarioMapperEntity;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.port.out.UsuarioRepository;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    /** Campo aceito na query string -> propriedade real da entidade. */
    private static final Map<String, String> ORDENACAO = Map.of(
            "id", "id",
            "name", "name",
            "login", "login",
            "role", "perfil",
            "perfil", "perfil",
            "status", "status");

    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioMapperEntity mapper;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository, UsuarioMapperEntity mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Usuario save(Usuario usuario) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(usuario)));
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByLogin(String login) {
        return jpaRepository.findByLogin(login).map(mapper::toDomain);
    }

    @Override
    public boolean existsByLogin(String login) {
        return jpaRepository.existsByLogin(login);
    }

    @Override
    public boolean existsByLoginAndIdNot(String login, Long id) {
        return jpaRepository.existsByLoginAndIdNot(login, id);
    }

    @Override
    public Pagina<Usuario> findByStatus(UsuarioEnum status, PaginaRequest paginaRequest) {
        return PaginacaoSpring.paraPagina(
                jpaRepository.findAllByStatus(
                        status, PaginacaoSpring.paraPageable(paginaRequest, ORDENACAO)),
                mapper::toDomain);
    }

    /** Exposto para o adaptador de seguranca, que precisa da entidade crua. */
    public Optional<UsuarioEntity> entidadePorLogin(String login) {
        return jpaRepository.findByLogin(login);
    }
}
