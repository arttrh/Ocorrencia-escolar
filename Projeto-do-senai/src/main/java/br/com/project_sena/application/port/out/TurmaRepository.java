package br.com.project_sena.application.port.out;

import br.com.project_sena.adapter.out.repository.entity.TurmaEntity;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.model.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TurmaRepository {
    Turma save(Turma dados);

    Page<Turma> findByTurmaEnum(Pageable paginacao, TurmaEnum status);

    Optional <Turma> findById(Long id);
}
