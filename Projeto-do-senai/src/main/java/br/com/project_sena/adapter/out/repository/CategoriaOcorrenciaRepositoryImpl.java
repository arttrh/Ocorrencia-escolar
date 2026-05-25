package br.com.project_sena.adapter.out.repository;

import br.com.project_sena.adapter.out.repository.mapper.CategoriaOcorrenciaMapperEntity;
import br.com.project_sena.adapter.out.repository.persistence.CategoriaOcorrenciaJpaRepository;
import br.com.project_sena.application.port.out.CategoriaOcorrenciaRepository;

public class CategoriaOcorrenciaRepositoryImpl implements CategoriaOcorrenciaRepository {

    private final CategoriaOcorrenciaJpaRepository categoriaOcorrenciaJpaRepository;
    private final CategoriaOcorrenciaMapperEntity mapperEntity;

    public CategoriaOcorrenciaRepositoryImpl(CategoriaOcorrenciaJpaRepository categoriaOcorrenciaJpaRepository, CategoriaOcorrenciaMapperEntity mapperEntity) {
        this.categoriaOcorrenciaJpaRepository = categoriaOcorrenciaJpaRepository;
        this.mapperEntity = mapperEntity;
    }

}
