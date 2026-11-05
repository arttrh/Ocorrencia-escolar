package br.com.project_sena;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.usecase.UsuarioService;
import br.com.project_sena.config.security.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FluxoApiTest {
    @Autowired MockMvc mvc;
    @Autowired UsuarioService usuarios;
    @Autowired TokenService tokens;
    @Autowired ObjectMapper json;
    private String token(PerfilEnum perfil) {
        return "Bearer "
            + tokens
                  .gerarToken(usuarios.cadastrar(
                      new Usuario("Teste", perfil + "@teste.com", "Senha-segura-123", perfil)))
                  .token();
    }
    private String criar(String url, String body, String token) throws Exception {
        return mvc
            .perform(post(url).header("Authorization", token).contentType("application/json").content(body))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    }
    @Test
    void fluxoCompleto() throws Exception {
        String auth = token(PerfilEnum.ADMIN);
        mvc.perform(post("/login")
                        .contentType("application/json")
                        .content("{\"email\":\"ADMIN@teste.com\",\"password\":\"Senha-segura-123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isString());
        long aluno = json.readTree(criar("/aluno/cadastrar",
                                       "{\"photo\":\"/static/images/"
                                       + "user-default.png\",\"name\":\"Ana\",\"dateBirth\":\"01/01/2010\"}",
                                       auth))
                         .get("id")
                         .asLong();
        long turma = json.readTree(criar("/turmas/cadastrar",
                                       "{\"className\":\"Turma "
                                       + "A\",\"turmaTurno\":\"MANHA\",\"classYear\":\"2026-01-01T00:00:00\","
                                       + "\"semestry\":\"2026-01-01T00:00:00\"}",
                                       auth))
                         .get("id")
                         .asLong();
        String ocorrencia = "{\"studentId\":" + aluno + ",\"classId\":" + turma
            + (",\"categoryId\":1,\"occurenceId\":1,\"date\":\"2026-01-01\",\"time\":\"08:00:00\","
               + "\"descriptionOccurrence\":\"Atraso na aula\"}");
        mvc.perform(post("/ocorrencias")
                        .header("Authorization", auth)
                        .contentType("application/json")
                        .content(ocorrencia))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/turmas/vincular/aluno/" + turma)
                        .header("Authorization", auth)
                        .contentType("application/json")
                        .content("{\"studentId\":" + aluno + "}"))
            .andExpect(status().isNoContent());
        mvc.perform(post("/turmas/vincular/aluno/" + turma)
                        .header("Authorization", auth)
                        .contentType("application/json")
                        .content("{\"studentId\":" + aluno + "}"))
            .andExpect(status().isBadRequest());
        long id = json.readTree(criar("/ocorrencias", ocorrencia, auth)).get("id").asLong();
        mvc.perform(get("/ocorrencias/" + id).header("Authorization", auth))
            .andExpect(jsonPath("$.student").value("Ana"));
        mvc.perform(put("/aluno/atualizar/" + aluno)
                        .header("Authorization", auth)
                        .contentType("application/json")
                        .content("{\"photo\":\"/static/images/user-default.png\",\"name\":\"Ana "
                                 + "Maria\",\"dateBirth\":\"01/01/2010\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(aluno))
            .andExpect(jsonPath("$.name").value("Ana Maria"));
        mvc.perform(get("/turmas/listar/ativo").header("Authorization", auth))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(patch("/ocorrencias/" + id + "/status")
                        .header("Authorization", auth)
                        .contentType("application/json")
                        .content("{\"status\":\"RESOLVIDAS\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("RESOLVIDAS"));
        mvc.perform(delete("/ocorrencias/" + id).header("Authorization", auth))
            .andExpect(status().isNoContent());
        mvc.perform(put("/ocorrencias/" + id)
                        .header("Authorization", auth)
                        .contentType("application/json")
                        .content(ocorrencia))
            .andExpect(status().isBadRequest());
        mvc.perform(delete("/aluno/delete/" + aluno).header("Authorization", auth))
            .andExpect(status().isNoContent());
        mvc.perform(patch("/aluno/reativar/" + aluno).header("Authorization", auth))
            .andExpect(status().isOk());
    }
    @Test
    void seguranca() throws Exception {
        mvc.perform(get("/health")).andExpect(status().isOk());
        mvc.perform(get("/aluno/ativos")).andExpect(status().isUnauthorized());
        mvc.perform(get("/aluno/ativos").header("Authorization", "Bearer invalido"))
            .andExpect(status().isUnauthorized());
        String auth = token(PerfilEnum.PROFESSOR);
        mvc.perform(post("/usuario/cadastrar")
                        .header("Authorization", auth)
                        .contentType("application/json")
                        .content("{}"))
            .andExpect(status().isForbidden());
        mvc.perform(get("/aluno/ativos").header("Authorization", auth)).andExpect(status().isForbidden());
        mvc.perform(get("/ocorrencias").header("Authorization", auth)).andExpect(status().isOk());
    }
}
