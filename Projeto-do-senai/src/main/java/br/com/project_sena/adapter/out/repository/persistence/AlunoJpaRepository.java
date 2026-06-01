package br.com.project_sena.adapter.out.repository.persistence;

import br.com.project_sena.adapter.out.repository.entity.AlunoEntity;
import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoJpaRepository extends JpaRepository<AlunoEntity, Long> {
    Page<AlunoEntity> findByAlunoEnum(Pageable paginacao, AlunoEnum status);
}