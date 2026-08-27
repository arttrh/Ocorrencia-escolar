package br.com.project_sena.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.integration.TesteDeIntegracao;

/**
 * Verifica que a API entrega exatamente o que os scripts do front-end leem: os mesmos
 * caminhos, os mesmos nomes de campo e os mesmos formatos de data.
 *
 * <p>E' o teste que impede a volta do descasamento que existia — o front chamava
 * {@code /users}, {@code /students}, {@code /schoolclasses} e {@code /incidents} enquanto
 * o back publicava {@code /usuario}, {@code /aluno} e {@code /turmas}, e nenhuma tela
 * funcionava.</p>
 */
@DisplayName("Contrato consumido pelo front-end (integracao)")
class ContratoDoFrontEndIT extends TesteDeIntegracao {

    @Nested
    @DisplayName("POST /login")
    class Login {

        /**
         * O {@code login.js} envia {@code {login, password}} e le {@code data.tokenJWT},
         * {@code data.id} e {@code data.login}. O contrato anterior recebia {@code email}
         * e devolvia {@code token}: os dois lados erravam o nome do campo.
         */
        @Test
        @DisplayName("aceita {login, password} e devolve {tokenJWT, id, login, role}")
        void contratoDoLogin() throws Exception {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"login":"%s","password":"%s"}"""
                                    .formatted(loginDe(PerfilEnum.ADMIN), SENHA_PADRAO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tokenJWT").isNotEmpty())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.login").value(loginDe(PerfilEnum.ADMIN)))
                    .andExpect(jsonPath("$.role").value("ADMIN"));
        }
    }

    @Nested
    @DisplayName("/users")
    class Usuarios {

        @Test
        @DisplayName("POST aceita {name, login, password, role} como o cadastro do front")
        void cadastra() throws Exception {
            mockMvc.perform(post("/users")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMIN))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Carla","login":"carla@escola.com",
                                     "password":"senha123","role":"ANALISTA"}"""))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Carla"))
                    .andExpect(jsonPath("$.login").value("carla@escola.com"))
                    .andExpect(jsonPath("$.role").value("ANALISTA"))
                    .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        @DisplayName("GET /users/roles devolve lista de textos para o <select>")
        void perfisComoTexto() throws Exception {
            mockMvc.perform(get("/users/roles")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMIN)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").isString())
                    .andExpect(jsonPath("$", Matchers.hasItem("ADMIN")));
        }

        @Test
        @DisplayName("GET lista com as colunas id, name e role")
        void listaComColunasDaTabela() throws Exception {
            mockMvc.perform(get("/users")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMIN))
                            .param("page", "0").param("size", "10").param("sort", "role"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").isNumber())
                    .andExpect(jsonPath("$.content[0].name").isString())
                    .andExpect(jsonPath("$.content[0].role").isString());
        }

        @Test
        @DisplayName("PUT faz atualizacao parcial no formato do front")
        void atualizaParcial() throws Exception {
            Long id = usuarioRepository.findByLogin(loginDe(PerfilEnum.ANALISTA))
                    .orElseThrow().getId();

            mockMvc.perform(put("/users")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMIN))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"id":%d,"name":"Nome Novo"}""".formatted(id)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Nome Novo"))
                    .andExpect(jsonPath("$.login").value(loginDe(PerfilEnum.ANALISTA)));
        }

        @Test
        @DisplayName("PATCH /users altera o perfil de acesso")
        void alteraPerfil() throws Exception {
            Long id = usuarioRepository.findByLogin(loginDe(PerfilEnum.PROFESSOR))
                    .orElseThrow().getId();

            mockMvc.perform(patch("/users")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMIN))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"id":%d,"role":"COORDENADOR"}""".formatted(id)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("COORDENADOR"));
        }

        @Test
        @DisplayName("DELETE inativa e o usuario aparece na listagem de inativos")
        void inativa() throws Exception {
            Long id = usuarioRepository.findByLogin(loginDe(PerfilEnum.PROFESSOR))
                    .orElseThrow().getId();

            mockMvc.perform(delete("/users/" + id)
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMIN)))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/users/inactive")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMIN)))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("POST recusa login ja usado com 400")
        void recusaLoginDuplicado() throws Exception {
            mockMvc.perform(post("/users")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMIN))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Outro","login":"%s",
                                     "password":"senha123","role":"ANALISTA"}"""
                                    .formatted(loginDe(PerfilEnum.ADMIN))))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("/students")
    class Alunos {

        @Test
        @DisplayName("POST aceita a data no formato dd/MM/yyyy que o front monta")
        void cadastraComDataBrasileira() throws Exception {
            mockMvc.perform(post("/students")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Bruno Lima","birthDate":"10/05/2007"}"""))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Bruno Lima"))
                    .andExpect(jsonPath("$.birthDate").value("10/05/2007"))
                    .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        @DisplayName("POST recusa data de nascimento no futuro")
        void recusaNascimentoNoFuturo() throws Exception {
            mockMvc.perform(post("/students")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Do Futuro","birthDate":"01/01/2099"}"""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET lista com as colunas id, name e imageUrl")
        void listaComColunasDaTabela() throws Exception {
            mockMvc.perform(get("/students")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").isNumber())
                    .andExpect(jsonPath("$.content[0].name").value("Ana Souza"));
        }

        /**
         * Regressao: a versao anterior carregava o aluno, ignorava o resultado e salvava
         * o objeto vindo do DTO, o que apagava foto e status a cada edicao.
         */
        @Test
        @DisplayName("PUT parcial preserva os campos nao enviados")
        void atualizacaoParcialPreservaCampos() throws Exception {
            mockMvc.perform(put("/students")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"id":%d,"name":"Ana Souza Silva"}""".formatted(aluno.getId())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Ana Souza Silva"))
                    .andExpect(jsonPath("$.birthDate").value("27/03/2008"))
                    .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        @DisplayName("PATCH /{id}/image aceita multipart com o campo image")
        void enviaFoto() throws Exception {
            MockMultipartFile arquivo = new MockMultipartFile(
                    "image", "foto.png", "image/png", new byte[]{1, 2, 3, 4});

            mockMvc.perform(multipart(HttpMethod.PATCH, "/students/" + aluno.getId() + "/image")
                            .file(arquivo)
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.imageUrl").value(
                            Matchers.startsWith("data:image/png;base64,")));
        }

        @Test
        @DisplayName("PATCH /{id}/image recusa tipo de arquivo nao permitido")
        void recusaArquivoNaoImagem() throws Exception {
            MockMultipartFile arquivo = new MockMultipartFile(
                    "image", "script.svg", "image/svg+xml", "<svg onload=alert(1)>".getBytes());

            mockMvc.perform(multipart(HttpMethod.PATCH, "/students/" + aluno.getId() + "/image")
                            .file(arquivo)
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("/schoolclasses")
    class Turmas {

        @Test
        @DisplayName("POST aceita {name, shift, year, semester}")
        void cadastra() throws Exception {
            mockMvc.perform(post("/schoolclasses")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"DS-02","shift":"TARDE","year":2026,"semester":"SEGUNDO"}"""))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("DS-02"))
                    .andExpect(jsonPath("$.shift").value("TARDE"))
                    .andExpect(jsonPath("$.year").value(2026))
                    .andExpect(jsonPath("$.semester").value("SEGUNDO"))
                    .andExpect(jsonPath("$.canceled").value(false));
        }

        @Test
        @DisplayName("GET /shifts e /semesters alimentam os selects do front")
        void catalogos() throws Exception {
            mockMvc.perform(get("/schoolclasses/shifts")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", Matchers.hasItem("MANHA")));

            mockMvc.perform(get("/schoolclasses/semesters")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", Matchers.hasItem("PRIMEIRO")));
        }

        /**
         * Regressao: no controller anterior {@code /turmas/listar/inativo} devolvia as
         * turmas ativas e {@code /listar/ativo} devolvia as canceladas.
         */
        @Test
        @DisplayName("GET lista ativas e /canceled lista canceladas — sem inversao")
        void listagensNaoInvertidas() throws Exception {
            mockMvc.perform(get("/schoolclasses")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO)))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.content[0].canceled").value(false));

            mockMvc.perform(delete("/schoolclasses/" + turma.getId())
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO)))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/schoolclasses")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO)))
                    .andExpect(jsonPath("$.totalElements").value(0));

            mockMvc.perform(get("/schoolclasses/canceled")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO)))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.content[0].canceled").value(true));
        }

        /**
         * Regressao: o endpoint de vinculo anterior era declarado sem parametros e tinha
         * corpo {@code return null} — a matricula nunca acontecia.
         */
        @Test
        @DisplayName("POST /{id}/students matricula o aluno de verdade")
        void matriculaAluno() throws Exception {
            String novoAluno = mockMvc.perform(post("/students")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Bruno Lima","birthDate":"10/05/2007"}"""))
                    .andReturn().getResponse().getContentAsString();
            long idDoAluno = objectMapper.readTree(novoAluno).get("id").asLong();

            mockMvc.perform(post("/schoolclasses/" + turma.getId() + "/students")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"studentId":%d}""".formatted(idDoAluno)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("POST /{id}/students recusa aluno ja matriculado em outra turma")
        void recusaAlunoDeOutraTurma() throws Exception {
            String outraTurma = mockMvc.perform(post("/schoolclasses")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"DS-02","shift":"TARDE","year":2026,"semester":"SEGUNDO"}"""))
                    .andReturn().getResponse().getContentAsString();
            long idDaOutra = objectMapper.readTree(outraTurma).get("id").asLong();

            mockMvc.perform(post("/schoolclasses/" + idDaOutra + "/students")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"studentId":%d}""".formatted(aluno.getId())))
                    .andExpect(status().isBadRequest());
        }
    }
}
