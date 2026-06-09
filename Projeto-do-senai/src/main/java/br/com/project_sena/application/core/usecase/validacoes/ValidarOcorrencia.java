package br.com.project_sena.application.core.usecase.validacoes;

import br.com.project_sena.application.core.domain.enums.CategoriaOcorrencia;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.CategoriaOcorrenciaRepository;
import br.com.project_sena.application.port.out.OcorrenciaRepository;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.exception.type.Aluno.AlunoNotFoundException;
import br.com.project_sena.exception.type.Ocorrencia.OcorrenciaNotFoundException;
import br.com.project_sena.exception.type.Turma.TurmaNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ValidarOcorrencia {
    private final CategoriaOcorrencia categoriaOcorrencia;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final OcorrenciaRepository ocorrenciaRepository;

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

    public void historicoOcorrencia(Long idOcorrencia, Long alunoId, Long turmaId){
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new AlunoNotFoundException("Aluno não encontrado"));

        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new TurmaNotFoundException("Turma não encontrada"));
        Ocorrencia ocorrencia = ocorrenciaRepository.findById(idOcorrencia).orElseThrow(
                () -> new OcorrenciaNotFoundException("Ocorrencia nao existe"));

        List<Ocorrencia> HistoricoOcorrencia = ocorrenciaRepository.findAll();
        for (Ocorrencia historico : HistoricoOcorrencia){
            historico.getTime();
        }
    }
}
