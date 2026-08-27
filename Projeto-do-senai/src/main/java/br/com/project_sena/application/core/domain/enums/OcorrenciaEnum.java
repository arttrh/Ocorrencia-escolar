package br.com.project_sena.application.core.domain.enums;

import java.util.Arrays;

/**
 * Situacao de uma ocorrencia.
 *
 * <p>O {@code slug} e' a forma publica usada na API ({@code /incidents/status/waiting}),
 * mantida separada do nome da constante para que renomear a constante nao quebre o
 * contrato HTTP.</p>
 */
public enum OcorrenciaEnum {

    AGUARDANDO("waiting", "Aguardando atendimento"),
    ATENDENDO("progressing", "Em atendimento"),
    ATIVA("active", "Ativa"),
    RESOLVIDA("solved", "Resolvida"),
    NAO_RESOLVIDA("unsolved", "Encerrada sem solucao"),
    FECHADA("closed", "Fechada");

    private final String slug;
    private final String descricao;

    OcorrenciaEnum(String slug, String descricao) {
        this.slug = slug;
        this.descricao = descricao;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Status finais: uma ocorrencia nesses estados nao aceita mais transicao. */
    public boolean isFinal() {
        return this == RESOLVIDA || this == NAO_RESOLVIDA || this == FECHADA;
    }

    public static OcorrenciaEnum porSlugOuNome(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String normalizado = valor.trim();
        return Arrays.stream(values())
                .filter(status -> status.slug.equalsIgnoreCase(normalizado)
                        || status.name().equalsIgnoreCase(normalizado))
                .findFirst()
                .orElse(null);
    }
}
