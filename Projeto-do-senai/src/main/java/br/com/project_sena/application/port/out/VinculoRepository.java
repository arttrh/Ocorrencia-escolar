package br.com.project_sena.application.port.out;

import java.util.List;
import java.util.Optional;

import br.com.project_sena.application.core.domain.model.Aluno;

/**
 * Vinculo aluno &lt;-&gt; turma (tabela {@code vinculo}).
 *
 * <p>Porta separada do {@link TurmaRepository} porque a contagem de matriculas e' o que
 * alimenta a regra de turma cheia, e carregar a turma inteira com todos os alunos so'
 * para contar seria caro.</p>
 */
public interface VinculoRepository {

    void vincular(Long alunoId, Long turmaId);

    long contarAlunosDaTurma(Long turmaId);

    List<Aluno> listarAlunosDaTurma(Long turmaId);

    boolean alunoPertenceATurma(Long alunoId, Long turmaId);

    /** @return o id da turma em que o aluno ja esta matriculado, se houver */
    Optional<Long> turmaDoAluno(Long alunoId);
}
