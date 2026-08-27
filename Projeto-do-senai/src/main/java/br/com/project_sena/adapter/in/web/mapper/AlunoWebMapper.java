package br.com.project_sena.adapter.in.web.mapper;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.in.web.dto.request.StudentRegisterRequest;
import br.com.project_sena.adapter.in.web.dto.request.StudentUpdateRequest;
import br.com.project_sena.adapter.in.web.dto.response.StudentResponse;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.port.in.command.AtualizarAlunoCommand;
import br.com.project_sena.application.port.in.command.CadastrarAlunoCommand;

@Component
public class AlunoWebMapper {

    public CadastrarAlunoCommand toCommand(StudentRegisterRequest request) {
        return new CadastrarAlunoCommand(request.name(), request.birthDate());
    }

    public AtualizarAlunoCommand toCommand(StudentUpdateRequest request) {
        return new AtualizarAlunoCommand(request.id(), request.name(), request.birthDate());
    }

    public StudentResponse toResponse(Aluno aluno) {
        return new StudentResponse(
                aluno.getId(),
                aluno.getName(),
                aluno.getBirthDate(),
                aluno.getImageUrl(),
                aluno.isAtivo());
    }
}
