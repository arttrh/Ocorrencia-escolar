package br.com.project_sena.adapter.in.controller.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record StudentUpdateDTO(
        @NotBlank
        String photo,
        @NotBlank
        String name,
        LocalDate dateBirth
) {
}
