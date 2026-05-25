package br.com.project_sena.adapter.out.repository;

import br.com.project_sena.adapter.out.repository.mapper.TurmaMapperEntity;
import br.com.project_sena.adapter.out.repository.persistence.TurmaJpaRepository;
import br.com.project_sena.application.port.out.TurmaRepository;

public class TurmaRepositoryImpl implements TurmaRepository {

    private final TurmaJpaRepository turmaJpaRepository;
    private final TurmaMapperEntity mapperEntity;

    public TurmaRepositoryImpl(TurmaJpaRepository turmaJpaRepository, TurmaMapperEntity mapperEntity) {
        this.turmaJpaRepository = turmaJpaRepository;
        this.mapperEntity = mapperEntity;
    }


}
