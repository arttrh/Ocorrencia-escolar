package br.com.project_sena.adapter.in.controller.request;

import br.com.project_sena.application.core.domain.model.Turma;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record StudentRequestDTO(
        @NotBlank
        String photo,
        @NotBlank
        String name,
        @NotNull
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dateBirth
) {
}
