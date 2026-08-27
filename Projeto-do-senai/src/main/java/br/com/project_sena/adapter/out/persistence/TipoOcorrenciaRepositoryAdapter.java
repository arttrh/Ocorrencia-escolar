package br.com.project_sena.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.entity.CategoriaOcorrenciaEntity;
import br.com.project_sena.adapter.out.persistence.entity.TipoOcorrenciaEntity;
import br.com.project_sena.adapter.out.persistence.jpa.CategoriaOcorrenciaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.TipoOcorrenciaJpaRepository;
import br.com.project_sena.adapter.out.persistence.mapper.TipoOcorrenciaMapperEntity;
import br.com.project_sena.application.core.domain.exception.CategoriaNotFoundException;
import br.com.project_sena.application.core.domain.model.TipoOcorrencia;
import br.com.project_sena.application.port.out.TipoOcorrenciaRepository;

@Component
public class TipoOcorrenciaRepositoryAdapter implements TipoOcorrenciaRepository {

    private final TipoOcorrenciaJpaRepository jpaRepository;
    private final CategoriaOcorrenciaJpaRepository categoriaJpaRepository;
    private final TipoOcorrenciaMapperEntity mapper;

    public TipoOcorrenciaRepositoryAdapter(TipoOcorrenciaJpaRepository jpaRepository,
                                           CategoriaOcorrenciaJpaRepository categoriaJpaRepository,
                                           TipoOcorrenciaMapperEntity mapper) {
        this.jpaRepository = jpaRepository;
        this.categoriaJpaRepository = categoriaJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public TipoOcorrencia save(TipoOcorrencia tipo) {
        CategoriaOcorrenciaEntity categoria = categoriaJpaRepository.findById(tipo.getCategoriaId())
                .orElseThrow(() -> new CategoriaNotFoundException(
                        "Categoria nao encontrada: " + tipo.getCategoriaId()));
        TipoOcorrenciaEntity entity =
                new TipoOcorrenciaEntity(tipo.getId(), tipo.getName(), categoria);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<TipoOcorrencia> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<TipoOcorrencia> findByName(String name) {
        return jpaRepository.findByNameIgnoreCase(name).map(mapper::toDomain);
    }

    @Override
    public List<TipoOcorrencia> findByCategoriaId(Long categoriaId) {
        return jpaRepository.findAllByCategoriaIdOrderByNameAsc(categoriaId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<TipoOcorrencia> findAll() {
        return jpaRepository.findAllByOrderByNameAsc().stream().map(mapper::toDomain).toList();
    }
}
