package br.com.project_sena.application.core.service;

import br.com.project_sena.adapter.in.controller.request.LoginDTO;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.port.out.UsuarioRepository;
import br.com.project_sena.config.security.service.TokenDTO;
import br.com.project_sena.config.security.service.TokenService;
import br.com.project_sena.exception.type.SenhaException;
import br.com.project_sena.exception.type.UsuarioNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

@Service
public class AutenticacaoService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final TokenService service;

    public AutenticacaoService(UsuarioRepository usuarioRepository, PasswordEncoder encoder, TokenService service) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email);
    }

    public TokenDTO logar(LoginDTO dto) throws LoginException {
        try {
            Usuario usuario = usuarioRepository.findByEmail(dto.email());
            boolean senhaCorreta = encoder.matches(dto.password(), usuario.getPassword());
            if (!senhaCorreta) {
                throw new SenhaException("Senha invalida por favor digite novamente");
            }
            return service.gerarToken(usuario);

        } catch (UsuarioNotFoundException ex) {
            throw new LoginException("Usuario Nao encontrado por esse Login");
        }
    }
}
