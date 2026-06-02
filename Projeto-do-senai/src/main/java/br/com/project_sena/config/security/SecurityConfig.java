package br.com.project_sena.config.security;

import br.com.project_sena.config.security.filter.SecurityFilter;
import br.com.project_sena.config.security.rateLimit.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final SecurityFilter securityFilter;
    private final RateLimitFilter limitFilter;

    public SecurityConfig(SecurityFilter securityFilter, RateLimitFilter limitFilter){
        this.securityFilter = securityFilter;
        this.limitFilter = limitFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(limitFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        "/login"
                        ).permitAll().requestMatchers(HttpMethod.GET, "/usuario/cadastrar").hasAnyRole("ADMIN"
                                ,"PROFESSOR",
                                "ANALISTA",
                                "COORDENADOR",
                                "ADMINISTRATIVO")
                        .anyRequest().authenticated()
                ).build();
    }
}
