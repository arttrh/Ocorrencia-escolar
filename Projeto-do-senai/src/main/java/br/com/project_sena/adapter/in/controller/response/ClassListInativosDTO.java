package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;

import java.time.LocalDateTime;

public record ClassListInativosDTO (
        Long id,
        String className,
        TurmaTurnoEnum turnoTurma,
        LocalDateTime classYear,
        TurmaEnum turmaEnum,
        LocalDateTime semestry
){
}
