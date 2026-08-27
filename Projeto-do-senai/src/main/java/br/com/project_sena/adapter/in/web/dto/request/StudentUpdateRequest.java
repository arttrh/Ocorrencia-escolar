package br.com.project_sena.adapter.in.web.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record StudentUpdateRequest(
        @NotNull(message = "Id e' obrigatorio")
        Long id,

        @Size(max = 100, message = "Nome deve ter no maximo 100 caracteres")
        String name,

        @Past(message = "Data de nascimento deve estar no passado")
        @JsonFormat(pattern = Formatos.DATA)
        LocalDate birthDate) {
}
