package br.com.project_sena.adapter.in.web.dto.request;

import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SchoolClassUpdateRequest(
        @NotNull(message = "Id e' obrigatorio")
        Long id,

        @Size(max = 50, message = "Nome da turma deve ter no maximo 50 caracteres")
        String name,

        TurmaTurnoEnum shift,

        @Min(value = 2000, message = "Ano deve ser a partir de 2000")
        Integer year,

        SemestreEnum semester) {
}
