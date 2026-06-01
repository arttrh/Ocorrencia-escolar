package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;

public record UserListInativosDTO(
        Long id,
        String name,
        String email,
        UsuarioEnum usuario,
        PerfilEnum perfil
) {
}
