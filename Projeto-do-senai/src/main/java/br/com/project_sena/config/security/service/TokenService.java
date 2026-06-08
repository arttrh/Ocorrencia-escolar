package br.com.project_sena.config.security.service;

import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.exception.type.Token.TokenInvalidoException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class TokenService {

    @Value("${JWT_SECRET}")
    private String secret;

    public TokenDTO gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("Sistema-Ocorrencia-Escolar")
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId())
                    .withClaim("Perfil", usuario.getPerfil().name())
                    .withExpiresAt(expiracaoToken())
                    .sign(algorithm);
            return new TokenDTO(token);
        } catch (IllegalArgumentException e) {
            throw new TokenInvalidoException("Token invalido");
        }
    }

    public String validarToken(String token) throws TokenInvalidoException{
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String tokenValidar = JWT.require(algorithm)
                    .withIssuer("Sistema-Ocorrencia-Escolar")
                    .build()
                    .verify(token)
                    .getSubject();
            return tokenValidar;
        } catch (JWTVerificationException e){
            return "Token invalido";
        }
    }

    public Instant expiracaoToken(){
        return ZonedDateTime.now(ZoneOffset.of("-03:00")).plusHours(2).toInstant();
    }
}
