package br.com.project_sena.application.core.domain.enums;

public enum TurmaTurnoEnum {

    MANHA("Manha"),
    TARDE("Tarde"),
    NOTURNO("Noturno");

    private final String descricao;

    TurmaTurnoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
