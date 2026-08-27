package br.com.project_sena.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.entity.AlunoEntity;
import br.com.project_sena.adapter.out.persistence.entity.TurmaEntity;
import br.com.project_sena.adapter.out.persistence.entity.VinculoEntity;
import br.com.project_sena.adapter.out.persistence.jpa.AlunoJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.TurmaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.VinculoJpaRepository;
import br.com.project_sena.adapter.out.persistence.mapper.AlunoMapperEntity;
import br.com.project_sena.application.core.domain.exception.AlunoNotFoundException;
import br.com.project_sena.application.core.domain.exception.TurmaNotFoundException;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.port.out.VinculoRepository;

@Component
public class VinculoRepositoryAdapter implements VinculoRepository {

    private final VinculoJpaRepository vinculoJpaRepository;
    private final AlunoJpaRepository alunoJpaRepository;
    private final TurmaJpaRepository turmaJpaRepository;
    private final AlunoMapperEntity alunoMapper;

    public VinculoRepositoryAdapter(VinculoJpaRepository vinculoJpaRepository,
                                    AlunoJpaRepository alunoJpaRepository,
                                    TurmaJpaRepository turmaJpaRepository,
                                    AlunoMapperEntity alunoMapper) {
        this.vinculoJpaRepository = vinculoJpaRepository;
        this.alunoJpaRepository = alunoJpaRepository;
        this.turmaJpaRepository = turmaJpaRepository;
        this.alunoMapper = alunoMapper;
    }

    @Override
    public void vincular(Long alunoId, Long turmaId) {
        AlunoEntity aluno = alunoJpaRepository.findById(alunoId)
                .orElseThrow(() -> new AlunoNotFoundException("Aluno nao encontrado: " + alunoId));
        TurmaEntity turma = turmaJpaRepository.findById(turmaId)
                .orElseThrow(() -> new TurmaNotFoundException("Turma nao encontrada: " + turmaId));
        vinculoJpaRepository.save(new VinculoEntity(null, aluno, turma));
    }

    @Override
    public long contarAlunosDaTurma(Long turmaId) {
        return vinculoJpaRepository.countByTurmaId(turmaId);
    }

    @Override
    public List<Aluno> listarAlunosDaTurma(Long turmaId) {
        return vinculoJpaRepository.findAllByTurmaIdOrderByAlunoNameAsc(turmaId).stream()
                .map(VinculoEntity::getAluno)
                .map(alunoMapper::toDomain)
                .toList();
    }

    @Override
    public boolean alunoPertenceATurma(Long alunoId, Long turmaId) {
        return vinculoJpaRepository.existsByAlunoIdAndTurmaId(alunoId, turmaId);
    }

    @Override
    public Optional<Long> turmaDoAluno(Long alunoId) {
        return vinculoJpaRepository.findByAlunoId(alunoId)
                .map(vinculo -> vinculo.getTurma().getId());
    }
}
