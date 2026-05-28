package br.com.project_sena.config.security.filter;

import br.com.project_sena.config.security.service.TokenService;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityFilter {

    private final TokenService token;

    public SecurityFilter(TokenService token) {
        this.token = token;
    }
}
