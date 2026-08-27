package br.com.project_sena.application.core.domain.enums;

public enum SemestreEnum {

    PRIMEIRO("1o semestre"),
    SEGUNDO("2o semestre");

    private final String descricao;

    SemestreEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
