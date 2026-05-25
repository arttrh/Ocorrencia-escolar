package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;

public record UserListDTO(
        Long id,
        String name,
        PerfilEnum perfil,
        UsuarioEnum usuarioEnum
) {
}
