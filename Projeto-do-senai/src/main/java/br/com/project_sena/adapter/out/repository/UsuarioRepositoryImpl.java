package br.com.project_sena.adapter.out.repository;

import br.com.project_sena.adapter.out.repository.entity.UsuarioEntity;
import br.com.project_sena.adapter.out.repository.mapper.UsuarioMapperEntity;
import br.com.project_sena.adapter.out.repository.persistence.UsuarioJpaRepository;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.port.out.UsuarioRepository;
import br.com.project_sena.exception.type.UsuarioNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioMapperEntity mapperEntity;

    public UsuarioRepositoryImpl(UsuarioJpaRepository jpaRepository, UsuarioMapperEntity mapperEntity) {
        this.jpaRepository = jpaRepository;
        this.mapperEntity = mapperEntity;
    }


    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entrada = mapperEntity.toEntity(usuario);
        UsuarioEntity saved = jpaRepository.save(entrada);
        return mapperEntity.toDomain(saved);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id).map(mapperEntity::toDomain);
    }

    @Override
    public Page<Usuario> findAllByUsuarioEnum(UsuarioEnum status, Pageable pageable) {
        return jpaRepository.findAllByUsuarioEnum(status, pageable)
                .map(mapperEntity::toDomain);
    }

    @Override
    public void delete(Long id) {
        UsuarioEntity entity = jpaRepository.findById(id).orElseThrow(() -> new RuntimeException("ID do usuario nao encontrado: " + id));
        entity.setUsuarioEnum(UsuarioEnum.INVATIVO);
        jpaRepository.save(entity);
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return null;
    }

    @Override
    public void reativar(Long id) {
        UsuarioEntity entity = jpaRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException("ID do usuario nao encontrado: " + id));
        entity.setUsuarioEnum(UsuarioEnum.ATIVO);
        jpaRepository.save(entity);
    }
}
