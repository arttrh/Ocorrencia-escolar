package br.com.project_sena.adapter.in.web.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.project_sena.adapter.in.web.dto.request.Formatos;

/**
 * Ocorrencia como o front a consome.
 *
 * <p>Traz os ids (para as telas de edicao) e os nomes ja resolvidos de turma e aluno
 * (para as tabelas de listagem, cujas colunas sao {@code schoolClassName} e
 * {@code studentName}).</p>
 */
public record IncidentResponse(Long id,
                               Long idSchoolClass,
                               String schoolClassName,
                               Long idStudent,
                               String studentName,
                               @JsonFormat(pattern = Formatos.DATA_HORA) LocalDateTime registerDate,
                               String category,
                               String type,
                               String description,
                               String status,
                               String statusDescription,
                               @JsonFormat(pattern = Formatos.DATA_HORA) LocalDateTime updateDate,
                               boolean deleted) {
}
