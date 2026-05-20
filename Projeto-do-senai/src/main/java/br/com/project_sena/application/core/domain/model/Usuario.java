package br.com.project_sena.application.core.domain.model;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class Usuario implements UserDetails {
    private Long id;
    private String name;
    private String password;
    private String login;
    private boolean active = true;
    private PerfilEnum perfil;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
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

    public Usuario(Long id, String name, String password, String login, boolean active, PerfilEnum perfil) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.login = login;
        this.active = active;
        this.perfil = perfil;
    }

    public Usuario() {

    }

    public Long getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public PerfilEnum getPerfil() {
        return perfil;
    }
}
