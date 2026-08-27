package br.com.project_sena.adapter.in.web.mapper;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.in.web.dto.request.SchoolClassRegisterRequest;
import br.com.project_sena.adapter.in.web.dto.request.SchoolClassUpdateRequest;
import br.com.project_sena.adapter.in.web.dto.response.SchoolClassResponse;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.port.in.command.AtualizarTurmaCommand;
import br.com.project_sena.application.port.in.command.CadastrarTurmaCommand;

@Component
public class TurmaWebMapper {

    public CadastrarTurmaCommand toCommand(SchoolClassRegisterRequest request) {
        return new CadastrarTurmaCommand(
                request.name(), request.shift(), request.year(), request.semester());
    }

    public AtualizarTurmaCommand toCommand(SchoolClassUpdateRequest request) {
        return new AtualizarTurmaCommand(
                request.id(), request.name(), request.shift(), request.year(), request.semester());
    }

    public SchoolClassResponse toResponse(Turma turma) {
        return new SchoolClassResponse(
                turma.getId(),
                turma.getName(),
                turma.getShift(),
                turma.getYear(),
                turma.getSemester(),
                turma.isCancelada());
    }
}
