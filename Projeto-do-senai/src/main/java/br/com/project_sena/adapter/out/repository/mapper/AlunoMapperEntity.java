package br.com.project_sena.adapter.out.repository.mapper;

import br.com.project_sena.adapter.out.repository.entity.AlunoEntity;
import br.com.project_sena.application.core.domain.model.Aluno;
import org.springframework.stereotype.Component;

@Component
public class AlunoMapperEntity {
    public Aluno toDomain(AlunoEntity entity){
        return new Aluno(
                entity.getId(),
                entity.getPhoto(),
                entity.getName(),
                entity.getDateBirth(),
                entity.getAlunoEnum()
        );
    }

    public AlunoEntity toEntity(Aluno aluno){
        return new AlunoEntity(
                aluno.getId(),
                aluno.getPhoto(),
                aluno.getName(),
                aluno.getDateBirth(),
                aluno.getAlunoEnum()
        );
    }
}
