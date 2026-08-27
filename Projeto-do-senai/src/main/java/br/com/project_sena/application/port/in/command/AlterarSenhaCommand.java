package br.com.project_sena.application.port.in.command;

public record AlterarSenhaCommand(Long id, String senhaAtual, String novaSenha) {
}
