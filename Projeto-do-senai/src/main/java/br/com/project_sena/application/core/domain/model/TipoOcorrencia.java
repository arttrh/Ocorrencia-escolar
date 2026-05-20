package br.com.project_sena.application.core.domain.model;

public class TipoOcorrencia {

    private Long id;
    private String nameOccurrence;

    public TipoOcorrencia(Long id, String nameOccurrence) {
        this.id = id;
        this.nameOccurrence = nameOccurrence;
    }

    public TipoOcorrencia() {
    }

    public Long getId() {
        return id;
    }

    public String getNameOccurrence() {
        return nameOccurrence;
    }
}
