package br.com.project_sena.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.jpa.CategoriaOcorrenciaJpaRepository;
import br.com.project_sena.adapter.out.persistence.mapper.CategoriaOcorrenciaMapperEntity;
import br.com.project_sena.application.core.domain.model.CategoriaOcorrencia;
import br.com.project_sena.application.port.out.CategoriaOcorrenciaRepository;

@Component
public class CategoriaOcorrenciaRepositoryAdapter implements CategoriaOcorrenciaRepository {

    private final CategoriaOcorrenciaJpaRepository jpaRepository;
    private final CategoriaOcorrenciaMapperEntity mapper;

    public CategoriaOcorrenciaRepositoryAdapter(CategoriaOcorrenciaJpaRepository jpaRepository,
                                                CategoriaOcorrenciaMapperEntity mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public CategoriaOcorrencia save(CategoriaOcorrencia categoria) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(categoria)));
    }

    @Override
    public Optional<CategoriaOcorrencia> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<CategoriaOcorrencia> findByName(String name) {
        return jpaRepository.findByNameIgnoreCase(name).map(mapper::toDomain);
    }

    @Override
    public List<CategoriaOcorrencia> findAll() {
        return jpaRepository.findAllByOrderByNameAsc().stream().map(mapper::toDomain).toList();
    }
}
