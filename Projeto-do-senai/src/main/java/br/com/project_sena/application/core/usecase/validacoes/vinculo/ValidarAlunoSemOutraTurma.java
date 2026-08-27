package br.com.project_sena.application.core.usecase.validacoes.vinculo;

import br.com.project_sena.application.core.domain.exception.AlunoJaVinculadoException;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.port.out.VinculoRepository;

/**
 * Um aluno pertence a no maximo uma turma.
 *
 * <p>A versao anterior carregava {@code findAll()} de turmas e varria a lista em memoria;
 * agora e' uma consulta unica ao indice de vinculos.</p>
 */
public class ValidarAlunoSemOutraTurma implements ValidadorVinculo {

    private final VinculoRepository vinculoRepository;

    public ValidarAlunoSemOutraTurma(VinculoRepository vinculoRepository) {
        this.vinculoRepository = vinculoRepository;
    }

    @Override
    public void validar(Aluno aluno, Turma turma) {
        vinculoRepository.turmaDoAluno(aluno.getId()).ifPresent(turmaAtualId -> {
            if (turmaAtualId.equals(turma.getId())) {
                throw new AlunoJaVinculadoException("Aluno ja esta matriculado nesta turma");
            }
            throw new AlunoJaVinculadoException(
                    "Aluno ja esta matriculado na turma de id " + turmaAtualId);
        });
    }
}
