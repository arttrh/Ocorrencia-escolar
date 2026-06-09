package br.com.project_sena.application.core.domain.model;

public class CategoriaOcorrencia {
    private Long id;
    private CategoriaOcorrencia categoriaOcorrencia;


    public CategoriaOcorrencia() {
    }

    public CategoriaOcorrencia(Long id, String nameCategory) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public CategoriaOcorrencia getCategoriaOcorrencia() {
        return categoriaOcorrencia;
    }
}
