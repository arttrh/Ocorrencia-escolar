package br.com.project_sena.application.core.domain.enums;

public enum AlunoEnum {

    ATIVO,
    INATIVO;

    public boolean isAtivo() {
        return this == ATIVO;
    }
}
