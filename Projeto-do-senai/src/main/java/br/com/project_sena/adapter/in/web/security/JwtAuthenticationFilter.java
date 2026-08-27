package br.com.project_sena.adapter.in.web.security;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.project_sena.application.core.domain.exception.DomainException;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.port.in.AutenticarUsuarioUseCase;
import br.com.project_sena.application.port.out.TokenPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Autentica a requisicao a partir do cabecalho {@code Authorization: Bearer <token>}.
 *
 * <p>Correcoes em relacao ao {@code SecurityFilter} anterior:</p>
 * <ul>
 *   <li>ele chamava {@code repository.findByEmail(email).orElseThrow()} — com um token
 *       invalido, o "email" era a string {@code "Token invalido"} e o
 *       {@code NoSuchElementException} resultante virava 500 em vez de 401;</li>
 *   <li>colocava a entidade JPA no {@code SecurityContext}, vazando persistencia para
 *       dentro da seguranca; agora entra o {@link UsuarioPrincipal};</li>
 *   <li>era anotado com {@code @Configuration} sendo um filtro;</li>
 *   <li>usuario inativo continuava autenticando com um token antigo — agora e' recusado.</li>
 * </ul>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String PREFIXO = "Bearer ";

    private final TokenPort tokenPort;
    private final AutenticarUsuarioUseCase autenticarUsuario;

    public JwtAuthenticationFilter(TokenPort tokenPort, AutenticarUsuarioUseCase autenticarUsuario) {
        this.tokenPort = tokenPort;
        this.autenticarUsuario = autenticarUsuario;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        extrairToken(request)
                .flatMap(tokenPort::extrairLogin)
                .ifPresent(login -> autenticar(login, request));

        filterChain.doFilter(request, response);
    }

    /** O endpoint de login nao precisa passar pela leitura de token. */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return "/login".equals(uri)
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs");
    }

    private void autenticar(String login, HttpServletRequest request) {
        try {
            Usuario usuario = autenticarUsuario.carregarPorLogin(login);
            if (!usuario.isAtivo()) {
                log.debug("Token valido de usuario inativo: {}", login);
                return;
            }
            UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (DomainException e) {
            // Token assinado por nos mas cujo usuario nao existe mais: segue sem autenticar,
            // e a cadeia de seguranca responde 401.
            log.debug("Nao foi possivel autenticar {}: {}", login, e.getMessage());
        }
    }

    private Optional<String> extrairToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(PREFIXO)) {
            return Optional.empty();
        }
        String token = header.substring(PREFIXO.length()).trim();
        return token.isEmpty() ? Optional.empty() : Optional.of(token);
    }
}
