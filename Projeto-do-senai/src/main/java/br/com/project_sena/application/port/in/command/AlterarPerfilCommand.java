package br.com.project_sena.application.port.in.command;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;

public record AlterarPerfilCommand(Long id, PerfilEnum perfil) {
}
