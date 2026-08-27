package br.com.project_sena.application.port.in.command;

import java.time.LocalDateTime;

/** @param status nome ou slug do status ({@code RESOLVIDA} ou {@code solved}) */
public record AlterarStatusOcorrenciaCommand(Long id, String status, LocalDateTime updateDate) {
}
