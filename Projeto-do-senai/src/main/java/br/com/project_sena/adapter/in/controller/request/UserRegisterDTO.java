package br.com.project_sena.adapter.in.controller.request;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;

public record UserRegisterDTO(
        String name,
        String password,
        String login,
        PerfilEnum perfil
) {
}
