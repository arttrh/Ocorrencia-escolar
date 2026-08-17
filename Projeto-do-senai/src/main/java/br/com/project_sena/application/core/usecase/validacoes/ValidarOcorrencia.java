package br.com.project_sena.application.core.usecase.validacoes;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.usecase.validacoes.ValidarAlunoETurmaExistente.ValidarTurmaEAlunoEOcorrencia;
import br.com.project_sena.application.port.out.OcorrenciaRepository;

@Component
public class ValidarOcorrencia{ 
    private final OcorrenciaRepository ocorrenciaRepository;
    private final ValidarTurmaEAlunoEOcorrencia validar;

    public ValidarOcorrencia(OcorrenciaRepository ocorrenciaRepository, ValidarTurmaEAlunoEOcorrencia validar){ 
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.validar = validar;
    }
 
    public void criarOcorrencia(Ocorrencia ocorrencia, Turma turma, Aluno aluno) {
       validar.validacoes(aluno, turma, ocorrencia);
        boolean alunoPertenceATurma = turma.getAluno().contains(aluno);
        if (!alunoPertenceATurma) {
            throw new RuntimeException("Aluno não pertence a essa turma.");
        }
        ocorrencia.setTurma(turma);
        ocorrencia.setStudent(aluno);
        ocorrenciaRepository.save(ocorrencia);
    }
    
    public void historicoOcorrencia(Aluno aluno, Turma turma, Ocorrencia ocorrencia) {
        validar.validacoes(aluno, turma, ocorrencia);
        List<Ocorrencia> historicoOcorrencia = ocorrenciaRepository.findAll();
        for (Ocorrencia historico : historicoOcorrencia) {
            LocalDateTime registradoEm = historico.getCreatedAt();
            System.out.println("Ocorrência " + historico.getId() + " registrada em: " + registradoEm);
        }
    }
}
