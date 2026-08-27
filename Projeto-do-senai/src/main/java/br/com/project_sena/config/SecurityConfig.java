package br.com.project_sena.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.project_sena.adapter.in.web.security.JwtAuthenticationFilter;
import br.com.project_sena.adapter.in.web.security.RespostasDeSeguranca;
import br.com.project_sena.adapter.in.web.security.JwtAuthenticationFilter;
import br.com.project_sena.adapter.in.web.security.RespostasDeSeguranca;
import br.com.project_sena.application.core.domain.enums.PerfilEnum;

/**
 * Cadeia de seguranca e CORS.
 *
 * <p>Notas sobre as regras de autorizacao:</p>
 * <ul>
 *   <li>o filtro de rate limit saiu daqui — o limite agora e' aplicado dentro do use case
 *       de autenticacao, que e' onde a regra "tantas tentativas de login" pertence;</li>
 *   <li>os caminhos acompanham o contrato REST em ingles ({@code /users}, {@code /students},
 *       {@code /schoolclasses}, {@code /incidents}), que e' o que o front chama;</li>
 *   <li>as permissoes de leitura foram abertas para os perfis que precisam ver os dados
 *       (coordenacao, professores), em vez de exigir ADMINISTRATIVO para tudo;</li>
 *   <li>{@code /incidents/**} nao existia na configuracao anterior e caia em
 *       {@code anyRequest().authenticated()} sem controle de perfil.</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    private static final String ADMIN = PerfilEnum.ADMIN.name();
    private static final String ADMINISTRATIVO = PerfilEnum.ADMINISTRATIVO.name();
    private static final String COORDENADOR = PerfilEnum.COORDENADOR.name();
    private static final String ANALISTA = PerfilEnum.ANALISTA.name();
    private static final String PROFESSOR = PerfilEnum.PROFESSOR.name();
    private static final String PROFESSOR_ADM = PerfilEnum.PROFESSOR_ADMINISTRATIVO.name();

    /** Quem pode manter cadastros de aluno e turma. */
    private static final String[] GESTAO_ACADEMICA = {ADMIN, ADMINISTRATIVO, COORDENADOR};

    /** Quem pode registrar e acompanhar ocorrencias. */
    private static final String[] GESTAO_OCORRENCIAS =
            {ADMIN, ADMINISTRATIVO, COORDENADOR, ANALISTA, PROFESSOR, PROFESSOR_ADM};

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RespostasDeSeguranca respostasDeSeguranca;

    @Value("${app.cors.allowed-origins}")
    private String origensPermitidas;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          RespostasDeSeguranca respostasDeSeguranca) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.respostasDeSeguranca = respostasDeSeguranca;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // API sem sessao e autenticada por token: nao ha cookie de sessao para
                // um site terceiro reaproveitar, entao CSRF nao se aplica.
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(respostasDeSeguranca)
                        .accessDeniedHandler(respostasDeSeguranca))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/login", "/actuator/health").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
                            .permitAll()

                        // Usuarios: administracao de contas e' exclusiva do ADMIN.
                        // A excecao e' a troca da propria senha, liberada a qualquer
                        // usuario autenticado (o controller confere se e' a propria conta).
                        .requestMatchers(HttpMethod.PATCH, "/users/password").authenticated()
                        .requestMatchers("/users/**").hasRole(ADMIN)

                        // Alunos
                        .requestMatchers(HttpMethod.GET, "/students/**").hasAnyRole(GESTAO_OCORRENCIAS)
                        .requestMatchers("/students/**").hasAnyRole(GESTAO_ACADEMICA)

                        // Turmas
                        .requestMatchers(HttpMethod.GET, "/schoolclasses/**").hasAnyRole(GESTAO_OCORRENCIAS)
                        .requestMatchers("/schoolclasses/**").hasAnyRole(GESTAO_ACADEMICA)

                        // Ocorrencias: cancelar exige perfil de gestao; o restante e'
                        // acessivel a quem acompanha o dia a dia da escola.
                        .requestMatchers(HttpMethod.DELETE, "/incidents/**").hasAnyRole(GESTAO_ACADEMICA)
                        .requestMatchers("/incidents/**").hasAnyRole(GESTAO_OCORRENCIAS)

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * CORS.
     *
     * <p>As origens vem de configuracao ({@code app.cors.allowed-origins}) em vez de
     * ficarem escritas no codigo — o {@code @CrossOrigin("http://127.0.0.1:5500")} que
     * estava no controller de login deixava o endereco de desenvolvimento fixo no jar.</p>
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(
                Arrays.stream(origensPermitidas.split(",")).map(String::trim).toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        config.setExposedHeaders(List.of("Location", "Retry-After"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
