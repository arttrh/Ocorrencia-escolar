package br.com.project_sena.adapter.out.ratelimit;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Limites de tentativa de login.
 *
 * <p>Antes os numeros estavam escritos direto no codigo em dois lugares diferentes
 * ({@code RateLimitConfig} e {@code RateLimitService}), com valores que precisavam ser
 * mantidos em sincronia na mao.</p>
 *
 * @param tentativas       tentativas permitidas dentro da janela
 * @param janela           periodo de reposicao das tentativas
 * @param maximoDeChaves   teto de chaves guardadas em memoria (protege contra estouro
 *                         de heap quando um atacante varia o IP a cada requisicao)
 * @param habilitado       permite desligar o limite em ambientes de teste
 */
@ConfigurationProperties(prefix = "app.rate-limit.login")
public record RateLimitProperties(Integer tentativas,
                                  Duration janela,
                                  Integer maximoDeChaves,
                                  Boolean habilitado) {

    public RateLimitProperties {
        tentativas = tentativas == null || tentativas <= 0 ? 5 : tentativas;
        janela = janela == null ? Duration.ofMinutes(1) : janela;
        maximoDeChaves = maximoDeChaves == null || maximoDeChaves <= 0 ? 10_000 : maximoDeChaves;
        habilitado = habilitado == null || habilitado;
    }
}
