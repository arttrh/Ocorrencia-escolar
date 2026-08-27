package br.com.project_sena.application.port.in;

import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.port.in.command.AlterarPerfilCommand;
import br.com.project_sena.application.port.in.command.AlterarSenhaCommand;
import br.com.project_sena.application.port.in.command.AtualizarUsuarioCommand;
import br.com.project_sena.application.port.in.command.CadastrarUsuarioCommand;

public interface UsuarioUseCase {

    Usuario cadastrar(CadastrarUsuarioCommand command);

    Usuario buscar(Long id);

    Pagina<Usuario> listar(UsuarioEnum status, PaginaRequest paginaRequest);

    Usuario atualizar(AtualizarUsuarioCommand command);

    Usuario alterarPerfil(AlterarPerfilCommand command);

    void alterarSenha(AlterarSenhaCommand command);

    void inativar(Long id);

    Usuario reativar(Long id);
}
