package br.com.project_sena.application.port.out;

import java.util.Optional;

import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;

public interface TurmaRepository {

    Turma save(Turma turma);

    Optional<Turma> findById(Long id);

    Pagina<Turma> findByStatus(TurmaEnum status, PaginaRequest paginaRequest);
}
