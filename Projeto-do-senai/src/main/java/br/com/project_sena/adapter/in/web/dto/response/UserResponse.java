package br.com.project_sena.adapter.in.web.dto.response;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;

public record UserResponse(Long id, String name, String login, PerfilEnum role, boolean active) {
}
