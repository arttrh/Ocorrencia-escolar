package br.com.project_sena.adapter.out.repository.mapper;

import br.com.project_sena.adapter.out.repository.entity.TipoCategoriaEntity;
import br.com.project_sena.application.core.domain.model.TipoOcorrencia;
import org.springframework.stereotype.Component;

@Component
public class TipoOcorrenciaMapperEntity {
    public TipoOcorrencia toDomain(TipoCategoriaEntity entity) {
        return new TipoOcorrencia(
                entity.getId(),
                entity.getNameOccurrence()
        );
    }

    public TipoCategoriaEntity toEntity(TipoOcorrencia ocorrencia) {
        return new TipoCategoriaEntity(
                ocorrencia.getId(),
                ocorrencia.getNameOccurrence()
        );
    }
}
