package br.com.project_sena.adapter.out.persistence;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.jpa.AlunoJpaRepository;
import br.com.project_sena.adapter.out.persistence.mapper.AlunoMapperEntity;
import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.port.out.AlunoRepository;

@Component
public class AlunoRepositoryAdapter implements AlunoRepository {

    private static final Map<String, String> ORDENACAO = Map.of(
            "id", "id",
            "name", "name",
            "birthDate", "birthDate",
            "status", "status");

    private final AlunoJpaRepository jpaRepository;
    private final AlunoMapperEntity mapper;

    public AlunoRepositoryAdapter(AlunoJpaRepository jpaRepository, AlunoMapperEntity mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Aluno save(Aluno aluno) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(aluno)));
    }

    @Override
    public Optional<Aluno> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Pagina<Aluno> findByStatus(AlunoEnum status, PaginaRequest paginaRequest) {
        return PaginacaoSpring.paraPagina(
                jpaRepository.findAllByStatus(
                        status, PaginacaoSpring.paraPageable(paginaRequest, ORDENACAO)),
                mapper::toDomain);
    }
}
