package br.com.project_sena.application.core.usecase;

import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.OcorrenciaRepository;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.exception.type.Aluno.AlunoNotFoundException;
import br.com.project_sena.exception.type.Turma.TurmaNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;


    public OcorrenciaService(OcorrenciaRepository repository, AlunoRepository alunoRepository, TurmaRepository turmaRepository){
        this.repository = repository;
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
    }

    public Ocorrencia cadastraOcorrencia(){

    }

}
