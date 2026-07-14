package br.com.project_sena.application.core.usecase.validacoes;

import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.usecase.validacoes.ValidarAlunoETurmaExistente.Validar;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.OcorrenciaRepository;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.exception.type.Aluno.AlunoNotFoundException;
import br.com.project_sena.exception.type.Turma.TurmaNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ValidarOcorrencia implements ValidarVinculos{
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final OcorrenciaRepository ocorrenciaRepository;
    private final Validar validar;

    public ValidarOcorrencia(AlunoRepository alunoRepository, TurmaRepository turmaRepository, OcorrenciaRepository ocorrenciaRepository, Validar validar){
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.validar = validar;
    }

    public void criarOcorrencia(Ocorrencia dados, Long alunoId, Long turmaId) {

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new AlunoNotFoundException("Aluno não encontrado"));

        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new TurmaNotFoundException("Turma não encontrada"));
        boolean alunoPertenceATurma = turma.getAluno().contains(aluno);
        if (!alunoPertenceATurma) {
            throw new RuntimeException("Aluno não pertence a essa turma.");
        }
        dados.setTurma(turma);
        dados.setStudent(aluno);

        ocorrenciaRepository.save(dados);
    }

    @Override
    public void validar(Aluno aluno, Turma turma) {
        validar.validar(aluno, turma);
        List<Ocorrencia> historicoOcorrencia = ocorrenciaRepository.findAll();
        for (Ocorrencia historico : historicoOcorrencia) {
            LocalDateTime registradoEm = historico.getCreatedAt();
            System.out.println("Ocorrência " + historico.getId() + " registrada em: " + registradoEm);
        }
    }
}
