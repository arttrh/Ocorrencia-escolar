package br.com.project_sena.adapter.out.repository;

import br.com.project_sena.adapter.out.repository.mapper.AlunoMapperEntity;
import br.com.project_sena.adapter.out.repository.persistence.AlunoJpaRepository;
import br.com.project_sena.application.port.out.AlunoRepository;
import org.springframework.stereotype.Component;

@Component
public class AlunoRepositoryImpl implements AlunoRepository {

    private final AlunoJpaRepository alunoJpaRepository;
    private final AlunoMapperEntity mapperEntity;

    public AlunoRepositoryImpl(AlunoJpaRepository alunoJpaRepository, AlunoMapperEntity mapperEntity) {
        this.alunoJpaRepository = alunoJpaRepository;
        this.mapperEntity = mapperEntity;
    }
}
