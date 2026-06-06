package br.com.project_sena.adapter.out.repository;

import br.com.project_sena.adapter.out.repository.mapper.TurmaMapperEntity;
import br.com.project_sena.adapter.out.repository.persistence.TurmaJpaRepository;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.port.out.TurmaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TurmaRepositoryImpl implements TurmaRepository {

    private final TurmaJpaRepository turmaJpaRepository;
    private final TurmaMapperEntity mapperEntity;

    public TurmaRepositoryImpl(TurmaJpaRepository turmaJpaRepository, TurmaMapperEntity mapperEntity) {
        this.turmaJpaRepository = turmaJpaRepository;
        this.mapperEntity = mapperEntity;
    }


    @Override
    public Turma save(Turma dados) {
        return mapperEntity.toDomain(turmaJpaRepository.save(mapperEntity.toEntity(dados)));
    }

    @Override
    public Page<Turma> findByTurmaEnum(Pageable paginacao, TurmaEnum status) {
        return turmaJpaRepository.findByTurmaEnum(paginacao, status)
                .map(mapperEntity::toDomain);
    }

    @Override
    public Optional<Turma> findById(Long id) {
        return turmaJpaRepository.findById(id).map(mapperEntity::toDomain);
    }
}
