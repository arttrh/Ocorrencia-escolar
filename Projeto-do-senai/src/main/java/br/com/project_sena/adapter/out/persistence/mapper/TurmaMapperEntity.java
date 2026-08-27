package br.com.project_sena.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.entity.TurmaEntity;
import br.com.project_sena.application.core.domain.model.Turma;

@Component
public class TurmaMapperEntity {

    public Turma toDomain(TurmaEntity entity) {
        return new Turma(
                entity.getId(),
                entity.getName(),
                entity.getShift(),
                entity.getYear(),
                entity.getSemester(),
                entity.getStatus());
    }

    public TurmaEntity toEntity(Turma turma) {
        return new TurmaEntity(
                turma.getId(),
                turma.getName(),
                turma.getShift(),
                turma.getYear(),
                turma.getSemester(),
                turma.getStatus());
    }
}
