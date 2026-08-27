package br.com.project_sena.adapter.in.web.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.project_sena.application.core.domain.model.Usuario;

/**
 * Adapta o {@link Usuario} do dominio para o contrato do Spring Security.
 *
 * <p>Antes o proprio modelo de dominio implementava {@code UserDetails} — o nucleo
 * dependia do framework, que e' exatamente a seta que a arquitetura hexagonal proibe.
 * A ponte agora vive no adaptador de entrada — que e' onde ela pertence: este objeto
 * representa <em>quem esta chamando</em> a API, e nao uma dependencia externa que a
 * aplicacao invoca.</p>
 */
public record UsuarioPrincipal(Usuario usuario) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getLogin();
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
        return usuario.isAtivo();
    }

    public Long getId() {
        return usuario.getId();
    }
}
