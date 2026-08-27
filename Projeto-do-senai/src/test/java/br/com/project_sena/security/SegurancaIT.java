package br.com.project_sena.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.integration.TesteDeIntegracao;

/**
 * Testes de seguranca da API.
 *
 * <p>Cobrem autenticacao, autorizacao por perfil, rate limit de login, CORS e vazamento
 * de informacao nas respostas de erro. Varios destes cenarios reproduzem falhas que
 * existiam na versao anterior e por isso valem como teste de regressao.</p>
 */
@DisplayName("Seguranca (integracao)")
class SegurancaIT extends TesteDeIntegracao {

    @Nested
    @DisplayName("autenticacao")
    class Autenticacao {

        @Test
        @DisplayName("recurso protegido sem token responde 401 em JSON")
        void semTokenEh401() throws Exception {
            mockMvc.perform(get("/incidents"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(401));
        }

        /**
         * Regressao: o filtro anterior tratava a string {@code "Token invalido"} como se
         * fosse um login e chamava {@code orElseThrow()}, devolvendo 500.
         */
        @Test
        @DisplayName("token adulterado responde 401, nunca 500")
        void tokenAdulteradoEh401() throws Exception {
            mockMvc.perform(get("/incidents")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer nao.e.um.jwt"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("token assinado com outro segredo e' recusado")
        void tokenDeOutroEmissorEh401() throws Exception {
            String forjado = "eyJhbGciOiJIUzI1NiJ9."
                    + "eyJpc3MiOiJTaXN0ZW1hLU9jb3JyZW5jaWEtRXNjb2xhciIsInN1YiI6ImFkbWluQGVzY29sYS5jb20ifQ."
                    + "assinatura-invalida";

            mockMvc.perform(get("/incidents")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + forjado))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("cabecalho Authorization sem o prefixo Bearer nao autentica")
        void semPrefixoBearer() throws Exception {
            mockMvc.perform(get("/incidents")
                            .header(HttpHeaders.AUTHORIZATION, token(PerfilEnum.ADMIN)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("usuario inativado nao consegue mais usar o token que ja tinha")
        void tokenDeUsuarioInativado() throws Exception {
            String autorizacao = autorizacao(PerfilEnum.COORDENADOR);
            mockMvc.perform(get("/incidents").header(HttpHeaders.AUTHORIZATION, autorizacao))
                    .andExpect(status().isOk());

            var usuario = usuarioRepository.findByLogin(loginDe(PerfilEnum.COORDENADOR)).orElseThrow();
            usuario.setStatus(UsuarioEnum.INATIVO);
            usuarioRepository.save(usuario);

            mockMvc.perform(get("/incidents").header(HttpHeaders.AUTHORIZATION, autorizacao))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("login com senha errada responde 401 sem revelar se a conta existe")
        void senhaErradaNaoRevelaConta() throws Exception {
            String mensagemContaReal = mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"login":"admin@escola.com","password":"errada"}"""))
                    .andExpect(status().isUnauthorized())
                    .andReturn().getResponse().getContentAsString();

            String mensagemContaFalsa = mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"login":"naoexiste@escola.com","password":"errada"}"""))
                    .andExpect(status().isUnauthorized())
                    .andReturn().getResponse().getContentAsString();

            org.junit.jupiter.api.Assertions.assertEquals(
                    objectMapper.readTree(mensagemContaReal).get("message").asText(),
                    objectMapper.readTree(mensagemContaFalsa).get("message").asText(),
                    "mensagens diferentes permitem enumerar contas validas");
        }

        @Test
        @DisplayName("a resposta do login nao devolve o hash da senha")
        void loginNaoVazaSenha() throws Exception {
            String resposta = mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"login":"admin@escola.com","password":"%s"}"""
                                    .formatted(SENHA_PADRAO)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            org.junit.jupiter.api.Assertions.assertFalse(
                    resposta.toLowerCase().contains("password")
                            || resposta.contains("$2a$"),
                    "a resposta nao pode conter senha nem hash: " + resposta);
        }

        @Test
        @DisplayName("nenhum endpoint de usuario devolve a senha")
        void endpointsNaoVazamSenha() throws Exception {
            String resposta = mockMvc.perform(get("/users")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMIN)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            org.junit.jupiter.api.Assertions.assertFalse(resposta.contains("$2a$"),
                    "hash de senha vazou na listagem de usuarios");
        }
    }

    @Nested
    @DisplayName("autorizacao por perfil")
    class Autorizacao {

        @Test
        @DisplayName("perfil sem permissao recebe 403 em JSON")
        void perfilSemPermissaoEh403() throws Exception {
            mockMvc.perform(get("/users")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.PROFESSOR)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(403));
        }

        @Test
        @DisplayName("apenas ADMIN administra usuarios")
        void apenasAdminAdministraUsuarios() throws Exception {
            for (PerfilEnum perfil : PerfilEnum.values()) {
                var resultado = mockMvc.perform(get("/users")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(perfil)));
                if (perfil == PerfilEnum.ADMIN) {
                    resultado.andExpect(status().isOk());
                } else {
                    resultado.andExpect(status().isForbidden());
                }
            }
        }

        @Test
        @DisplayName("professor le ocorrencias mas nao cancela")
        void professorNaoCancelaOcorrencia() throws Exception {
            mockMvc.perform(get("/incidents")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.PROFESSOR)))
                    .andExpect(status().isOk());

            mockMvc.perform(delete("/incidents/1")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.PROFESSOR)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("professor le turmas mas nao cadastra")
        void professorNaoCadastraTurma() throws Exception {
            mockMvc.perform(get("/schoolclasses")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.PROFESSOR)))
                    .andExpect(status().isOk());

            mockMvc.perform(post("/schoolclasses")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.PROFESSOR))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"DS-09","shift":"MANHA","year":2026,"semester":"PRIMEIRO"}"""))
                    .andExpect(status().isForbidden());
        }

        /**
         * Sem esta checagem, qualquer usuario autenticado sobrescreveria a senha de outro
         * apenas trocando o {@code id} no corpo da requisicao.
         */
        @Test
        @DisplayName("usuario nao troca a senha de outra pessoa")
        void naoTrocaSenhaDeOutro() throws Exception {
            Long idDoAdmin = usuarioRepository.findByLogin(loginDe(PerfilEnum.ADMIN))
                    .orElseThrow().getId();

            mockMvc.perform(patch("/users/password")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.PROFESSOR))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"id":%d,"oldPassword":"%s","newPassword":"novaSenha1"}"""
                                    .formatted(idDoAdmin, SENHA_PADRAO)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("usuario troca a propria senha")
        void trocaAPropriaSenha() throws Exception {
            Long idDoProfessor = usuarioRepository.findByLogin(loginDe(PerfilEnum.PROFESSOR))
                    .orElseThrow().getId();

            mockMvc.perform(patch("/users/password")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.PROFESSOR))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"id":%d,"oldPassword":"%s","newPassword":"novaSenha1"}"""
                                    .formatted(idDoProfessor, SENHA_PADRAO)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("admin nao inativa a si mesmo")
        void adminNaoSeInativa() throws Exception {
            Long idDoAdmin = usuarioRepository.findByLogin(loginDe(PerfilEnum.ADMIN))
                    .orElseThrow().getId();

            mockMvc.perform(delete("/users/" + idDoAdmin)
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMIN)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("rate limit de login")
    class RateLimit {

        @Test
        @DisplayName("bloqueia com 429 e Retry-After apos exceder as tentativas")
        void bloqueiaAposLimite() throws Exception {
            String corpo = """
                    {"login":"admin@escola.com","password":"errada"}""";

            // O perfil de teste permite 3 tentativas por janela.
            for (int i = 0; i < 3; i++) {
                mockMvc.perform(post("/login")
                                .contentType(MediaType.APPLICATION_JSON).content(corpo))
                        .andExpect(status().isUnauthorized());
            }

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON).content(corpo))
                    .andExpect(status().isTooManyRequests())
                    .andExpect(header().exists(HttpHeaders.RETRY_AFTER))
                    .andExpect(jsonPath("$.status").value(429));
        }

        /**
         * Regressao do bug mais serio da versao anterior: um unico {@code Bucket} estatico
         * no filtro fazia com que cinco erros de qualquer pessoa derrubassem o login de
         * todo mundo.
         */
        @Test
        @DisplayName("o bloqueio de um login nao derruba o login dos demais")
        void bloqueioNaoEhGlobal() throws Exception {
            String corpoErrado = """
                    {"login":"admin@escola.com","password":"errada"}""";
            for (int i = 0; i < 4; i++) {
                mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON).content(corpoErrado));
            }

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"login":"%s","password":"%s"}"""
                                    .formatted(loginDe(PerfilEnum.PROFESSOR), SENHA_PADRAO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tokenJWT").isNotEmpty());
        }

        @Test
        @DisplayName("o limite nao se aplica aos demais endpoints")
        void limiteSoValeParaLogin() throws Exception {
            String autorizacao = autorizacao(PerfilEnum.COORDENADOR);

            for (int i = 0; i < 10; i++) {
                mockMvc.perform(get("/incidents").header(HttpHeaders.AUTHORIZATION, autorizacao))
                        .andExpect(status().isOk());
            }
        }
    }

    @Nested
    @DisplayName("CORS e cabecalhos")
    class Cors {

        @Test
        @DisplayName("preflight da origem configurada e' aceito")
        void preflightAceito() throws Exception {
            mockMvc.perform(options("/incidents")
                            .header(HttpHeaders.ORIGIN, "http://localhost:5500")
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(
                            HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5500"));
        }

        @Test
        @DisplayName("origem nao configurada e' recusada")
        void origemDesconhecidaRecusada() throws Exception {
            mockMvc.perform(options("/incidents")
                            .header(HttpHeaders.ORIGIN, "http://site-malicioso.example")
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("respostas de erro")
    class RespostasDeErro {

        @Test
        @DisplayName("JSON malformado devolve 400, nao 500")
        void jsonMalformado() throws Exception {
            mockMvc.perform(post("/incidents")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ isso nao e json }"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("valor de enum inexistente devolve 400")
        void enumInvalido() throws Exception {
            mockMvc.perform(post("/schoolclasses")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ADMINISTRATIVO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"DS-09","shift":"MADRUGADA","year":2026,"semester":"PRIMEIRO"}"""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("id em formato invalido devolve 400")
        void idInvalido() throws Exception {
            mockMvc.perform(get("/incidents/abc")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("a resposta de erro nao expoe detalhes internos")
        void erroNaoExpoeInterno() throws Exception {
            String resposta = mockMvc.perform(get("/incidents/999999")
                            .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR)))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            org.junit.jupiter.api.Assertions.assertFalse(
                    resposta.contains("org.springframework") || resposta.contains("SQL")
                            || resposta.contains("Hibernate"),
                    "resposta vazou detalhe interno: " + resposta);
        }
    }
}
