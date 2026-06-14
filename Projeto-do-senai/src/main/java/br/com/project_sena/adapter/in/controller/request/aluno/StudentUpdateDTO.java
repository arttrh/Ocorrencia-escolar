package br.com.project_sena.adapter.in.controller.request.aluno;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StudentUpdateDTO(
        @NotBlank
        String photo,
        @NotBlank
        String name,
        @NotNull
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dateBirth
) {
}
