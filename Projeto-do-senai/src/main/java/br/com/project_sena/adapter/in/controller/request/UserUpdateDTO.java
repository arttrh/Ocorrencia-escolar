package br.com.project_sena.adapter.in.controller.request;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;

public record UserUpdateDTO(
        String password,
        PerfilEnum perfil
) {
}
