package br.com.project_sena.adapter.in.web.mapper;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.in.web.dto.request.IncidentRegisterRequest;
import br.com.project_sena.adapter.in.web.dto.request.IncidentStatusRequest;
import br.com.project_sena.adapter.in.web.dto.request.IncidentUpdateRequest;
import br.com.project_sena.adapter.in.web.dto.response.IncidentResponse;
import br.com.project_sena.adapter.in.web.dto.response.IncidentSummaryResponse;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.vo.ResumoOcorrencias;
import br.com.project_sena.application.port.in.command.AlterarStatusOcorrenciaCommand;
import br.com.project_sena.application.port.in.command.AtualizarOcorrenciaCommand;
import br.com.project_sena.application.port.in.command.CadastrarOcorrenciaCommand;

@Component
public class OcorrenciaWebMapper {

    public CadastrarOcorrenciaCommand toCommand(IncidentRegisterRequest request) {
        return new CadastrarOcorrenciaCommand(
                request.idSchoolClass(),
                request.idStudent(),
                request.category(),
                request.type(),
                request.registerDate(),
                request.description());
    }

    public AtualizarOcorrenciaCommand toCommand(IncidentUpdateRequest request) {
        return new AtualizarOcorrenciaCommand(
                request.id(),
                request.idSchoolClass(),
                request.idStudent(),
                request.category(),
                request.type(),
                request.registerDate(),
                request.description());
    }

    public AlterarStatusOcorrenciaCommand toCommand(IncidentStatusRequest request) {
        return new AlterarStatusOcorrenciaCommand(
                request.id(), request.status(), request.updateDate());
    }

    public IncidentResponse toResponse(Ocorrencia ocorrencia) {
        return new IncidentResponse(
                ocorrencia.getId(),
                ocorrencia.getTurma().getId(),
                ocorrencia.getTurma().getName(),
                ocorrencia.getStudent().getId(),
                ocorrencia.getStudent().getName(),
                ocorrencia.getRegisterDate(),
                ocorrencia.getCategory().getName(),
                ocorrencia.getType().getName(),
                ocorrencia.getDescription(),
                ocorrencia.getStatus().name(),
                ocorrencia.getStatus().getDescricao(),
                ocorrencia.getUpdateDate(),
                ocorrencia.isDeleted());
    }

    public IncidentSummaryResponse toResponse(ResumoOcorrencias resumo) {
        return new IncidentSummaryResponse(
                resumo.total(OcorrenciaEnum.ATIVA),
                resumo.total(OcorrenciaEnum.AGUARDANDO),
                resumo.total(OcorrenciaEnum.ATENDENDO),
                resumo.total(OcorrenciaEnum.RESOLVIDA),
                resumo.total(OcorrenciaEnum.NAO_RESOLVIDA),
                resumo.total(OcorrenciaEnum.FECHADA),
                resumo.totalGeral(),
                resumo.byCategory(),
                resumo.byType(),
                resumo.bySchoolClass(),
                resumo.byStudent());
    }
}
