package br.com.project_sena.application.port.in.command;

import java.time.LocalDateTime;

public record AtualizarOcorrenciaCommand(Long id,
                                         Long schoolClassId,
                                         Long studentId,
                                         String category,
                                         String type,
                                         LocalDateTime registerDate,
                                         String description) {
}
