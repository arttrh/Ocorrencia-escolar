package br.com.project_sena.controller;

import br.com.project_sena.adapter.in.controller.UsuarioController;
import br.com.project_sena.adapter.in.controller.request.UserRegisterDTO;
import br.com.project_sena.adapter.in.controller.request.UserUpdateDTO;
import br.com.project_sena.adapter.in.controller.response.UserDetailsDTO;
import br.com.project_sena.adapter.in.controller.response.UserListAtivosDTO;
import br.com.project_sena.adapter.in.controller.response.UserListInativosDTO;
import br.com.project_sena.adapter.in.web.mapper.UsuarioMapperDTO;
import br.com.project_sena.adapter.out.repository.persistence.UsuarioJpaRepository;
import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.usecase.UsuarioService;
import br.com.project_sena.config.security.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioMapperDTO mapper;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioJpaRepository usuarioJpaRepository;

    @Test
    @DisplayName("Deve cadastrar um usuário com sucesso")
    void deveCadastrarUsuarioComSucesso() throws Exception {
        String requestBody = """
                {
                    "name": "Arthur Lucas",
                    "email": "arthur@email.com",
                    "password": "senha123",
                    "perfil": "ADMIN"
                }
                """;

        Usuario usuario = new Usuario("Arthur Lucas", "arthur@email.com", "senha123", PerfilEnum.ADMIN);
        Usuario usuarioSalvo = new Usuario(1L, "Arthur Lucas", "arthur@email.com", "senha123", PerfilEnum.ADMIN, UsuarioEnum.ATIVO);
        UserDetailsDTO responseDto = new UserDetailsDTO(1L, "Arthur Lucas", "arthur@email.com", PerfilEnum.ADMIN, UsuarioEnum.ATIVO);

        when(mapper.toDomain(any(UserRegisterDTO.class))).thenReturn(usuario);
        when(usuarioService.cadastrar(any(Usuario.class))).thenReturn(usuarioSalvo);
        when(mapper.toDTO(any(Usuario.class))).thenReturn(responseDto);

        mockMvc.perform(post("/usuario/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(usuarioService, times(1)).cadastrar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao cadastrar usuário com dados inválidos")
    void deveRetornar400AoCadastrarUsuarioInvalido() throws Exception {
        String requestBody = """
                {
                    "name": "",
                    "email": "email_invalido",
                    "password": "",
                    "perfil": null
                }
                """;

        mockMvc.perform(post("/usuario/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).cadastrar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve listar usuários ativos com sucesso")
    void deveListarUsuariosAtivosComSucesso() throws Exception {
        Usuario usuario = new Usuario(1L, "Arthur Lucas", "arthur@email.com", "senha123", PerfilEnum.ADMIN, UsuarioEnum.ATIVO);
        Page<Usuario> page = new PageImpl<>(List.of(usuario));
        UserListAtivosDTO listDto = new UserListAtivosDTO(1L, "Arthur Lucas", "arthur@email.com", PerfilEnum.ADMIN);

        when(usuarioService.listar(any(Pageable.class))).thenReturn(page);
        when(mapper.toList(any(Usuario.class))).thenReturn(listDto);

        mockMvc.perform(get("/usuario/ativos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(usuarioService, times(1)).listar(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar usuários inativos com sucesso")
    void deveListarUsuariosInativosComSucesso() throws Exception {
        Usuario usuario = new Usuario(1L, "Arthur Lucas", "arthur@email.com", "senha123", PerfilEnum.ADMIN, UsuarioEnum.INVATIVO);
        Page<Usuario> page = new PageImpl<>(List.of(usuario));
        UserListInativosDTO listDto = new UserListInativosDTO(1L, "Arthur Lucas", "arthur@email.com", PerfilEnum.ADMIN);

        when(usuarioService.listarInvativos(any(Pageable.class))).thenReturn(page);
        when(mapper.toListInativo(any(Usuario.class))).thenReturn(listDto);

        mockMvc.perform(get("/usuario/inativos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(usuarioService, times(1)).listarInvativos(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve detalhar usuário com sucesso")
    void deveDetalharUsuarioComSucesso() throws Exception {
        Usuario usuario = new Usuario(1L, "Arthur Lucas", "arthur@email.com", "senha123", PerfilEnum.ADMIN, UsuarioEnum.ATIVO);
        UserDetailsDTO responseDto = new UserDetailsDTO(1L, "Arthur Lucas", "arthur@email.com", PerfilEnum.ADMIN, UsuarioEnum.ATIVO);

        when(usuarioService.buscar(1L)).thenReturn(usuario);
        when(mapper.toDTO(usuario)).thenReturn(responseDto);

        mockMvc.perform(get("/usuario/1"))
                .andExpect(status().isOk());

        verify(usuarioService, times(1)).buscar(1L);
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() throws Exception {
        String requestBody = """
                {
                    "email": "arthur.novo@email.com",
                    "password": "novasenha123",
                    "perfil": "ADMIN"
                }
                """;

        Usuario usuario = new Usuario(null, null, "arthur.novo@email.com", "novasenha123", PerfilEnum.ADMIN, null);
        Usuario usuarioAtualizado = new Usuario(1L, "Arthur Lucas", "arthur.novo@email.com", "novasenha123", PerfilEnum.ADMIN, UsuarioEnum.ATIVO);
        UserDetailsDTO responseDto = new UserDetailsDTO(1L, "Arthur Lucas", "arthur.novo@email.com", PerfilEnum.ADMIN, UsuarioEnum.ATIVO);

        when(mapper.toDomainUpdate(any(UserUpdateDTO.class))).thenReturn(usuario);
        when(usuarioService.atualizar(any(Usuario.class), eq(1L))).thenReturn(usuarioAtualizado);
        when(mapper.toDTO(any(Usuario.class))).thenReturn(responseDto);

        mockMvc.perform(put("/usuario/atualizar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(usuarioService, times(1)).atualizar(any(Usuario.class), eq(1L));
    }

    @Test
    @DisplayName("Deve excluir (inativar) usuário com sucesso")
    void deveExcluirUsuarioComSucesso() throws Exception {
        doNothing().when(usuarioService).excluir(1L);

        mockMvc.perform(delete("/usuario/delete/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).excluir(1L);
    }

    @Test
    @DisplayName("Deve reativar usuário com sucesso")
    void deveReativarUsuarioComSucesso() throws Exception {
        doNothing().when(usuarioService).reativar(1L);

        mockMvc.perform(patch("/usuario/reativar/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).reativar(1L);
    }
}
