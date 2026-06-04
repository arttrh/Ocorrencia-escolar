package br.com.project_sena.controller;

import br.com.project_sena.adapter.in.controller.LoginController;
import br.com.project_sena.adapter.in.controller.request.EmailDTO;
import br.com.project_sena.adapter.out.repository.persistence.UsuarioJpaRepository;
import br.com.project_sena.application.core.service.AutenticacaoService;
import br.com.project_sena.config.security.rateLimit.RateLimitConfig;
import br.com.project_sena.config.security.rateLimit.RateLimitService;
import br.com.project_sena.config.security.service.TokenDTO;
import br.com.project_sena.config.security.service.TokenService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutenticacaoService autenticacaoService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioJpaRepository usuarioJpaRepository;

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void deveRealizarLoginComSucesso() throws Exception {
        String requestBody = """
                {
                    "email": "admin@email.com",
                    "password": "senha123"
                }
                """;

        Bucket bucket = RateLimitConfig.novoBucket();
        TokenDTO responseToken = new TokenDTO("mocked_jwt_token");

        when(rateLimitService.getBucket(anyString())).thenReturn(bucket);
        when(autenticacaoService.logar(any(EmailDTO.class))).thenReturn(responseToken);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(rateLimitService, times(1)).getBucket(anyString());
        verify(autenticacaoService, times(1)).logar(any(EmailDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar logar com credenciais em branco")
    void deveRetornar400AoLogarComDadosInvalidos() throws Exception {
        String requestBody = """
                {
                    "email": "",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(rateLimitService, never()).getBucket(anyString());
        verify(autenticacaoService, never()).logar(any(EmailDTO.class));
    }
}
