package br.com.project_sena.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.entity.UsuarioEntity;
import br.com.project_sena.application.core.domain.model.Usuario;

@Component
public class UsuarioMapperEntity {

    public Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getName(),
                entity.getLogin(),
                entity.getPassword(),
                entity.getPerfil(),
                entity.getStatus());
    }

    public UsuarioEntity toEntity(Usuario usuario) {
        return new UsuarioEntity(
                usuario.getId(),
                usuario.getName(),
                usuario.getLogin(),
                usuario.getPassword(),
                usuario.getPerfil(),
                usuario.getStatus());
    }
}
