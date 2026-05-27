package br.com.project_sena.adapter.in.controller.request;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(
        @NotBlank
        String password,
        @NotNull
        PerfilEnum perfil
) {
}
