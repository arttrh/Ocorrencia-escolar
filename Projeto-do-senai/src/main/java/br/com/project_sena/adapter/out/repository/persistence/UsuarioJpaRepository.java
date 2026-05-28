package br.com.project_sena.adapter.out.repository.persistence;

import br.com.project_sena.adapter.out.repository.entity.UsuarioEntity;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
    Page<UsuarioEntity> findAllByUsuarioEnum(UsuarioEnum status, Pageable pageable);

    Optional<UsuarioEntity> findByLogin(String login);
}
