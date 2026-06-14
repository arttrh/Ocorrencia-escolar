package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.model.Aluno;

import java.time.LocalDate;

public record StudentDetailsDTO(
        Long studentId,
        String photo,
        String name,
        LocalDate dateBirth,
        AlunoEnum alunoEnum
){
}
