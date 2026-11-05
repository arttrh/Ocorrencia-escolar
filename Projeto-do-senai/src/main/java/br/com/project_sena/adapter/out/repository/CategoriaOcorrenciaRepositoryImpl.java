package br.com.project_sena.adapter.out.repository;
import br.com.project_sena.adapter.out.repository.mapper.CategoriaOcorrenciaMapperEntity;
import br.com.project_sena.adapter.out.repository.persistence.*;
import br.com.project_sena.application.core.domain.model.*;
import br.com.project_sena.application.port.out.CategoriaOcorrenciaRepository;
import java.util.*;
import org.springframework.stereotype.Component;
@Component
public class CategoriaOcorrenciaRepositoryImpl implements CategoriaOcorrenciaRepository {
    private final CategoriaOcorrenciaJpaRepository categorias;
    private final TipoCategoriaJpaRepository tipos;
    private final CategoriaOcorrenciaMapperEntity mapper;
    public CategoriaOcorrenciaRepositoryImpl(CategoriaOcorrenciaJpaRepository categorias,
        TipoCategoriaJpaRepository tipos, CategoriaOcorrenciaMapperEntity mapper) {
        this.categorias = categorias;
        this.tipos = tipos;
        this.mapper = mapper;
    }
    public CategoriaOcorrencia save(CategoriaOcorrencia dados) {
        return mapper.toDomain(categorias.save(mapper.toEntity(dados)));
    }
    public List<CategoriaOcorrencia> listar() {
        return categorias.findAll().stream().map(mapper::toDomain).toList();
    }
    public Optional<CategoriaOcorrencia> buscar(Long id) {
        return categorias.findById(id).map(mapper::toDomain);
    }
    public List<TipoOcorrencia> tipos(Long id) {
        return tipos.findByCategoriasId(id)
            .stream()
            .map(t -> new TipoOcorrencia(t.getId(), t.getNameOccurrence()))
            .toList();
    }
    public Optional<TipoOcorrencia> buscarTipo(Long id, Long categoriaId) {
        return tipos.findById(id)
            .filter(t -> t.getCategorias().getId().equals(categoriaId))
            .map(t -> new TipoOcorrencia(t.getId(), t.getNameOccurrence()));
    }
}
