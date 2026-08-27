package br.com.project_sena.adapter.out.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.project_sena.application.core.domain.exception.TokenInvalidoException;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.port.out.TokenPort;

/**
 * Emissao e verificacao de JWT (HMAC-256).
 *
 * <p>Correcoes em relacao ao {@code TokenService} anterior:</p>
 * <ul>
 *   <li>{@code validarToken} devolvia a <em>string</em> {@code "Token invalido"} quando a
 *       verificacao falhava; o filtro entao procurava um usuario com esse login. Agora o
 *       retorno e' {@link Optional} e um token invalido simplesmente nao autentica;</li>
 *   <li>a expiracao usava o fuso fixo {@code -03:00}; passou a ser calculada em UTC;</li>
 *   <li>o segredo e' validado na inicializacao — antes um {@code JWT_SECRET} ausente so'
 *       estourava na primeira tentativa de login.</li>
 * </ul>
 */
@Component
public class JwtTokenAdapter implements TokenPort {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenAdapter.class);
    private static final String ISSUER = "Sistema-Ocorrencia-Escolar";
    private static final int TAMANHO_MINIMO_SEGREDO = 32;

    private final Algorithm algoritmo;
    private final Duration validade;

    public JwtTokenAdapter(@Value("${app.security.jwt.secret}") String secret,
                           @Value("${app.security.jwt.expiration-hours:2}") long horasDeValidade) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "app.security.jwt.secret (JWT_SECRET) nao configurado");
        }
        if (secret.length() < TAMANHO_MINIMO_SEGREDO) {
            throw new IllegalStateException(
                    "app.security.jwt.secret deve ter ao menos "
                            + TAMANHO_MINIMO_SEGREDO + " caracteres");
        }
        this.algoritmo = Algorithm.HMAC256(secret);
        this.validade = Duration.ofHours(horasDeValidade);
    }

    @Override
    public String gerarToken(Usuario usuario) {
        try {
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getLogin())
                    .withClaim("id", usuario.getId())
                    .withClaim("perfil", usuario.getPerfil().name())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plus(validade))
                    .sign(algoritmo);
        } catch (IllegalArgumentException | JWTVerificationException e) {
            throw new TokenInvalidoException("Nao foi possivel gerar o token de acesso");
        }
    }

    @Override
    public Optional<String> extrairLogin(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            String subject = JWT.require(algoritmo)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
            return Optional.ofNullable(subject);
        } catch (JWTVerificationException e) {
            log.debug("Token rejeitado: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
