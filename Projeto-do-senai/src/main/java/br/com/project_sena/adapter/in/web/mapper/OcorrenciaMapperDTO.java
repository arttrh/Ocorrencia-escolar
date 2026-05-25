package br.com.project_sena.adapter.in.web.mapper;

import br.com.project_sena.adapter.in.controller.request.OccurrenceRequestDTO;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import org.springframework.stereotype.Component;

@Component
public class OcorrenciaMapperDTO {


    //Fazer esse metodo no Ocorrencia por que o Preciso passar o domain completo e nao apenas o id dele
    public Ocorrencia toDomain(OccurrenceRequestDTO dto) {
        return new Ocorrencia(
                dto.studentId(),
                dto.classId(),
                dto.categoryId(),
                dto.occurenceId(),
                dto.date(),
                dto.time(),
                dto.descriptionOccurrence()
        );
    }
}
