package br.com.project_sena.adapter.in.web.mapper;

import br.com.project_sena.adapter.in.controller.request.ClassRegisterDTO;
import br.com.project_sena.adapter.in.controller.request.ClassUpdateDTO;
import br.com.project_sena.adapter.in.controller.response.ClassDetailsDTO;
import br.com.project_sena.adapter.in.controller.response.ClassListAtivoDTO;
import br.com.project_sena.adapter.in.controller.response.ClassListInativosDTO;
import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.model.Turma;
import org.springframework.stereotype.Component;

@Component
public class TurmaMapperDTO {

    public Turma toDomain(ClassRegisterDTO dto){
        return new Turma(
                dto.className(),
                dto.shift(),
                dto.classYear()
        );
    }

    public ClassDetailsDTO toDetailsDTO(Turma dto){
        return new ClassDetailsDTO(
              dto.getId(),
              dto.getClassName(),
              dto.getShift(),
              dto.getClassYear(),
              dto.getTurmaEnum()
        );
    }

    public Turma toUpdateDTO(ClassUpdateDTO dto){
        return new Turma(
                dto.className(),
                dto.shift(),
                dto.classYear()
        );
    }

    public ClassListAtivoDTO toListAtivosDTO(Turma dto){
        return new ClassListAtivoDTO(
                dto.getId(),
                dto.getClassName(),
                dto.getShift(),
                dto.getClassYear(),
                dto.getTurmaEnum()
        );
    }

    public ClassListInativosDTO toListInativos(Turma dto){
        return new ClassListInativosDTO(
                dto.getId(),
                dto.getClassName(),
                dto.getShift(),
                dto.getClassYear(),
                dto.getTurmaEnum()
        );
    }
}
