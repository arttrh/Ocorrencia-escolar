package br.com.project_sena.adapter.in.controller.request;
import jakarta.validation.constraints.*;
import java.time.*;
public record OccurrenceRequestDTO(@NotNull @Positive Long studentId, @NotNull @Positive Long classId,
    @NotNull @Positive Long categoryId, @NotNull @Positive Long occurenceId,
    @NotNull @PastOrPresent LocalDate date, @NotNull LocalTime time,
    @NotBlank @Size(max = 100) String descriptionOccurrence) {}
