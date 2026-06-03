package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.enums.TurmaEnum;

public record ClassDetailsDTO (
        Long idClass,
        String className,
        String shift,
        Integer classYear,
        TurmaEnum turmaEnum
){
}
