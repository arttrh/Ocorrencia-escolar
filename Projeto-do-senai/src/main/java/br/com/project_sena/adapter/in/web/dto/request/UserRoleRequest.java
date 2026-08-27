package br.com.project_sena.adapter.in.web.dto.request;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import jakarta.validation.constraints.NotNull;

public record UserRoleRequest(
        @NotNull(message = "Id e' obrigatorio")
        Long id,

        @NotNull(message = "Perfil e' obrigatorio")
        PerfilEnum role) {
}
