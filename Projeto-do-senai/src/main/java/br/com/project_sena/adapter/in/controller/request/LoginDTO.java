package br.com.project_sena.adapter.in.controller.request;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank
        String login,
        @NotBlank
        String password
) {
}
