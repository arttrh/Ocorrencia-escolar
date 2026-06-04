package br.com.project_sena.config.security;

import br.com.project_sena.config.security.filter.SecurityFilter;
import br.com.project_sena.config.security.rateLimit.RateLimitFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {
    private final SecurityFilter securityFilter;
    private final RateLimitFilter limitFilter;

    public SecurityConfig(SecurityFilter securityFilter, RateLimitFilter limitFilter){
        this.securityFilter = securityFilter;
        this.limitFilter = limitFilter;
    }

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfiguration()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(limitFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        "/login"
                        ).permitAll()

                        // Usuario
                        .requestMatchers(HttpMethod.POST, "/usuario/cadastrar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/usuario/ativos").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/usuario/inativos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/usuario/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/usuario/atualizar/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/usuario/delete/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/usuario/reativar/{id}").hasRole("ADMIN")

                        // Aluno
                        .requestMatchers(HttpMethod.POST, "/aluno/cadastrar").hasRole("ADMINISTRATIVO")
                        .requestMatchers(HttpMethod.GET, "/aluno/ativos").hasRole("ADMINISTRATIVO")
                        .requestMatchers(HttpMethod.GET, "/aluno/inativos").hasRole("ADMINISTRATIVO")
                        .requestMatchers(HttpMethod.GET, "/aluno/{id}").hasRole("ADMINISTRATIVO")
                        .requestMatchers(HttpMethod.POST, "/aluno/atualizar/{id}").hasRole("ADMINISTRATIVO")
                        .requestMatchers(HttpMethod.DELETE, "/aluno/delete/{id}").hasRole("ADMINISTRATIVO")
                        .requestMatchers(HttpMethod.PATCH, "/aluno/reativar/{id}").hasRole("ADMINISTRATIVO")

                        //Turma
                        .anyRequest().authenticated()
                ).build();
    }

    @Bean
    public CorsConfigurationSource corsConfiguration(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        config.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
