package br.com.project_sena.adapter.in.controller.request;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;

public record UserRegisterDTO(
        String name,
        String password,
        String login,
        PerfilEnum perfi,
        UsuarioEnum usuarioEnum
) {
}
