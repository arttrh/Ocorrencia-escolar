package br.com.project_sena.application.port.in;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.port.in.command.AlterarFotoAlunoCommand;
import br.com.project_sena.application.port.in.command.AtualizarAlunoCommand;
import br.com.project_sena.application.port.in.command.CadastrarAlunoCommand;

public interface AlunoUseCase {

    Aluno cadastrar(CadastrarAlunoCommand command);

    Aluno buscar(Long id);

    Pagina<Aluno> listar(AlunoEnum status, PaginaRequest paginaRequest);

    Aluno atualizar(AtualizarAlunoCommand command);

    Aluno alterarFoto(AlterarFotoAlunoCommand command);

    void inativar(Long id);

    Aluno reativar(Long id);
}
