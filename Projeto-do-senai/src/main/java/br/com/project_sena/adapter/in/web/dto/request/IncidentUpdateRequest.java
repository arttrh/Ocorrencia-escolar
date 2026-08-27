package br.com.project_sena.adapter.in.web.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Atualizacao parcial: campos ausentes preservam o valor gravado. */
public record IncidentUpdateRequest(
        @NotNull(message = "Id e' obrigatorio")
        Long id,

        Long idSchoolClass,

        Long idStudent,

        @JsonFormat(pattern = Formatos.DATA_HORA)
        LocalDateTime registerDate,

        String category,

        String type,

        @Size(max = 500, message = "Descricao deve ter no maximo 500 caracteres")
        String description) {
}
