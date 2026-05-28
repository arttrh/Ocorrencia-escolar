package br.com.project_sena.application.core.service;

import br.com.project_sena.adapter.in.controller.request.LoginDTO;
import br.com.project_sena.application.port.out.UsuarioRepository;
import br.com.project_sena.exception.type.SenhaException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public AutenticacaoService(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return usuarioRepository.loadUserByUsername(login);
    }

    public void logar(LoginDTO dto) {
        UserDetails usuario = usuarioRepository.loadUserByUsername(dto.login());
        boolean senhaCorreta = encoder.matches(dto.password(), usuario.getPassword());

        if (!senhaCorreta) {
            throw new SenhaException("Senha invalida por favor digite novamente");
        }
    }
}
