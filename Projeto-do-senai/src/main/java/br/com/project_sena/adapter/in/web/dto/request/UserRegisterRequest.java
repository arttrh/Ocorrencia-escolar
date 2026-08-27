package br.com.project_sena.adapter.in.web.dto.request;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @NotBlank(message = "Nome e' obrigatorio")
        @Size(max = 100, message = "Nome deve ter no maximo 100 caracteres")
        String name,

        @NotBlank(message = "Login e' obrigatorio")
        @Email(message = "Login deve ser um e-mail valido")
        @Size(max = 255, message = "Login deve ter no maximo 255 caracteres")
        String login,

        @NotBlank(message = "Senha e' obrigatoria")
        @Size(min = 6, max = 72, message = "Senha deve ter entre 6 e 72 caracteres")
        String password,

        @NotNull(message = "Perfil e' obrigatorio")
        PerfilEnum role) {
}
