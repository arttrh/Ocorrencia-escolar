package br.com.project_sena.application.core.domain.model;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class Usuario implements UserDetails {
    private Long id;
    private String name;
    private String email;
    private String password;

    //Enums
    private UsuarioEnum usuarioEnum = UsuarioEnum.ATIVO;
    private PerfilEnum perfil;

    public Usuario(Long id, String name, String email, String password, PerfilEnum perfil, UsuarioEnum usuarioEnum) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.perfil = perfil;
        this.usuarioEnum = usuarioEnum;
    }

    public Usuario(String name, String email, String password, PerfilEnum perfil) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.perfil = perfil;
    }

    //UserDetails
    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public PerfilEnum getPerfil() {
        return perfil;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPerfil(PerfilEnum perfil) {
        this.perfil = perfil;
    }

    public UsuarioEnum getUsuarioEnum() {
        return usuarioEnum;
    }

    public void setUsuarioEnum(UsuarioEnum usuarioEnum) {
        this.usuarioEnum = usuarioEnum;
    }

    public void atualizarUsuario(Usuario usuario) {
        if (usuario.getPerfil() != null){
            this.perfil = usuario.getPerfil();
        }
        if (usuario.getEmail() != null && !usuario.getEmail().isBlank()){
            this.email = usuario.getEmail();
        }

        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()){
            this.password = usuario.getPassword();
        }
    }

    public void excluir(UsuarioEnum usuarioEnum) {
        this.usuarioEnum = UsuarioEnum.INVATIVO;
    }

    public void reativar(UsuarioEnum usuarioEnum){
        this.usuarioEnum = UsuarioEnum.ATIVO;
    }

}
