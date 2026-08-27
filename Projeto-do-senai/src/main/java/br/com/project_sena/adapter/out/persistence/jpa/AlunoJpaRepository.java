package br.com.project_sena.adapter.out.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.project_sena.adapter.out.persistence.entity.AlunoEntity;
import br.com.project_sena.application.core.domain.enums.AlunoEnum;

public interface AlunoJpaRepository extends JpaRepository<AlunoEntity, Long> {

    Page<AlunoEntity> findAllByStatus(AlunoEnum status, Pageable pageable);
}
