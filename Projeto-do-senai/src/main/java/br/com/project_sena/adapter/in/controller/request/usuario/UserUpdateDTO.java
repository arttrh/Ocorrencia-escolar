package br.com.project_sena.adapter.in.controller.request.usuario;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String password,
        @NotNull
        PerfilEnum perfil
) {
}
