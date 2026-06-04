package br.com.project_sena.autenticacao;

import br.com.project_sena.adapter.in.controller.request.EmailDTO;
import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.service.AutenticacaoService;
import br.com.project_sena.application.port.out.UsuarioRepository;
import br.com.project_sena.config.security.service.TokenDTO;
import br.com.project_sena.config.security.service.TokenService;
import br.com.project_sena.exception.type.EmailException;
import br.com.project_sena.exception.type.SenhaException;
import br.com.project_sena.exception.type.UsuarioNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder encoder; // Wait, org.springframework.security.crypto.password.PasswordEncoder is imported as PasswordEncoder

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    // Helper method to import password encoder class manually in mock
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve carregar o usuário pelo username (email) com sucesso")
    void deveCarregarUsuarioPeloUsernameComSucesso() {
        Usuario usuario = new Usuario(1L, "Admin", "admin@email.com", "senha123", PerfilEnum.ADMIN, UsuarioEnum.ATIVO);
        when(usuarioRepository.findByEmail("admin@email.com")).thenReturn(usuario);

        UserDetails resultado = autenticacaoService.loadUserByUsername("admin@email.com");

        assertNotNull(resultado);
        assertEquals("admin@email.com", resultado.getUsername());
        verify(usuarioRepository, times(1)).findByEmail("admin@email.com");
    }

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar TokenDTO")
    void deveRealizarLoginComSucesso() throws EmailException {
        EmailDTO loginDto = new EmailDTO("admin@email.com", "senha123");
        Usuario usuario = new Usuario(1L, "Admin", "admin@email.com", "hashed_senha", PerfilEnum.ADMIN, UsuarioEnum.ATIVO);
        TokenDTO tokenDto = new TokenDTO("mocked_jwt_token");

        when(usuarioRepository.findByEmail("admin@email.com")).thenReturn(usuario);
        when(encoder.matches("senha123", "hashed_senha")).thenReturn(true);
        when(tokenService.gerarToken(usuario)).thenReturn(tokenDto);

        TokenDTO resultado = autenticacaoService.logar(loginDto);

        assertNotNull(resultado);
        assertEquals("mocked_jwt_token", resultado.token());
        verify(usuarioRepository, times(1)).findByEmail("admin@email.com");
        verify(encoder, times(1)).matches("senha123", "hashed_senha");
        verify(tokenService, times(1)).gerarToken(usuario);
    }

    @Test
    @DisplayName("Deve lançar SenhaException ao logar com senha inválida")
    void deveLancarExceptionAoLogarComSenhaInvalida() {
        EmailDTO loginDto = new EmailDTO("admin@email.com", "senha_errada");
        Usuario usuario = new Usuario(1L, "Admin", "admin@email.com", "hashed_senha", PerfilEnum.ADMIN, UsuarioEnum.ATIVO);

        when(usuarioRepository.findByEmail("admin@email.com")).thenReturn(usuario);
        when(encoder.matches("senha_errada", "hashed_senha")).thenReturn(false);

        assertThrows(SenhaException.class, () -> autenticacaoService.logar(loginDto));
        verify(usuarioRepository, times(1)).findByEmail("admin@email.com");
        verify(encoder, times(1)).matches("senha_errada", "hashed_senha");
        verify(tokenService, never()).gerarToken(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar EmailException se o usuário não for encontrado")
    void deveLancarExceptionAoLogarComUsuarioNaoEncontrado() {
        EmailDTO loginDto = new EmailDTO("inexistente@email.com", "senha123");

        when(usuarioRepository.findByEmail("inexistente@email.com")).thenThrow(new UsuarioNotFoundException("Usuario nao encontrado"));

        assertThrows(EmailException.class, () -> autenticacaoService.logar(loginDto));
        verify(usuarioRepository, times(1)).findByEmail("inexistente@email.com");
        verify(encoder, never()).matches(anyString(), anyString());
    }
}
