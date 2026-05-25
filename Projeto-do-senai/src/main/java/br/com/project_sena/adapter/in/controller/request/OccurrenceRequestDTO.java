package br.com.project_sena.adapter.in.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record OccurrenceRequestDTO(
        @NotNull
        Long studentId,
        @NotNull
        Long classId,
        @NotNull
        Long categoryId,
        @NotNull
        Long occurenceId,
        @NotNull
        LocalDate date,
        @NotNull
        LocalTime time,
        @NotBlank
        String descriptionOccurrence
) {
}
