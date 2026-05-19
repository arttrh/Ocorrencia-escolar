package br.com.project_sena.adapter.out.repository.persistence;

import br.com.project_sena.application.core.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UsuarioJpaRepository extends JpaRepository<Usuario,Long> {
    UserDetails loadUserByUsername(String login) throws UsernameNotFoundException;
}
