package br.com.project_sena.adapter.in.controller.request.email;

import jakarta.validation.constraints.NotBlank;

public record EmailDTO(
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
