package br.com.project_sena.application.core.usecase.validacoes.vinculo;

import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.port.out.VinculoRepository;

/**
 * Delega a decisao para {@link Turma#garantirVagaDisponivel(long)} — a regra de capacidade
 * mora no dominio; o validador apenas fornece a contagem atual de matriculados.
 */
public class ValidarTurmaComVaga implements ValidadorVinculo {

    private final VinculoRepository vinculoRepository;

    public ValidarTurmaComVaga(VinculoRepository vinculoRepository) {
        this.vinculoRepository = vinculoRepository;
    }

    @Override
    public void validar(Aluno aluno, Turma turma) {
        turma.garantirVagaDisponivel(vinculoRepository.contarAlunosDaTurma(turma.getId()));
    }
}
