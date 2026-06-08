package br.com.project_sena.adapter.in.controller.request.turma;

import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ClassRegisterDTO(
        @NotBlank
        String className,
        @NotNull
        TurmaTurnoEnum turmaTurno,
        @NotNull
        LocalDateTime classYear,
        @NotNull
        LocalDateTime semestry
) {
}
