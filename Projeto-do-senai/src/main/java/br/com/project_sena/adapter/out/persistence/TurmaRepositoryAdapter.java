package br.com.project_sena.adapter.out.persistence;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.jpa.TurmaJpaRepository;
import br.com.project_sena.adapter.out.persistence.mapper.TurmaMapperEntity;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.port.out.TurmaRepository;

@Component
public class TurmaRepositoryAdapter implements TurmaRepository {

    private static final Map<String, String> ORDENACAO = Map.of(
            "id", "id",
            "name", "name",
            "year", "year",
            "shift", "shift",
            "semester", "semester",
            "status", "status");

    private final TurmaJpaRepository jpaRepository;
    private final TurmaMapperEntity mapper;

    public TurmaRepositoryAdapter(TurmaJpaRepository jpaRepository, TurmaMapperEntity mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Turma save(Turma turma) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(turma)));
    }

    @Override
    public Optional<Turma> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Pagina<Turma> findByStatus(TurmaEnum status, PaginaRequest paginaRequest) {
        return PaginacaoSpring.paraPagina(
                jpaRepository.findAllByStatus(
                        status, PaginacaoSpring.paraPageable(paginaRequest, ORDENACAO)),
                mapper::toDomain);
    }
}
