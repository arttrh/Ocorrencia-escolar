package br.com.project_sena.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.entity.AlunoEntity;
import br.com.project_sena.application.core.domain.model.Aluno;

@Component
public class AlunoMapperEntity {

    public Aluno toDomain(AlunoEntity entity) {
        return new Aluno(
                entity.getId(),
                entity.getName(),
                entity.getBirthDate(),
                entity.getImageUrl(),
                entity.getStatus());
    }

    public AlunoEntity toEntity(Aluno aluno) {
        return new AlunoEntity(
                aluno.getId(),
                aluno.getName(),
                aluno.getBirthDate(),
                aluno.getImageUrl(),
                aluno.getStatus());
    }
}
