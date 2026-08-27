package br.com.project_sena.application.port.out;

import java.util.Optional;

import br.com.project_sena.application.core.domain.model.Usuario;

/**
 * Emissao e verificacao de tokens de acesso.
 *
 * <p>Porta de saida: o nucleo pede "gere um token para este usuario" sem saber que
 * por tras existe JWT/HMAC. Trocar JWT por outra tecnologia troca so' o adaptador.</p>
 */
public interface TokenPort {

    String gerarToken(Usuario usuario);

    /** @return o login (subject) do token, ou vazio se ele for invalido/expirado */
    Optional<String> extrairLogin(String token);
}
