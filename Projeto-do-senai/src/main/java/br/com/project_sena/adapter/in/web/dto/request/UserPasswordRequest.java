package br.com.project_sena.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserPasswordRequest(
        @NotNull(message = "Id e' obrigatorio")
        Long id,

        @NotBlank(message = "Senha atual e' obrigatoria")
        String oldPassword,

        @NotBlank(message = "Nova senha e' obrigatoria")
        @Size(min = 6, max = 72, message = "Nova senha deve ter entre 6 e 72 caracteres")
        String newPassword) {
}
