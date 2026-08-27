package br.com.project_sena.adapter.in.web.dto.response;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;

/**
 * Resposta do login.
 *
 * <p>O campo se chama {@code tokenJWT} porque e' o que o {@code login.js} le. O antigo
 * {@code TokenDTO} devolvia {@code token}, entao mesmo um login correto era tratado como
 * falha pelo front. {@code id} e {@code login} tambem sao lidos pelo front, que os guarda
 * no {@code localStorage} para a tela de troca de senha.</p>
 */
public record LoginResponse(String tokenJWT, Long id, String login, PerfilEnum role) {
}
