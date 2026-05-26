package br.com.project_sena.adapter.in.web.mapper;

import br.com.project_sena.adapter.in.controller.response.OccurrenceDetailsDTO;
import br.com.project_sena.adapter.in.controller.response.OccurrenceListDTO;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import org.springframework.stereotype.Component;

@Component
public class OcorrenciaMapperDTO {

    public OccurrenceListDTO toListDTO(Ocorrencia list) {
        return new OccurrenceListDTO(
                list.getId(),
                list.getStudent().getName(),
                list.getTurma().getClassName(),
                list.getCategory().getNameCategory(),
                list.getOccurrenceType().getNameOccurrence(),
                list.getDataOcorrencia()
        );
    }

    public OccurrenceDetailsDTO toDetailsDTO(Ocorrencia details) {
        return new OccurrenceDetailsDTO(
                details.getId(),
                details.getStudent().getName(),
                details.getTurma().getClassName(),
                details.getCategory().getNameCategory(),
                details.getOccurrenceType().getNameOccurrence(),
                details.getDataOcorrencia(),
                details.getTime(),
                details.getDescricaoDaOcorrencia()
        );
    }
}
