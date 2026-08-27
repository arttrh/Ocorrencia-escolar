package br.com.project_sena.application.port.out;

import java.util.Optional;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;

public interface AlunoRepository {

    Aluno save(Aluno aluno);

    Optional<Aluno> findById(Long id);

    Pagina<Aluno> findByStatus(AlunoEnum status, PaginaRequest paginaRequest);
}
