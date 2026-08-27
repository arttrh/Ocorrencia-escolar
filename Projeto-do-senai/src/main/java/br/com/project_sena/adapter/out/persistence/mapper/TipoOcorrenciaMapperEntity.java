package br.com.project_sena.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.entity.TipoOcorrenciaEntity;
import br.com.project_sena.application.core.domain.model.TipoOcorrencia;

@Component
public class TipoOcorrenciaMapperEntity {

    public TipoOcorrencia toDomain(TipoOcorrenciaEntity entity) {
        return new TipoOcorrencia(
                entity.getId(),
                entity.getName(),
                entity.getCategoria() == null ? null : entity.getCategoria().getId());
    }
}
