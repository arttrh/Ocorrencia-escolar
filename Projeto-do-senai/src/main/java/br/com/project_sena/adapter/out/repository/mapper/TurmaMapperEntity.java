package br.com.project_sena.adapter.out.repository.mapper;

import br.com.project_sena.adapter.out.repository.entity.TurmaEntity;
import br.com.project_sena.application.core.domain.model.Turma;
import org.springframework.stereotype.Component;

@Component
public class TurmaMapperEntity {
    public Turma toDomain(TurmaEntity entity){
        return new Turma(
                entity.getId(),
                entity.getClassName(),
                entity.getShift(),
                entity.getClassYear(),
                entity.getTurmaEnum()
        );
    }

    public TurmaEntity toEntity(Turma turma){
        return new TurmaEntity(
                turma.getId(),
                turma.getClassName(),
                turma.getShift(),
                turma.getClassYear(),
                turma.getTurmaEnum()
        );
    }
}
