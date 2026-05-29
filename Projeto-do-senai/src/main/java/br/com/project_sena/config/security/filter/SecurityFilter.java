package br.com.project_sena.config.security.filter;

import br.com.project_sena.adapter.out.repository.entity.UsuarioEntity;
import br.com.project_sena.adapter.out.repository.persistence.UsuarioJpaRepository;
import br.com.project_sena.application.core.service.AutenticacaoService;
import br.com.project_sena.config.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService token;
    private final AutenticacaoService service;
    private final UsuarioJpaRepository repository;

    public SecurityFilter(TokenService token, AutenticacaoService service, UsuarioJpaRepository repository) {
        this.token = token;
        this.service = service;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");
            if (authHeader != null){
                var tokenLimpo = authHeader.replace("Bearer ", "");
                var login = token.validarToken(tokenLimpo);
                UsuarioEntity usuario = repository.findByLogin(login).orElseThrow();
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                usuario,
                                null,
                                usuario.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
    }
}
