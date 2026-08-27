package br.com.project_sena.application.port.in.command;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;

public record AtualizarUsuarioCommand(Long id, String name, String login, PerfilEnum perfil) {
}
