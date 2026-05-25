package br.com.project_sena.adapter.out.repository.mapper;

import br.com.project_sena.adapter.out.repository.entity.UsuarioEntity;
import br.com.project_sena.application.core.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapperEntity {
    public Usuario toDomain(UsuarioEntity entity){
        return new Usuario(
                entity.getId(),
                entity.getName(),
                entity.getPassword(),
                entity.getLogin(),
                entity.getPerfil(),
                entity.getUsuarioEnum()
        );
    }

    public UsuarioEntity toEntity(Usuario usuario){
        return new UsuarioEntity(
                usuario.getId(),
                usuario.getName(),
                usuario.getPassword(),
                usuario.getLogin(),
                usuario.getPerfil(),
                usuario.getUsuarioEnum()
        );
    }
}
