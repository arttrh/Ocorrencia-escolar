package br.com.project_sena.adapter.in.controller.request.turma;


import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;

import java.time.LocalDateTime;

public record ClassUpdateDTO(
        String className,
        TurmaTurnoEnum turnoTurma,
        LocalDateTime classYear,
        LocalDateTime semestry
) {
}
