package br.com.project_sena.application.port.in;

import java.util.List;

import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.model.CategoriaOcorrencia;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.TipoOcorrencia;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.core.domain.vo.ResumoOcorrencias;
import br.com.project_sena.application.port.in.command.AlterarStatusOcorrenciaCommand;
import br.com.project_sena.application.port.in.command.AtualizarOcorrenciaCommand;
import br.com.project_sena.application.port.in.command.CadastrarOcorrenciaCommand;

public interface OcorrenciaUseCase {

    Ocorrencia cadastrar(CadastrarOcorrenciaCommand command);

    Ocorrencia buscar(Long id);

    Pagina<Ocorrencia> listar(PaginaRequest paginaRequest);

    Pagina<Ocorrencia> listarPorStatus(OcorrenciaEnum status, PaginaRequest paginaRequest);

    Ocorrencia atualizar(AtualizarOcorrenciaCommand command);

    Ocorrencia alterarStatus(AlterarStatusOcorrenciaCommand command);

    void cancelar(Long id);

    /** Historico de ocorrencias de um aluno, da mais recente para a mais antiga. */
    List<Ocorrencia> historicoDoAluno(Long alunoId);

    ResumoOcorrencias resumo();

    List<CategoriaOcorrencia> listarCategorias();

    List<TipoOcorrencia> listarTiposPorCategoria(String nomeCategoria);
}
