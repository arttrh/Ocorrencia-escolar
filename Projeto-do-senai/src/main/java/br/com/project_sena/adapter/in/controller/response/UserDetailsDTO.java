package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;

public record UserDetailsDTO(
        Long userId,
        String name,
        String login,
        PerfilEnum perfil
){
}
