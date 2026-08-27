package br.com.project_sena.adapter.in.web.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

// Jackson 3: no Spring Boot 4 o mapper auto-configurado e' tools.jackson.databind,
// nao mais com.fasterxml.jackson.databind (as anotacoes seguem em com.fasterxml).
import tools.jackson.databind.ObjectMapper;

import br.com.project_sena.adapter.in.web.dto.response.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 401 e 403 no mesmo formato JSON do resto da API.
 *
 * <p>O {@code MeuAccessDeniedHandler} anterior escrevia texto puro com
 * {@code Content-Type: application/type} — tipo MIME inexistente, que fazia o front
 * falhar ao tentar interpretar a resposta.</p>
 */
@Component
public class RespostasDeSeguranca implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public RespostasDeSeguranca(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        escrever(request, response, HttpStatus.UNAUTHORIZED,
                "Autenticacao necessaria. Faca login novamente.");
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        escrever(request, response, HttpStatus.FORBIDDEN,
                "Seu perfil de acesso nao permite esta operacao.");
    }

    private void escrever(HttpServletRequest request, HttpServletResponse response,
                          HttpStatus status, String mensagem) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ErroResponse.de(
                status.value(), status.getReasonPhrase(), mensagem, request.getRequestURI()));
    }
}
