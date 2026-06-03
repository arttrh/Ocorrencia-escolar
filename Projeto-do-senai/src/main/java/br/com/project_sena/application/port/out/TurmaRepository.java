package br.com.project_sena.application.port.out;

import br.com.project_sena.application.core.domain.model.Turma;

public interface TurmaRepository {
    Turma save(Turma dados);
}
