package br.com.project_sena.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import br.com.project_sena.adapter.out.ratelimit.RateLimitProperties;

/** Habilita os records de configuracao tipada. */
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
public class PropertiesConfig {
}
