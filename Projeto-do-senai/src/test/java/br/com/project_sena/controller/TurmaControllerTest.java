package br.com.project_sena.controller;

import br.com.project_sena.adapter.in.controller.TurmaController;
import br.com.project_sena.adapter.in.web.mapper.TurmaMapperDTO;
import br.com.project_sena.adapter.out.repository.persistence.UsuarioJpaRepository;
import br.com.project_sena.application.core.usecase.TurmaService;
import br.com.project_sena.config.security.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TurmaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TurmaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TurmaService service;

    @MockitoBean
    private TurmaMapperDTO mapper;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioJpaRepository usuarioJpaRepository;

    @Test
    @DisplayName("Deve carregar o contexto do controlador de turmas com sucesso")
    void contextLoads() throws Exception {
        // Como o controlador atual não possui endpoints expostos, validamos que o contexto carrega sem erros.
        assert mockMvc != null;
    }
}
