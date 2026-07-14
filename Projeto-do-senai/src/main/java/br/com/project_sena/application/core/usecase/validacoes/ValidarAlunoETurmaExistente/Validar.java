package br.com.project_sena.application.core.usecase.validacoes.ValidarAlunoETurmaExistente;

import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.usecase.validacoes.ValidarVinculos;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.OcorrenciaRepository;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.exception.type.Aluno.AlunoExistingException;
import br.com.project_sena.exception.type.Aluno.AlunoNotFoundException;
import br.com.project_sena.exception.type.Ocorrencia.OcorrenciaNotFoundException;
import br.com.project_sena.exception.type.Turma.TurmaNotFoundException;

public class Validar implements ValidarVinculos {
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final OcorrenciaRepository ocorrenciaRepository;
    private final Ocorrencia id;

    public Validar(AlunoRepository alunoRepository, TurmaRepository turmaRepository, OcorrenciaRepository ocorrenciaRepository, Ocorrencia ocorrencia){
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.id = ocorrencia;
    }
    @Override
    public void validar(Aluno aluno, Turma turma) {
        alunoRepository.findById(aluno.getId())
                .orElseThrow(() -> new AlunoNotFoundException("Aluno nao existe"));
        turmaRepository.findById(turma.getId())
                .orElseThrow(() -> new TurmaNotFoundException("Aluno nao existe"));
        Ocorrencia ocorrencia = ocorrenciaRepository.findById(id.getId())
                .orElseThrow(() -> new OcorrenciaNotFoundException("Ocorrencia nao existe"));
    }
}
