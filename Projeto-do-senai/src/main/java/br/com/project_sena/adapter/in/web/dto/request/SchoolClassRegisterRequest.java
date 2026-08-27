package br.com.project_sena.adapter.in.web.dto.request;

import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SchoolClassRegisterRequest(
        @NotBlank(message = "Nome da turma e' obrigatorio")
        @Size(max = 50, message = "Nome da turma deve ter no maximo 50 caracteres")
        String name,

        @NotNull(message = "Turno e' obrigatorio")
        TurmaTurnoEnum shift,

        @NotNull(message = "Ano e' obrigatorio")
        @Min(value = 2000, message = "Ano deve ser a partir de 2000")
        Integer year,

        @NotNull(message = "Semestre e' obrigatorio")
        SemestreEnum semester) {
}
