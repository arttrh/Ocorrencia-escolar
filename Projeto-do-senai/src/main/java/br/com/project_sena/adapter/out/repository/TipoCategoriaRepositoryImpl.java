package br.com.project_sena.adapter.out.repository;

import br.com.project_sena.adapter.out.repository.mapper.TipoOcorrenciaMapperEntity;
import br.com.project_sena.adapter.out.repository.persistence.TipoCategoriaJpaRepository;
import br.com.project_sena.application.port.out.TipoCategoriaRepository;
import org.springframework.stereotype.Component;

@Component
public class TipoCategoriaRepositoryImpl implements TipoCategoriaRepository {

    private final TipoCategoriaJpaRepository turmaJpaRepository;
    private final TipoOcorrenciaMapperEntity mapperEntity;

    public TipoCategoriaRepositoryImpl(TipoCategoriaJpaRepository turmaJpaRepository, TipoOcorrenciaMapperEntity mapperEntity) {
        this.turmaJpaRepository = turmaJpaRepository;
        this.mapperEntity = mapperEntity;
    }


}
