package br.com.project_sena.application.core.usecase.validacoes;
import br.com.project_sena.application.core.domain.enums.*;
import br.com.project_sena.application.core.domain.model.*;
import br.com.project_sena.application.port.out.TurmaRepository;
import org.springframework.stereotype.Component;
@Component
public class ValidarOcorrencia {
    private final TurmaRepository turmas;
    public ValidarOcorrencia(TurmaRepository turmas) {
        this.turmas = turmas;
    }
    public void validar(Aluno aluno, Turma turma) {
        if (aluno.getAlunoEnum() != AlunoEnum.ATIVO || turma.getTurmaEnum() != TurmaEnum.ATIVA)
            throw new IllegalArgumentException("Aluno e turma precisam estar ativos");
        if (!turmas.alunoPertenceATurma(aluno.getId(), turma.getId()))
            throw new IllegalArgumentException("Aluno não pertence à turma informada");
    }
}
