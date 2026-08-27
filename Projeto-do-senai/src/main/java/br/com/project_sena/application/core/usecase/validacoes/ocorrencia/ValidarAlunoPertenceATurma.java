package br.com.project_sena.application.core.usecase.validacoes.ocorrencia;

import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;
import br.com.project_sena.application.port.out.VinculoRepository;

/**
 * So' e' possivel registrar ocorrencia para um aluno na turma em que ele esta' matriculado.
 *
 * <p>A versao anterior fazia {@code turma.getAluno().contains(aluno)} sobre uma lista que
 * nunca era carregada — o que dava {@code NullPointerException} em vez de erro de negocio.</p>
 */
public class ValidarAlunoPertenceATurma implements ValidadorOcorrencia {

    private final VinculoRepository vinculoRepository;

    public ValidarAlunoPertenceATurma(VinculoRepository vinculoRepository) {
        this.vinculoRepository = vinculoRepository;
    }

    @Override
    public void validar(ContextoOcorrencia contexto) {
        boolean pertence = vinculoRepository.alunoPertenceATurma(
                contexto.aluno().getId(), contexto.turma().getId());
        if (!pertence) {
            throw new RegraDeNegocioException(
                    "Aluno " + contexto.aluno().getName()
                            + " nao esta matriculado na turma " + contexto.turma().getName());
        }
    }
}
