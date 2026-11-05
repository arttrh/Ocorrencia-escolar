package br.com.project_sena.application.core.usecase.validacoes;
import br.com.project_sena.application.core.domain.enums.*;
import br.com.project_sena.application.core.domain.model.*;
import br.com.project_sena.application.port.out.TurmaRepository;
import org.springframework.stereotype.Component;
@Component
public class ValidarVinculo {
    private final TurmaRepository repository;
    public ValidarVinculo(TurmaRepository repository) {
        this.repository = repository;
    }
    public void validarTurmaEAluno(Aluno aluno, Turma turma) {
        if (aluno.getAlunoEnum() != AlunoEnum.ATIVO || turma.getTurmaEnum() != TurmaEnum.ATIVA)
            throw new IllegalArgumentException("Aluno e turma precisam estar ativos");
        if (repository.alunoTemTurma(aluno.getId()))
            throw new IllegalArgumentException("Aluno já vinculado a uma turma");
        if (repository.contarAlunos(turma.getId()) >= turma.getTurmaCheia())
            throw new IllegalArgumentException("Turma lotada");
    }
}
