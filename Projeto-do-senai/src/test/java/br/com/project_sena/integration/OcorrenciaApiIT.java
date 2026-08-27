package br.com.project_sena.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import tools.jackson.databind.JsonNode;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;

/**
 * Integracao ponta a ponta do recurso {@code /incidents}, exercitando exatamente os
 * caminhos, os nomes de campo e os formatos de data que o front-end usa.
 */
@DisplayName("API de ocorrencias (integracao)")
class OcorrenciaApiIT extends TesteDeIntegracao {

    private static final DateTimeFormatter DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

    private String corpoDeCadastro() {
        return """
                {
                  "idSchoolClass": %d,
                  "idStudent": %d,
                  "registerDate": "%s",
                  "category": "DISCIPLINAR",
                  "type": "INDISCIPLINA EM SALA",
                  "description": "Conversa excessiva durante a aula"
                }""".formatted(turma.getId(), aluno.getId(),
                LocalDateTime.now().minusHours(1).format(DATA_HORA));
    }

    private long cadastrarOcorrencia() throws Exception {
        String resposta = mockMvc.perform(post("/incidents")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoDeCadastro()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(resposta);
        return json.get("id").asLong();
    }

    @Test
    @DisplayName("POST /incidents registra e devolve 201 com Location")
    void cadastra() throws Exception {
        mockMvc.perform(post("/incidents")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.PROFESSOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoDeCadastro()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("AGUARDANDO"))
                .andExpect(jsonPath("$.schoolClassName").value("DS-01"))
                .andExpect(jsonPath("$.studentName").value("Ana Souza"))
                .andExpect(jsonPath("$.deleted").value(false));
    }

    @Test
    @DisplayName("POST /incidents recusa aluno de outra turma com 400")
    void recusaAlunoDeOutraTurma() throws Exception {
        var outroAluno = alunoRepository.save(
                new br.com.project_sena.adapter.out.persistence.entity.AlunoEntity(
                        null, "Bruno Lima", java.time.LocalDate.of(2007, 5, 10), null,
                        br.com.project_sena.application.core.domain.enums.AlunoEnum.ATIVO));

        mockMvc.perform(post("/incidents")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idSchoolClass": %d, "idStudent": %d,
                                 "category": "DISCIPLINAR", "type": "INDISCIPLINA EM SALA",
                                 "description": "Fato qualquer"}"""
                                .formatted(turma.getId(), outroAluno.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("nao esta matriculado")));
    }

    @Test
    @DisplayName("POST /incidents devolve os campos invalidos quando falta dado obrigatorio")
    void validaCamposObrigatorios() throws Exception {
        mockMvc.perform(post("/incidents")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields.length()").value(
                        org.hamcrest.Matchers.greaterThanOrEqualTo(4)));
    }

    @Test
    @DisplayName("GET /incidents devolve a pagina no formato que o front espera")
    void listaPaginado() throws Exception {
        cadastrarOcorrencia();

        mockMvc.perform(get("/incidents")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ANALISTA))
                        .param("page", "0").param("size", "5").param("sort", "registerDate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @DisplayName("GET /incidents ignora campo de ordenacao desconhecido em vez de estourar")
    void ordenacaoInvalidaNaoQuebra() throws Exception {
        cadastrarOcorrencia();

        mockMvc.perform(get("/incidents")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ANALISTA))
                        .param("sort", "coluna.que.nao.existe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("PATCH /incidents/status muda a situacao e recusa transicao invalida")
    void mudancaDeStatus() throws Exception {
        long id = cadastrarOcorrencia();

        mockMvc.perform(patch("/incidents/status")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": %d, "status": "ATENDENDO"}""".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ATENDENDO"))
                .andExpect(jsonPath("$.updateDate").isNotEmpty());

        mockMvc.perform(patch("/incidents/status")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": %d, "status": "ATENDENDO"}""".formatted(id)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /incidents/status aceita o slug publico usado nas URLs")
    void aceitaSlug() throws Exception {
        long id = cadastrarOcorrencia();

        mockMvc.perform(patch("/incidents/status")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": %d, "status": "progressing"}""".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ATENDENDO"));
    }

    @Test
    @DisplayName("GET /incidents/status/{slug} filtra pela situacao")
    void listaPorStatus() throws Exception {
        cadastrarOcorrencia();

        mockMvc.perform(get("/incidents/status/waiting")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ANALISTA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        mockMvc.perform(get("/incidents/status/solved")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ANALISTA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /incidents/status/{slug} recusa situacao inexistente")
    void statusInexistente() throws Exception {
        mockMvc.perform(get("/incidents/status/inventado")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.ANALISTA)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /incidents faz atualizacao parcial sem apagar os outros campos")
    void atualizacaoParcial() throws Exception {
        long id = cadastrarOcorrencia();

        mockMvc.perform(put("/incidents")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": %d, "description": "Descricao revisada"}""".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Descricao revisada"))
                .andExpect(jsonPath("$.category").value("DISCIPLINAR"))
                .andExpect(jsonPath("$.schoolClassName").value("DS-01"));
    }

    @Test
    @DisplayName("DELETE /incidents/{id} faz exclusao logica")
    void exclusaoLogica() throws Exception {
        long id = cadastrarOcorrencia();

        mockMvc.perform(delete("/incidents/" + id)
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/incidents/" + id)
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));

        mockMvc.perform(get("/incidents")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /incidents/{id} inexistente devolve 404 com corpo padronizado")
    void naoEncontrado() throws Exception {
        mockMvc.perform(get("/incidents/9999")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/incidents/9999"));
    }

    @Test
    @DisplayName("GET /incidents/categories e /types devolvem listas de texto")
    void catalogos() throws Exception {
        mockMvc.perform(get("/incidents/categories")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.PROFESSOR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("DISCIPLINAR"));

        mockMvc.perform(get("/incidents/types/DISCIPLINAR")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.PROFESSOR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("INDISCIPLINA EM SALA"));
    }

    @Test
    @DisplayName("GET /incidents/status devolve nome e descricao para o select do front")
    void listaDeStatus() throws Exception {
        mockMvc.perform(get("/incidents/status")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.PROFESSOR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].description").isNotEmpty());
    }

    @Test
    @DisplayName("GET /incidents/summary alimenta os cartoes e graficos do dashboard")
    void resumoDoDashboard() throws Exception {
        long id = cadastrarOcorrencia();
        cadastrarOcorrencia();
        mockMvc.perform(patch("/incidents/status")
                .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"id": %d, "status": "progressing"}""".formatted(id)));

        mockMvc.perform(get("/incidents/summary")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.waiting").value(1))
                .andExpect(jsonPath("$.progressing").value(1))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.byCategory[0].key").value("DISCIPLINAR"))
                .andExpect(jsonPath("$.byCategory[0].value").value(2))
                .andExpect(jsonPath("$.byStudent[0].key").value("Ana Souza"))
                .andExpect(jsonPath("$.bySchoolClass[0].key").value("DS-01"));
    }

    @Test
    @DisplayName("GET historico do aluno devolve da mais recente para a mais antiga")
    void historicoDoAluno() throws Exception {
        cadastrarOcorrencia();

        mockMvc.perform(get("/incidents/students/" + aluno.getId() + "/history")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao(PerfilEnum.COORDENADOR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].studentName").value("Ana Souza"));
    }
}
