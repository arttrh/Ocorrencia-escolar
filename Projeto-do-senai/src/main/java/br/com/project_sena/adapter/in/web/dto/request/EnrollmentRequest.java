package br.com.project_sena.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;

/** Matricula de um aluno em uma turma. */
public record EnrollmentRequest(
        @NotNull(message = "Id do aluno e' obrigatorio")
        Long studentId) {
}
