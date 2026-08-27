package br.com.project_sena.adapter.in.web.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IncidentStatusRequest(
        @NotNull(message = "Id e' obrigatorio")
        Long id,

        @NotBlank(message = "Status e' obrigatorio")
        String status,

        @JsonFormat(pattern = Formatos.DATA_HORA)
        LocalDateTime updateDate) {
}
