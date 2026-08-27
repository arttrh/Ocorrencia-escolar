package br.com.project_sena.application.port.in.command;

import java.time.LocalDate;

public record CadastrarAlunoCommand(String name, LocalDate birthDate) {
}
