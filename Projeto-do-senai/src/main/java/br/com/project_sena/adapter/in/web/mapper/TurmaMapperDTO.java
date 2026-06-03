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

    public Turma toDetails(ClassDetailsDTO dto){
        return new Turma(
                dto.idClass(),
                dto.className(),
                dto.shift(),
                dto.classYear(),
                dto.turmaEnum()
        );
    }

    public Turma toUpdateDTO(ClassUpdateDTO dto){
        return new Turma(
                dto.className(),
                dto.shift(),
                dto.classYear(),
                dto.turmaEnum()
        );
    }

    public Turma toListAtivosDTO(ClassListAtivoDTO dto){
        return new Turma(
                dto.id(),
                dto.className(),
                dto.shift(),
                dto.classYear(),
                TurmaEnum.ATIVA
        );
    }

    public Turma toListInativos(ClassListInativosDTO dto){
        return new Turma(
                dto.id(),
                dto.className(),
                dto.shift(),
                dto.classYear(),
                TurmaEnum.CANCELADA
        );
    }

}
