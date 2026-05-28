package br.com.project_sena.config.security.service;

import br.com.project_sena.application.core.domain.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

@Component
public class TokenService {

    private String secret = "{JWT_SECRET}";

    public TokenDTO gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("Sistema-Ocorrencia-Escolar")
                    .withSubject(usuario.getLogin())
                    .withClaim("id", usuario.getId())
                    .withClaim("Perfil", usuario.getPerfil().name())
                    .sign(algorithm);
            return new TokenDTO(token);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
