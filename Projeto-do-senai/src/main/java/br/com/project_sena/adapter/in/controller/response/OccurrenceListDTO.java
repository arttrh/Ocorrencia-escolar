package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.model.Ocorrencia;

import java.time.LocalDate;

public record OccurrenceListDTO(
        Long id,
        String studentName,
        String className,
        String categoryName,
        String occurrenceTypeName,
        LocalDate date
) {
    public OccurrenceListDTO(Ocorrencia ocorrencia){
        this(
                ocorrencia.getId(),
                ocorrencia.getStudent().getName(),
                ocorrencia.getTurma().getClass_name(),
                ocorrencia.getCategory().getNameCategory(),
                ocorrencia.getOccurrenceType().getNameOccurrence(),
                ocorrencia.getDataOcorrencia()
        );
    }
}
