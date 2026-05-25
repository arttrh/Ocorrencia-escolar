package br.com.project_sena.adapter.in.controller.request;

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
