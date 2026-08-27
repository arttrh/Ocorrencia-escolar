package br.com.project_sena.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.entity.CategoriaOcorrenciaEntity;
import br.com.project_sena.application.core.domain.model.CategoriaOcorrencia;

@Component
public class CategoriaOcorrenciaMapperEntity {

    public CategoriaOcorrencia toDomain(CategoriaOcorrenciaEntity entity) {
        return new CategoriaOcorrencia(entity.getId(), entity.getName());
    }

    public CategoriaOcorrenciaEntity toEntity(CategoriaOcorrencia categoria) {
        return new CategoriaOcorrenciaEntity(categoria.getId(), categoria.getName());
    }
}
