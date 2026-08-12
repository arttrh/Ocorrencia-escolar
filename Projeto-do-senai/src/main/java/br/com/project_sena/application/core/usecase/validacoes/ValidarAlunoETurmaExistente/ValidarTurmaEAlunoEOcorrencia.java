package br.com.project_sena.application.core.usecase.validacoes.ValidarAlunoETurmaExistente;
import org.springframework.stereotype.Component;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.OcorrenciaRepository;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.exception.type.Aluno.AlunoNotFoundException;
import br.com.project_sena.exception.type.Ocorrencia.OcorrenciaNotFoundException;
import br.com.project_sena.exception.type.Turma.TurmaNotFoundException;

@Component
public class ValidarTurmaEAlunoEOcorrencia{
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final OcorrenciaRepository ocorrenciaRepository;

    public ValidarTurmaEAlunoEOcorrencia(AlunoRepository alunoRepository, TurmaRepository turmaRepository, OcorrenciaRepository ocorrenciaRepository){
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
        this.ocorrenciaRepository = ocorrenciaRepository;
    }

  
    public void validacoes(Aluno aluno, Turma turma, Ocorrencia ocorrencia) {
        alunoRepository.findById(aluno.getId())
                .orElseThrow(() -> new AlunoNotFoundException("Aluno nao existe"));
        turmaRepository.findById(turma.getId())
                .orElseThrow(() -> new TurmaNotFoundException("Aluno nao existe"));
        ocorrenciaRepository.findById(ocorrencia.getId()).orElseThrow(() -> new OcorrenciaNotFoundException("Ocorrencia não existe"));
    }
}
