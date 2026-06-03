package br.com.project_sena.adapter.in.controller.request;

import br.com.project_sena.adapter.out.repository.entity.AlunoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClassRegisterDTO(
        @NotBlank
        String className,
        @NotBlank
        String shift,
        @NotNull
        Integer classYear
) {
}
