package br.com.project_sena.adapter.out.repository.mapper;

import br.com.project_sena.adapter.out.repository.entity.CategoriaOcorrenciaEntity;
import br.com.project_sena.application.core.domain.model.CategoriaOcorrencia;
import org.springframework.stereotype.Component;

@Component
public class CategoriaOcorrenciaMapperEntity {
    public CategoriaOcorrencia toDomain(CategoriaOcorrenciaEntity entity){
        return new CategoriaOcorrencia(
                entity.getId(),
                entity.getNameCategory()
        );
    }

    public CategoriaOcorrenciaEntity toEntity(CategoriaOcorrencia categoria){
        return new CategoriaOcorrenciaEntity(
                categoria.getId(),
                categoria.getNameCategory()
        );
    }
}
