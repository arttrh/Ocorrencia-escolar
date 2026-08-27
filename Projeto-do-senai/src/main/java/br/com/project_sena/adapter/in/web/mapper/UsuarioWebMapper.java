package br.com.project_sena.adapter.in.web.mapper;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.in.web.dto.request.UserRegisterRequest;
import br.com.project_sena.adapter.in.web.dto.request.UserUpdateRequest;
import br.com.project_sena.adapter.in.web.dto.response.UserResponse;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.port.in.command.AtualizarUsuarioCommand;
import br.com.project_sena.application.port.in.command.CadastrarUsuarioCommand;

@Component
public class UsuarioWebMapper {

    public CadastrarUsuarioCommand toCommand(UserRegisterRequest request) {
        return new CadastrarUsuarioCommand(
                request.name(), request.login(), request.password(), request.role());
    }

    public AtualizarUsuarioCommand toCommand(UserUpdateRequest request) {
        return new AtualizarUsuarioCommand(
                request.id(), request.name(), request.login(), request.role());
    }

    /** A senha (mesmo o hash) nunca sai na resposta. */
    public UserResponse toResponse(Usuario usuario) {
        return new UserResponse(
                usuario.getId(),
                usuario.getName(),
                usuario.getLogin(),
                usuario.getPerfil(),
                usuario.isAtivo());
    }
}
