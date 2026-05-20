package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.model.Ocorrencia;

import java.time.LocalDate;
import java.time.LocalTime;

public record OccurrenceDetailsDTO(
        Long occurrenceId,
        String studentName,
        String className,
        String categoryName,
        String occurrenceTypeName,
        LocalDate date,
        LocalTime time,
        String descriptionOccurrence
) {
}
