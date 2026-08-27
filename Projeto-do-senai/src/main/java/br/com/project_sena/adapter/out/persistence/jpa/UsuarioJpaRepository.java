package br.com.project_sena.adapter.out.persistence.jpa;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.project_sena.adapter.out.persistence.entity.UsuarioEntity;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByLoginAndIdNot(String login, Long id);

    Page<UsuarioEntity> findAllByStatus(UsuarioEnum status, Pageable pageable);
}
