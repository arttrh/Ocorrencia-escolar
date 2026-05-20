package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.model.Usuario;

public record UserListDTO(
        Long id,
        String name,
        boolean active,
        PerfilEnum perfil
) {
}
