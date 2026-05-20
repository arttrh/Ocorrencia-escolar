package br.com.project_sena.adapter.in.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StudentRequestDTO(
        @NotBlank
        String photo,
        @NotBlank
        String name,
        @NotNull
        LocalDate dateBirth
) {
}
