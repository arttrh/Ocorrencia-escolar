package br.com.project_sena.config.security.filter;
import br.com.project_sena.adapter.out.repository.entity.UsuarioEntity;
import br.com.project_sena.adapter.out.repository.persistence.UsuarioJpaRepository;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.config.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService token;
    private final UsuarioJpaRepository repository;

    public SecurityFilter(TokenService token, UsuarioJpaRepository repository) {
        this.token = token;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null) {
            try {
                if (!header.startsWith("Bearer "))
                    throw new IllegalArgumentException();
                String email = token.validarToken(header.substring(7));
                UsuarioEntity usuario = repository.findByEmail(email).orElseThrow();
                if (usuario.getUsuarioEnum() != UsuarioEnum.ATIVO)
                    throw new IllegalArgumentException();
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    usuario.getEmail(), null, usuario.getAuthorities()));
            } catch (RuntimeException ex) {
                response.sendError(401, "Sessão inválida");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
