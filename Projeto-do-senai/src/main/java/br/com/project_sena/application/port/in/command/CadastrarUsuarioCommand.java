package br.com.project_sena.application.port.in.command;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;

public record CadastrarUsuarioCommand(String name, String login, String password, PerfilEnum perfil) {
}
