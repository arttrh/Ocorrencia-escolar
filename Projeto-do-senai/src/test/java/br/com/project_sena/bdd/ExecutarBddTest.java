package br.com.project_sena.bdd;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Executa os cenarios BDD escritos em {@code src/test/resources/features}.
 *
 * <p>Os arquivos {@code .feature} estao em portugues e descrevem o comportamento
 * esperado do ponto de vista de quem usa o sistema. Servem tanto como teste executavel
 * quanto como documentacao viva das regras.</p>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.com.project_sena.bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, summary, html:target/cucumber-report.html")
public class ExecutarBddTest {
}
