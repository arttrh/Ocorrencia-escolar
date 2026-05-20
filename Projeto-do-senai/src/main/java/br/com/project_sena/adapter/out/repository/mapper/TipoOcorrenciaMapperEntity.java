package br.com.project_sena.adapter.out.repository.mapper;

import br.com.project_sena.adapter.out.repository.entity.TipoOcorrenciaEntity;
import br.com.project_sena.application.core.domain.model.TipoOcorrencia;
import org.springframework.stereotype.Component;

@Component
public class TipoOcorrenciaMapperEntity {
    public TipoOcorrencia toDomain(TipoOcorrenciaEntity entity){
        return new TipoOcorrencia(
                entity.getId(),
                entity.getNameOccurrence()
        );
    }

    public TipoOcorrenciaEntity toEntity(TipoOcorrencia ocorrencia){
        return new TipoOcorrenciaEntity(
                ocorrencia.getId(),
                ocorrencia.getNameOccurrence()
        );
    }
}
