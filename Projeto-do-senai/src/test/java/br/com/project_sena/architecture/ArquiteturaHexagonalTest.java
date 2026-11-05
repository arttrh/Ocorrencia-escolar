package br.com.project_sena.architecture;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.*;
import com.tngtech.archunit.lang.ArchRule;
@AnalyzeClasses(packages = "br.com.project_sena", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquiteturaHexagonalTest {
    @ArchTest
    static final ArchRule dominio_nao_conhece_persistencia =
        noClasses()
            .that()
            .resideInAPackage("..application.core.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..adapter..", "jakarta.persistence..", "..application.core.usecase..");
    @ArchTest
    static final ArchRule casos_de_uso_nao_conhecem_persistencia =
        noClasses()
            .that()
            .resideInAPackage("..application.core.usecase..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..adapter.out..");
    @ArchTest
    static final ArchRule web_nao_conhece_persistencia = noClasses()
                                                             .that()
                                                             .resideInAPackage("..adapter.in..")
                                                             .should()
                                                             .dependOnClassesThat()
                                                             .resideInAPackage("..adapter.out..");
    @ArchTest
    static final ArchRule entidades_na_persistencia =
        classes().that().areAnnotatedWith("jakarta.persistence.Entity").should().resideInAPackage(
            "..adapter.out.repository.entity..");
    @ArchTest
    static final ArchRule portas_sao_interfaces =
        classes().that().resideInAPackage("..application.port.out..").should().beInterfaces();
}
