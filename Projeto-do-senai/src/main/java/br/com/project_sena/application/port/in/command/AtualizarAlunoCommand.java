package br.com.project_sena.application.port.in.command;

import java.time.LocalDate;

public record AtualizarAlunoCommand(Long id, String name, LocalDate birthDate) {
}
