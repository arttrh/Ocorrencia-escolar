package br.com.project_sena.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Credenciais de acesso.
 *
 * <p>O campo se chama {@code login} — e nao {@code email} como no antigo
 * {@code EmailDTO} — porque e' o nome que o formulario de login do front envia. O
 * descasamento fazia o back receber {@code login} nulo e recusar toda tentativa.</p>
 */
public record LoginRequest(
        @NotBlank(message = "Login e' obrigatorio")
        String login,

        @NotBlank(message = "Senha e' obrigatoria")
        String password) {
}
