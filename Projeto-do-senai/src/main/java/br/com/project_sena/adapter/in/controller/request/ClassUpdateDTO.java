package br.com.project_sena.adapter.in.controller.request;

import br.com.project_sena.application.core.domain.enums.TurmaEnum;

public record ClassUpdateDTO(
        String className,
        String shift,
        Integer classYear
) {
}
