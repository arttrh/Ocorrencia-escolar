package br.com.project_sena.adapter.in.controller.request;

import br.com.project_sena.application.core.domain.enums.Perfil;

public record UserRegisterDTO(
        String name,
        String password,
        String login,
        Perfil perfil
) {
}
