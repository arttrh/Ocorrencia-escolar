package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;

import java.time.LocalDate;

public record StudentListAtivosDTO(
        Long id,
        String photo,
        String name,
        LocalDate dateBirth,
        AlunoEnum alunoEnum
) {
}
