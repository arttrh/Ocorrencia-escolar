package br.com.project_sena.adapter.in.web.mapper;

import br.com.project_sena.adapter.in.controller.request.StudentRequestDTO;
import br.com.project_sena.adapter.in.controller.request.StudentUpdateDTO;
import br.com.project_sena.adapter.in.controller.response.StudentDetailsDTO;
import br.com.project_sena.adapter.in.controller.response.StudentListAtivosDTO;
import br.com.project_sena.adapter.in.controller.response.StudentListInativosDTO;
import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import org.springframework.stereotype.Component;

@Component
public class AlunoMapperDTO {
    public Aluno toDomain(StudentRequestDTO dto){
        return new Aluno(
                null,
                dto.photo(),
                dto.name(),
                dto.dateBirth(),
                AlunoEnum.ATIVO,
                dto.classId()
        );
    }

    public StudentDetailsDTO toDTO(Aluno aluno){
        return new StudentDetailsDTO(
                aluno.getId(),
                aluno.getPhoto(),
                aluno.getName(),
               aluno.getDateBirth(),
               aluno.getAlunoEnum()
        );
    }

    public StudentListAtivosDTO toListAtivoDTO(Aluno aluno){
        return new StudentListAtivosDTO(
                aluno.getId(),
                aluno.getPhoto(),
                aluno.getName(),
                aluno.getDateBirth(),
                AlunoEnum.ATIVO,
                aluno.getTurma().getClassName()
        );
    }

    public StudentListInativosDTO toListInvativos(Aluno aluno){
        return new StudentListInativosDTO(
                aluno.getId(),
                aluno.getPhoto(),
                aluno.getName(),
                aluno.getDateBirth(),
                aluno.getAlunoEnum(),
                aluno.getTurma().getClassName()
        );
    }

    public Aluno toDomainUpdate(StudentUpdateDTO dto){
        return new Aluno(
                null,
                dto.photo(),
                dto.name(),
                dto.dateBirth(),
                AlunoEnum.ATIVO,
                dto.classId()
        );
    }
}
