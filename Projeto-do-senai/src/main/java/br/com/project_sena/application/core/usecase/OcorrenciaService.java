package br.com.project_sena.application.core.usecase;

import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.usecase.validacoes.ValidarOcorrencia;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.OcorrenciaRepository;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.exception.type.Aluno.AlunoNotFoundException;
import br.com.project_sena.exception.type.Turma.TurmaNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final ValidarOcorrencia validarOcorrencia;


    public OcorrenciaService(OcorrenciaRepository repository, ValidarOcorrencia validarOcorrencia){
        this.repository = repository;
        this.validarOcorrencia = validarOcorrencia;
    }

    public void cadastraOcorrencia(Aluno aluno, Turma turma, Ocorrencia dados){
        validarOcorrencia.criarOcorrencia(dados, turma, aluno);
    }
}
