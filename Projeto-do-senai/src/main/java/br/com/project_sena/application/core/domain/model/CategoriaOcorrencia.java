package br.com.project_sena.application.core.domain.model;

import java.util.Objects;

import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;

public class CategoriaOcorrencia {

    private final Long id;
    private final String name;

    public CategoriaOcorrencia(Long id, String name) {
        this.id = id;
        if (name == null || name.isBlank()) {
            throw new RegraDeNegocioException("Nome da categoria e' obrigatorio");
        }
        this.name = name.trim();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoriaOcorrencia outra)) {
            return false;
        }
        return id != null && Objects.equals(id, outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
