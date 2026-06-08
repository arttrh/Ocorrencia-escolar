package br.com.project_sena.application.core.usecase.validacoes;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.exception.type.Aluno.AlunoInativoException;
import br.com.project_sena.exception.type.Aluno.AlunoNotFoundException;
import br.com.project_sena.exception.type.Turma.TurmaCanceladaException;
import br.com.project_sena.exception.type.Turma.TurmaNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ValidarVinculo {

    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;

    public ValidarVinculo(AlunoRepository alunoRepository, TurmaRepository turmaRepository){
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
    }

    public void vincularAlunoATurma(Aluno aluno, Turma turma){
        Aluno encontrarAluno = alunoRepository.findById(aluno.getId()).orElseThrow(() -> new AlunoNotFoundException("Aluno nao existe"));
        log.warn("Id Aluno: {}", aluno.getId());
        Turma encontrarTurma = turmaRepository.findById(turma.getId()).orElseThrow(() -> new TurmaNotFoundException("Turma nao existe"));
        log.warn("Id Turma: {}", turma.getId());
        if (turma.getTurmaEnum() == TurmaEnum.CANCELADA){
            throw new TurmaCanceladaException("Turma cancelada nao pode vincular aluno a esta turma");
        }
        //Predicate<Aluno> alunoAtivo = al -> aluno.getAlunoEnum().equals(AlunoEnum.ATIVO); // Validacoes em uma linha
        if (turma.getAluno().contains(aluno)){
            log.warn("ALUNO Ja cadastrado: {}", turma.getAluno());
            throw new RuntimeException("Aluno ja cadastrado em uma turma");
        }
        Page<Turma> todasAsTurmas = turmaRepository.findAll((Pageable) turma);
        for (Turma turmarDiferentes : todasAsTurmas){
            if (turmarDiferentes.getAluno().contains(aluno)){
                throw new RuntimeException("Aluno nao pode ser adicionado em turmaDiferente");
            }
        }
        if (aluno.getAlunoEnum() == AlunoEnum.INVATIVO){
            throw new AlunoInativoException("Aluno esta inativo nao pode ser vinculado a turma");
        }
        if (aluno.getAlunoEnum().equals(AlunoEnum.ATIVO)){
            if (turma.getTurmaEnum().equals(TurmaEnum.ATIVA))
            alunoRepository.save(aluno);
        }
    }
}
