package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;

public record ClassListAtivoDTO(
        Long id,
        String className,
        String shift,
        Integer classYear,
        TurmaEnum turmaEnum,
        Long studentId,
        AlunoEnum alunoEnum
) {
}
