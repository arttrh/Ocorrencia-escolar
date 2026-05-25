package br.com.project_sena.adapter.out.repository;

import br.com.project_sena.adapter.out.repository.mapper.OcorrenciaMapperEntity;
import br.com.project_sena.adapter.out.repository.persistence.OcorrenciaJpaRepository;
import br.com.project_sena.application.port.out.OcorrenciaRepository;

public class OcorrenciaRepositoryImpl implements OcorrenciaRepository {

    private final OcorrenciaJpaRepository ocorrenciaJpaRepository;
    private final OcorrenciaMapperEntity mapperEntity;

    public OcorrenciaRepositoryImpl(OcorrenciaJpaRepository ocorrenciaJpaRepository, OcorrenciaMapperEntity mapperEntity) {
        this.ocorrenciaJpaRepository = ocorrenciaJpaRepository;
        this.mapperEntity = mapperEntity;
    }
}
