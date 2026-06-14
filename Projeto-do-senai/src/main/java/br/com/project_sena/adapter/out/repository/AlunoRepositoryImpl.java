package br.com.project_sena.adapter.out.repository;

import br.com.project_sena.adapter.out.repository.entity.AlunoEntity;
import br.com.project_sena.adapter.out.repository.mapper.AlunoMapperEntity;
import br.com.project_sena.adapter.out.repository.persistence.AlunoJpaRepository;
import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.port.out.AlunoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AlunoRepositoryImpl implements AlunoRepository {

    private final AlunoJpaRepository alunoJpaRepository;
    private final AlunoMapperEntity mapperEntity;

    public AlunoRepositoryImpl(AlunoJpaRepository alunoJpaRepository, AlunoMapperEntity mapperEntity) {
        this.alunoJpaRepository = alunoJpaRepository;
        this.mapperEntity = mapperEntity;
    }

    @Override
    public Aluno save(Aluno aluno) {
        AlunoEntity entrada = mapperEntity.toEntity(aluno);
        AlunoEntity saved = alunoJpaRepository.save(entrada);
        return mapperEntity.toDomain(saved);
    }

    @Override
    public Optional<Aluno> findById(Long id) {
        return alunoJpaRepository.findById(id).map(mapperEntity::toDomain);
    }

    @Override
    public Page<Aluno> findByAlunoEnum(Pageable paginacao, AlunoEnum status) {
       return alunoJpaRepository.findByAlunoEnum(paginacao, status)
                .map(mapperEntity::toDomain);
    }
}
