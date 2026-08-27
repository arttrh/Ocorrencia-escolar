package br.com.project_sena.application.port.in;

import java.util.List;

import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.port.in.command.AtualizarTurmaCommand;
import br.com.project_sena.application.port.in.command.CadastrarTurmaCommand;
import br.com.project_sena.application.port.in.command.VincularAlunoCommand;

public interface TurmaUseCase {

    Turma cadastrar(CadastrarTurmaCommand command);

    Turma buscar(Long id);

    Pagina<Turma> listar(TurmaEnum status, PaginaRequest paginaRequest);

    Turma atualizar(AtualizarTurmaCommand command);

    void cancelar(Long id);

    Turma reativar(Long id);

    void vincularAluno(VincularAlunoCommand command);

    List<Aluno> listarAlunos(Long turmaId);
}
