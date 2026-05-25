package br.com.project_sena.adapter.in.controller.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRegisterDTO(
        @NotBlank
        String nameCategory
) {
}
