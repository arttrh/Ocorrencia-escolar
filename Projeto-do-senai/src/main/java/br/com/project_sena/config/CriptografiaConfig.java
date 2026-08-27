package br.com.project_sena.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Bean do algoritmo de hash de senha.
 *
 * <p>Fica em uma configuracao propria, separada de {@code SecurityConfig}, para quebrar
 * um ciclo de criacao de beans: a cadeia de seguranca depende do filtro JWT, que depende
 * do caso de uso de autenticacao, que depende do {@code PasswordEncoder}. Com o encoder
 * declarado dentro de {@code SecurityConfig} o Spring fechava o circulo e a aplicacao
 * nao subia.</p>
 */
@Configuration
public class CriptografiaConfig {

    /**
     * Custo 10 e' o padrao do Spring Security e um equilibrio razoavel entre resistencia
     * a forca bruta e latencia do login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
