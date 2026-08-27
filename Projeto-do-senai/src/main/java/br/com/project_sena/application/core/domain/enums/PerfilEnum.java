package br.com.project_sena.application.core.domain.enums;

/**
 * Perfis de acesso do sistema. O nome da constante e' usado como ROLE do Spring
 * Security (ROLE_ADMIN, ROLE_ADMINISTRATIVO, ...) pelo adaptador de seguranca.
 */
public enum PerfilEnum {

    ADMIN("Administrador"),
    ADMINISTRATIVO("Administrativo"),
    COORDENADOR("Coordenador"),
    ANALISTA("Analista"),
    PROFESSOR("Professor"),
    PROFESSOR_ADMINISTRATIVO("Professor administrativo");

    private final String descricao;

    PerfilEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
