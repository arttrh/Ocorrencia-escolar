package br.com.project_sena.adapter.out.repository;

import br.com.project_sena.adapter.out.repository.mapper.OcorrenciaMapperEntity;
import br.com.project_sena.adapter.out.repository.persistence.OcorrenciaJpaRepository;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.port.out.OcorrenciaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OcorrenciaRepositoryImpl implements OcorrenciaRepository {

    private final OcorrenciaJpaRepository ocorrenciaJpaRepository;
    private final OcorrenciaMapperEntity mapperEntity;

    public OcorrenciaRepositoryImpl(OcorrenciaJpaRepository ocorrenciaJpaRepository, OcorrenciaMapperEntity mapperEntity) {
        this.ocorrenciaJpaRepository = ocorrenciaJpaRepository;
        this.mapperEntity = mapperEntity;
    }

    @Override
    public Ocorrencia save(Ocorrencia dados) {
        return mapperEntity.toDomain(ocorrenciaJpaRepository.save(mapperEntity.toEntity(dados)));
    }

    @Override
    public Optional<Ocorrencia> findById(Long id) {
        return ocorrenciaJpaRepository.findById(id).map(mapperEntity::toDomain);
    }

    @Override
    public List<Ocorrencia> findAll() {
        return ocorrenciaJpaRepository.findAll()
                .stream()
                .map(mapperEntity::toDomain)
                .toList();
    }
}
