package br.com.project_sena.application.core.domain.enums;

public enum UsuarioEnum {

    ATIVO,
    INATIVO;

    public boolean isAtivo() {
        return this == ATIVO;
    }
}
