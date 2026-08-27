package br.com.project_sena.application.port.out;

import java.util.List;
import java.util.Optional;

import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.vo.ContagemPorChave;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;

public interface OcorrenciaRepository {

    Ocorrencia save(Ocorrencia ocorrencia);

    Optional<Ocorrencia> findById(Long id);

    /** Listagem geral: apenas ocorrencias nao canceladas. */
    Pagina<Ocorrencia> findAtivas(PaginaRequest paginaRequest);

    Pagina<Ocorrencia> findByStatus(OcorrenciaEnum status, PaginaRequest paginaRequest);

    List<Ocorrencia> findByAlunoId(Long alunoId);

    long contarPorStatus(OcorrenciaEnum status);

    List<ContagemPorChave> contarPorCategoria();

    List<ContagemPorChave> contarPorTipo();

    List<ContagemPorChave> contarPorTurma();

    List<ContagemPorChave> contarPorAluno();
}
