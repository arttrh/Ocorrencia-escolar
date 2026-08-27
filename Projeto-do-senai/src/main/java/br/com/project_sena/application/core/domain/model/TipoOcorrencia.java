package br.com.project_sena.application.core.domain.model;

import java.util.Objects;

import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;

public class TipoOcorrencia {

    private final Long id;
    private final String name;
    private final Long categoriaId;

    public TipoOcorrencia(Long id, String name, Long categoriaId) {
        this.id = id;
        if (name == null || name.isBlank()) {
            throw new RegraDeNegocioException("Nome do tipo de ocorrencia e' obrigatorio");
        }
        this.name = name.trim();
        this.categoriaId = categoriaId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TipoOcorrencia outro)) {
            return false;
        }
        return id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
