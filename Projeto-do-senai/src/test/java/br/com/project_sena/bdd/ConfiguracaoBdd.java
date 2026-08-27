package br.com.project_sena.bdd;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import io.cucumber.spring.CucumberContextConfiguration;

/**
 * Liga o Cucumber ao contexto Spring: os steps recebem os mesmos beans reais que a
 * aplicacao usa em producao, apenas com o banco em memoria do perfil de teste.
 */
@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ConfiguracaoBdd {
}
