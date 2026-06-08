package br.com.project_sena.adapter.in.web.mapper;

import br.com.project_sena.adapter.in.controller.request.usuario.UserRegisterDTO;
import br.com.project_sena.adapter.in.controller.request.usuario.UserUpdateDTO;
import br.com.project_sena.adapter.in.controller.response.UserDetailsDTO;
import br.com.project_sena.adapter.in.controller.response.UserListAtivosDTO;
import br.com.project_sena.adapter.in.controller.response.UserListInativosDTO;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapperDTO {

    // Front-End vai mandar um JSON e vai ser convertido para domain.
    public Usuario toDomain(UserRegisterDTO dto){
        return new Usuario(
                dto.name(),
                dto.email(),
                dto.password(),
                dto.perfil()
        );
    }

    // Vou devolver o domain para o usuario mas em json
    public UserDetailsDTO toDTO(Usuario usuario){
        return new UserDetailsDTO(
                usuario.getId(),
                usuario.getName(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getUsuarioEnum()
        );
    }

    // Devolvendo a lista para o Usuarios Ativos
    public UserListAtivosDTO toList(Usuario list){
        return new UserListAtivosDTO(
                list.getId(),
                list.getName(),
                list.getEmail(),
                list.getPerfil(),
                list.getUsuarioEnum()
        );
    }

    // Devolve a lista para Usuarios Inativos
    public UserListInativosDTO toListInativo(Usuario list){
        return new UserListInativosDTO(
                list.getId(),
                list.getName(),
                list.getEmail(),
                list.getUsuarioEnum(),
                list.getPerfil()
        );
    }

    public Usuario toDomainUpdate(UserUpdateDTO dto){
      return new Usuario(
              null,
              null,
              dto.email(),
              dto.password(),
              dto.perfil(),
              UsuarioEnum.ATIVO
      );
    }
}
