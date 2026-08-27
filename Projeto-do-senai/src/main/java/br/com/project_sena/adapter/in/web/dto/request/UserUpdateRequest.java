package br.com.project_sena.adapter.in.web.dto.request;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Atualizacao parcial: apenas o id e' obrigatorio. */
public record UserUpdateRequest(
        @NotNull(message = "Id e' obrigatorio")
        Long id,

        @Size(max = 100, message = "Nome deve ter no maximo 100 caracteres")
        String name,

        @Email(message = "Login deve ser um e-mail valido")
        @Size(max = 255, message = "Login deve ter no maximo 255 caracteres")
        String login,

        PerfilEnum role) {
}
