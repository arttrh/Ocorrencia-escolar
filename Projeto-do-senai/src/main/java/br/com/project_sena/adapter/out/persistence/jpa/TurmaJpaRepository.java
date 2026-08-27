package br.com.project_sena.adapter.out.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.project_sena.adapter.out.persistence.entity.TurmaEntity;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;

public interface TurmaJpaRepository extends JpaRepository<TurmaEntity, Long> {

    Page<TurmaEntity> findAllByStatus(TurmaEnum status, Pageable pageable);
}
