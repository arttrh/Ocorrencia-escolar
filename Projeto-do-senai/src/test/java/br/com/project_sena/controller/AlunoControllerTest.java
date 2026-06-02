package br.com.project_sena.controller;

import br.com.project_sena.adapter.in.controller.AlunoController;
import br.com.project_sena.adapter.in.web.mapper.AlunoMapperDTO;
import br.com.project_sena.adapter.out.repository.persistence.UsuarioJpaRepository;
import br.com.project_sena.application.core.usecase.AlunoService;
import br.com.project_sena.config.security.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlunoController.class)
@AutoConfigureMockMvc(addFilters = false) //Desativa Spring security no teste
public class AlunoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AlunoService alunoService;

    @MockitoBean
    AlunoMapperDTO mapper;

    @MockitoBean
    TokenService token;

    @MockitoBean
    UsuarioJpaRepository usuarioJpaRepository;

    @Test
    void excluirUsuario() throws Exception {
        mockMvc.perform(delete("/aluno/1"))
                .andExpect(status().isNoContent());

        verify(alunoService).excluir(1L);
    }
}
