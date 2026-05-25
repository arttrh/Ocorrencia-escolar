package br.com.project_sena.adapter.in.controller.response;

import java.time.LocalDate;

public record OccurrenceListDTO(
        Long id,
        String studentName,
        String className,
        String categoryName,
        String occurrenceTypeName,
        LocalDate date
) {
}
