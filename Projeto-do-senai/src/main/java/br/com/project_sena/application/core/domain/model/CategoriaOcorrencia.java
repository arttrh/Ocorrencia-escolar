package br.com.project_sena.application.core.domain.model;

public class CategoriaOcorrencia {
    private Long id;
    private String nameCategory;

    public CategoriaOcorrencia() {
    }

    public CategoriaOcorrencia(Long id, String nameCategory) {
        this.id = id;
        this.nameCategory = nameCategory;
    }

    public Long getId() {
        return id;
    }

    public String getNameCategory() {
        return nameCategory;
    }
}
