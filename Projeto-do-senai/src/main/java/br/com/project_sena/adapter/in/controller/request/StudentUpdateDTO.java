package br.com.project_sena.adapter.in.controller.request;

import br.com.project_sena.application.core.domain.model.Turma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StudentUpdateDTO(
        @NotBlank
        String photo,
        @NotBlank
        String name,
        @NotNull
        LocalDate dateBirth,
        Turma classId
) {
}
