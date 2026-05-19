package br.com.project_sena.application.port.out;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UsuarioRepository {
    UserDetails loadUserByUsername(String login) throws UsernameNotFoundException;
}
