package br.com.project_sena.application.port.out;

import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.model.Turma;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TurmaRepository {
    void vincularAluno(Long turmaId, Long alunoId);
    boolean alunoTemTurma(Long alunoId);
    boolean alunoPertenceATurma(Long alunoId, Long turmaId);
    long contarAlunos(Long turmaId);
    Turma save(Turma dados);

    Page<Turma> findByTurmaEnum(Pageable paginacao, TurmaEnum status);

    Optional<Turma> findById(Long id);

    List<Turma> findAll();
}