package br.com.project_sena.adapter.out.repository;
import br.com.project_sena.adapter.out.repository.entity.AlunoTurmaEntity;
import br.com.project_sena.adapter.out.repository.mapper.TurmaMapperEntity;
import br.com.project_sena.adapter.out.repository.persistence.AlunoJpaRepository;
import br.com.project_sena.adapter.out.repository.persistence.AlunoTurmaJpaRepository;
import br.com.project_sena.adapter.out.repository.persistence.TurmaJpaRepository;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.port.out.TurmaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class TurmaRepositoryImpl implements TurmaRepository {
    private final TurmaJpaRepository turmaJpaRepository;
    private final TurmaMapperEntity mapperEntity;
    private final AlunoTurmaJpaRepository vinculos;
    private final AlunoJpaRepository alunos;

    public TurmaRepositoryImpl(TurmaJpaRepository turmaJpaRepository, TurmaMapperEntity mapperEntity,
        AlunoTurmaJpaRepository vinculos, AlunoJpaRepository alunos) {
        this.turmaJpaRepository = turmaJpaRepository;
        this.mapperEntity = mapperEntity;
        this.vinculos = vinculos;
        this.alunos = alunos;
    }

    @Override
    public void vincularAluno(Long turmaId, Long alunoId) {
        vinculos.save(new AlunoTurmaEntity(
            null, alunos.getReferenceById(alunoId), turmaJpaRepository.getReferenceById(turmaId)));
    }
    @Override
    public boolean alunoTemTurma(Long id) {
        return vinculos.existsByAlunoId(id);
    }
    @Override
    public boolean alunoPertenceATurma(Long alunoId, Long turmaId) {
        return vinculos.existsByAlunoIdAndTurmaId(alunoId, turmaId);
    }
    @Override
    public long contarAlunos(Long id) {
        return vinculos.countByTurmaId(id);
    }

    @Override
    public Turma save(Turma dados) {
        return mapperEntity.toDomain(turmaJpaRepository.save(mapperEntity.toEntity(dados)));
    }

    @Override
    public Page<Turma> findByTurmaEnum(Pageable paginacao, TurmaEnum status) {
        return turmaJpaRepository.findByTurmaEnum(paginacao, status).map(mapperEntity::toDomain);
    }

    @Override
    public Optional<Turma> findById(Long id) {
        return turmaJpaRepository.findById(id).map(mapperEntity::toDomain);
    }

    @Override
    public List<Turma> findAll() {
        return turmaJpaRepository.findAll().stream().map(mapperEntity::toDomain).toList();
    }
}
