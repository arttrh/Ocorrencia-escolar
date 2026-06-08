package br.com.project_sena.adapter.out.repository.persistence;

import br.com.project_sena.adapter.out.repository.entity.TurmaEntity;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.model.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TurmaJpaRepository extends JpaRepository<TurmaEntity, Long> {
    Page<TurmaEntity> findByTurmaEnum(Pageable paginacao, TurmaEnum status);

    Turma findAll(Turma turma);
}
