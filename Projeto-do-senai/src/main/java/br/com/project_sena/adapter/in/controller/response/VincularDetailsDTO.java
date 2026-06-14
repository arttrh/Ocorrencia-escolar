package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;

public record VincularDetailsDTO(
        Long studentId,
        String name,
        Long classId,
        String className,
        TurmaTurnoEnum turmaTurnoEnum
) {
}
