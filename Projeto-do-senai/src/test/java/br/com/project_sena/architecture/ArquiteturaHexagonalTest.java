package br.com.project_sena.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Guarda-corpo automatico da arquitetura hexagonal.
 *
 * <p>Estas regras rodam no {@code mvn test}: qualquer import que atravesse uma fronteira
 * proibida quebra o build antes de virar divida tecnica. Para uma varredura mais ampla —
 * incluindo nomenclatura, camadas orfas e um relatorio legivel — use o scanner de linha
 * de comando em {@code tools/arch_scan.py}.</p>
 *
 * <p>Camadas, de dentro para fora:</p>
 * <pre>
 *   application.core.domain   nucleo: modelos, enums, VOs e excecoes de negocio
 *   application.core.usecase  orquestracao das regras
 *   application.port          contratos (in = casos de uso, out = dependencias)
 *   adapter                   implementacoes concretas (web, JPA, JWT, mensageria)
 *   config                    montagem Spring
 * </pre>
 */
@AnalyzeClasses(
        packages = "br.com.project_sena",
        importOptions = {ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class})
class ArquiteturaHexagonalTest {

    private static final String DOMINIO = "br.com.project_sena.application.core.domain..";
    private static final String USECASE = "br.com.project_sena.application.core.usecase..";
    private static final String PORT_IN = "br.com.project_sena.application.port.in..";
    private static final String PORT_OUT = "br.com.project_sena.application.port.out..";
    private static final String APPLICATION = "br.com.project_sena.application..";
    private static final String ADAPTER = "br.com.project_sena.adapter..";
    private static final String CONFIG = "br.com.project_sena.config..";

    /** Classes de {@code config}: a raiz de composicao, isenta do modelo de camadas. */
    private static final DescribedPredicate<JavaClass> RAIZ_DE_COMPOSICAO =
            com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage(CONFIG);

    // =====================================================================
    // 1. O nucleo nao conhece framework nenhum
    // =====================================================================

    /**
     * O caso concreto que motivou esta regra: {@code Usuario} implementava
     * {@code UserDetails} do Spring Security e {@code Ocorrencia} importava
     * {@code jakarta.validation}. Trocar de framework exigiria reescrever o dominio.
     */
    @ArchTest
    static final ArchRule dominio_nao_depende_de_framework = noClasses()
            .that().resideInAPackage(DOMINIO)
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "jakarta.persistence..",
                    "jakarta.validation..",
                    "jakarta.servlet..",
                    "com.fasterxml.jackson..",
                    "io.swagger..",
                    "com.auth0..",
                    "io.github.bucket4j..",
                    "lombok..")
            .because("o dominio precisa compilar e ser testavel sem nenhum framework");

    @ArchTest
    static final ArchRule usecase_nao_depende_de_framework = noClasses()
            .that().resideInAPackage(USECASE)
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "jakarta.persistence..",
                    "jakarta.servlet..",
                    "com.fasterxml.jackson..",
                    "io.swagger..")
            .because("os use cases sao POJOs montados em config.BeanConfiguration");

    /**
     * As portas sao o vocabulario do nucleo. {@code Page}/{@code Pageable} do Spring Data
     * nas assinaturas — como estava antes — arrasta o framework para dentro de quem so'
     * deveria enxergar o contrato.
     */
    @ArchTest
    static final ArchRule portas_nao_expoem_tipos_de_framework = noClasses()
            .that().resideInAnyPackage(PORT_IN, PORT_OUT)
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "jakarta.persistence..",
                    "jakarta.servlet..",
                    "io.swagger..")
            .because("porta e' contrato: HTTP, JPA e Spring Data nao pertencem a ela");

    // =====================================================================
    // 2. Direcao das dependencias
    // =====================================================================

    @ArchTest
    static final ArchRule aplicacao_nao_conhece_adaptadores = noClasses()
            .that().resideInAPackage(APPLICATION)
            .should().dependOnClassesThat().resideInAnyPackage(ADAPTER, CONFIG)
            .because("a seta aponta para dentro: adaptadores dependem da aplicacao, nunca o contrario");

    @ArchTest
    static final ArchRule dominio_nao_conhece_use_cases = noClasses()
            .that().resideInAPackage(DOMINIO)
            .should().dependOnClassesThat().resideInAnyPackage(USECASE, PORT_IN, PORT_OUT)
            .because("o modelo de dominio e' a camada mais interna");

    @ArchTest
    static final ArchRule adaptadores_nao_conversam_entre_si = noClasses()
            .that().resideInAPackage("br.com.project_sena.adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("br.com.project_sena.adapter.out..")
            .because("o adaptador web fala com portas, nunca com entidades JPA, "
                    + "repositorios concretos ou outro adaptador");

    // =====================================================================
    // 3. Estrutura geral (Onion/Hexagonal)
    // =====================================================================

    /**
     * O pacote {@code config} nao entra no modelo de proposito: ele e' a raiz de
     * composicao, e a raiz precisa enxergar todas as camadas para instanciar e ligar as
     * pecas. Que ela seja a <em>unica</em> com esse privilegio e' garantido pela regra
     * {@link #aplicacao_nao_conhece_adaptadores}.
     */
    @ArchTest
    static final ArchRule camadas_respeitadas = onionArchitecture()
            .domainModels("br.com.project_sena.application.core.domain..")
            .domainServices(
                    "br.com.project_sena.application.core.usecase..",
                    "br.com.project_sena.application.port..")
            .adapter("web", "br.com.project_sena.adapter.in.web..")
            .adapter("persistence", "br.com.project_sena.adapter.out.persistence..")
            .adapter("security", "br.com.project_sena.adapter.out.security..")
            .adapter("messaging", "br.com.project_sena.adapter.out.messaging..")
            .adapter("ratelimit", "br.com.project_sena.adapter.out.ratelimit..")
            .adapter("transaction", "br.com.project_sena.adapter.out.transaction..")
            .withOptionalLayers(true)
            .ignoreDependency(RAIZ_DE_COMPOSICAO, DescribedPredicate.alwaysTrue());

    // =====================================================================
    // 4. Convencoes
    // =====================================================================

    @ArchTest
    static final ArchRule entidades_jpa_ficam_na_persistencia = classes()
            .that().areAnnotatedWith("jakarta.persistence.Entity")
            .should().resideInAPackage("br.com.project_sena.adapter.out.persistence.entity..")
            .because("mapeamento relacional e' detalhe do adaptador de persistencia");

    @ArchTest
    static final ArchRule controllers_ficam_no_adaptador_web = classes()
            .that().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .should().resideInAPackage("br.com.project_sena.adapter.in.web.controller..")
            .andShould().haveSimpleNameEndingWith("Controller");

    /**
     * Vale para os tipos de topo. Um record aninhado dentro da porta — como
     * {@code RateLimiterPort.Veredito} — e' o vocabulario do proprio contrato e nao
     * precisa (nem pode) ser interface.
     */
    @ArchTest
    static final ArchRule portas_de_saida_sao_interfaces = classes()
            .that().resideInAPackage(PORT_OUT)
            .and().areTopLevelClasses()
            .should().beInterfaces();

    @ArchTest
    static final ArchRule portas_de_entrada_sao_interfaces = classes()
            .that().resideInAPackage(PORT_IN)
            .and().haveSimpleNameEndingWith("UseCase")
            .should().beInterfaces();

    /**
     * Impede o retorno do {@code UsuarioDomainController<C, R, RI, U, D, DR, DET, ID>}:
     * uma "porta de entrada" cujos metodos devolviam {@code ResponseEntity}, ou seja,
     * um controller HTTP disfarcado de contrato de aplicacao.
     */
    @ArchTest
    static final ArchRule portas_de_entrada_nao_retornam_response_entity = noClasses()
            .that().resideInAPackage(PORT_IN)
            .should().dependOnClassesThat().haveFullyQualifiedName(
                    "org.springframework.http.ResponseEntity")
            .because("decidir status HTTP e' trabalho do controller, nao do caso de uso");

    @ArchTest
    static final ArchRule excecoes_de_negocio_ficam_no_dominio = classes()
            .that().haveSimpleNameEndingWith("Exception")
            .and().resideInAPackage("br.com.project_sena..")
            .should().resideInAPackage("br.com.project_sena.application.core.domain.exception..")
            .because("quem lanca as excecoes de negocio e' o nucleo");

    @ArchTest
    static final ArchRule sem_saida_no_console = noClasses()
            .that().resideInAPackage("br.com.project_sena..")
            .should().accessField(System.class, "out")
            .orShould().accessField(System.class, "err")
            .because("use o logger: System.out nao tem nivel, contexto nem destino configuravel");
}
