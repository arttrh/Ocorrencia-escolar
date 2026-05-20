package br.com.project_sena.adapter.in.web.mapper;

import br.com.project_sena.adapter.in.controller.request.UserRegisterDTO;
import br.com.project_sena.adapter.in.controller.response.UserDetailsDTO;
import br.com.project_sena.adapter.in.controller.response.UserListDTO;
import br.com.project_sena.application.core.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapperDTO {

    // Front-End vai mandar um JSON e vai ser convertido para domain.
    public Usuario toDomain(UserRegisterDTO dto){
        return new Usuario(
                null,
                dto.name(),
                dto.password(),
                dto.login(),
                true,
                dto.perfil()
        );
    }

    // Vou devolver o domain para o usuario mas em json
    public UserDetailsDTO toDTO(Usuario usuario){
        return new UserDetailsDTO(
                usuario.getId(),
                usuario.getName(),
                usuario.getLogin(),
                usuario.getPerfil()
        );
    }

    public UserListDTO toList(Usuario list){
        return new UserListDTO(
                list.getId(),
                list.getName(),
                list.isActive(),
                list.getPerfil()
        );
    }

}
