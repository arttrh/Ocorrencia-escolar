package br.com.project_sena.application.core.usecase;

import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.usecase.validacoes.ValidarSemestre;
import br.com.project_sena.application.core.usecase.validacoes.ValidarVinculo;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.exception.type.Turma.TurmaNotFoundException;
import br.com.project_sena.exception.type.Turma.TurmaValidadeException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class TurmaService {

    private final TurmaRepository repository;
    private final ValidarSemestre validarSemestre;
    private final ValidarVinculo validarTurma;

    public TurmaService(TurmaRepository repository, ValidarSemestre validarSemestre, ValidarVinculo validarTurma) {
        this.repository = repository;
        this.validarSemestre = validarSemestre;
        this.validarTurma = validarTurma;
    }

    @Transactional
    public Turma vincularAlunoTurma(Turma dados, Aluno aluno){
        validarSemestre.validarSemestre(dados);
        validarTurma.vincularAlunoATurma(aluno, dados);
        return repository.save(dados);
    }

    @Transactional
    public Turma cadastrar(Turma dados){
        Turma saved = repository.save(dados);
        return saved;
    }

    public Turma buscar(Long id){
        Turma turma = repository.findById(id).orElseThrow(() -> new TurmaNotFoundException("Turma nao existe"));
        return turma;
    }

    public Page<Turma> listarTurmasAtivas(Pageable pageable){
        return repository.findByTurmaEnum(pageable, TurmaEnum.ATIVA);
    }

    public Page <Turma> listarTurmasCanceladas(Pageable pageable){
        return repository.findByTurmaEnum(pageable, TurmaEnum.CANCELADA);
    }

    public Turma atualizarTurma(Turma dados, Long id){
        Turma turma = repository.findById(id).orElseThrow(() -> new TurmaNotFoundException("Turma nao existe"));
        turma.atualizarTurma(
                dados.getClassName(),
                dados.getTurmaTurnoEnum(),
                dados.getClassYear(),
                dados.getTurmaEnum()
        );
        return repository.save(turma);
    }

    public void deletar(Long id){
        Turma turma = repository.findById(id).orElseThrow(() -> new TurmaNotFoundException("Turma nao existe"));
        turma.excluir();
        repository.save(turma);
    }

    public Turma reativar(Long id){
        Turma turma = repository.findById(id).orElseThrow(() -> new TurmaNotFoundException("Turma nao existe"));
        turma.reativar();
       Turma saved = repository.save(turma);
        return saved;
    }
}