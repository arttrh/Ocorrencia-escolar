package br.com.project_sena.application.port.out;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AlunoRepository {
    Aluno save(Aluno dados);

    Optional<Aluno> findById(Long id);

    Page<Aluno> findByAlunoEnum(Pageable paginacao, AlunoEnum status);
}
