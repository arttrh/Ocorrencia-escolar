package br.com.project_sena.adapter.in.web.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cadastro de ocorrencia.
 *
 * <p>Os nomes dos campos seguem o payload que o formulario de cadastro do front ja monta
 * ({@code idSchoolClass}, {@code idStudent}, {@code registerDate}, ...).</p>
 */
public record IncidentRegisterRequest(
        @NotNull(message = "Turma e' obrigatoria")
        Long idSchoolClass,

        @NotNull(message = "Aluno e' obrigatorio")
        Long idStudent,

        @JsonFormat(pattern = Formatos.DATA_HORA)
        LocalDateTime registerDate,

        @NotBlank(message = "Categoria e' obrigatoria")
        String category,

        @NotBlank(message = "Tipo e' obrigatorio")
        String type,

        @NotBlank(message = "Descricao e' obrigatoria")
        @Size(max = 500, message = "Descricao deve ter no maximo 500 caracteres")
        String description) {
}
