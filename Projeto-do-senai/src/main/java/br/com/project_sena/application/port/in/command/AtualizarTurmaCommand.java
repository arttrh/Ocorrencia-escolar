package br.com.project_sena.application.port.in.command;

import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;

public record AtualizarTurmaCommand(Long id, String name, TurmaTurnoEnum shift, Integer year, SemestreEnum semester) {
}
