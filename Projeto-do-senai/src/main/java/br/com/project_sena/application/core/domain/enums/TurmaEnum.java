package br.com.project_sena.application.core.domain.enums;

public enum TurmaEnum {

    ATIVA,
    CANCELADA;

    public boolean isAtiva() {
        return this == ATIVA;
    }
}
