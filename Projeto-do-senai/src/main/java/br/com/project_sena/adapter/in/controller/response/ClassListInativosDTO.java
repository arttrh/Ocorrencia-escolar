package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;

public record ClassListInativosDTO (
        Long id,
        String className,
        String shift,
        Integer classYear,
        TurmaEnum turmaEnum,
        Long idStudent,
        AlunoEnum alunoEnum
){
}
