package br.com.project_sena.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import br.com.project_sena.adapter.out.persistence.entity.AlunoEntity;
import br.com.project_sena.adapter.out.persistence.entity.CategoriaOcorrenciaEntity;
import br.com.project_sena.adapter.out.persistence.entity.TipoOcorrenciaEntity;
import br.com.project_sena.adapter.out.persistence.entity.TurmaEntity;
import br.com.project_sena.adapter.out.persistence.entity.UsuarioEntity;
import br.com.project_sena.adapter.out.persistence.entity.VinculoEntity;
import br.com.project_sena.adapter.out.persistence.jpa.AlunoJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.CategoriaOcorrenciaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.OcorrenciaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.TipoOcorrenciaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.TurmaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.UsuarioJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.VinculoJpaRepository;
import br.com.project_sena.adapter.out.ratelimit.LoginRateLimiterAdapter;
import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Mas;
import io.cucumber.java.pt.Quando;

/** Steps dos cenarios de ocorrencia. Conversam com a API real via MockMvc. */
public class OcorrenciaSteps {

    private static final String SENHA = "senha123";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private LoginRateLimiterAdapter rateLimiter;
    @Autowired private MundoDoCenario mundo;

    @Autowired private UsuarioJpaRepository usuarios;
    @Autowired private AlunoJpaRepository alunos;
    @Autowired private TurmaJpaRepository turmas;
    @Autowired private VinculoJpaRepository vinculos;
    @Autowired private OcorrenciaJpaRepository ocorrencias;
    @Autowired private CategoriaOcorrenciaJpaRepository categorias;
    @Autowired private TipoOcorrenciaJpaRepository tipos;

    @Before
    public void limparBase() {
        rateLimiter.limpar();
        ocorrencias.deleteAll();
        vinculos.deleteAll();
        tipos.deleteAll();
        categorias.deleteAll();
        alunos.deleteAll();
        turmas.deleteAll();
        usuarios.deleteAll();
    }

    // ------------------------------------------------------------------ //
    // Dado
    // ------------------------------------------------------------------ //

    @Dado("que existe a turma {string} ativa")
    public void queExisteATurmaAtiva(String nome) {
        TurmaEntity turma = turmas.save(new TurmaEntity(
                null, nome, TurmaTurnoEnum.MANHA, LocalDate.now().getYear(),
                SemestreEnum.PRIMEIRO, TurmaEnum.ATIVA));
        mundo.guardarId(nome, turma.getId());
    }

    @Dado("que existe o aluno {string} matriculado na turma {string}")
    public void queExisteOAlunoMatriculado(String nomeDoAluno, String nomeDaTurma) {
        AlunoEntity aluno = alunos.save(new AlunoEntity(
                null, nomeDoAluno, LocalDate.of(2008, 3, 27), null, AlunoEnum.ATIVO));
        TurmaEntity turma = turmas.findById(mundo.id(nomeDaTurma)).orElseThrow();
        vinculos.save(new VinculoEntity(null, aluno, turma));
        mundo.guardarId(nomeDoAluno, aluno.getId());
    }

    @Dado("que existe o aluno {string} sem turma")
    public void queExisteOAlunoSemTurma(String nome) {
        AlunoEntity aluno = alunos.save(new AlunoEntity(
                null, nome, LocalDate.of(2007, 5, 10), null, AlunoEnum.ATIVO));
        mundo.guardarId(nome, aluno.getId());
    }

    @Dado("que existe a categoria {string} com o tipo {string}")
    public void queExisteACategoriaComOTipo(String nomeDaCategoria, String nomeDoTipo) {
        CategoriaOcorrenciaEntity categoria =
                categorias.save(new CategoriaOcorrenciaEntity(null, nomeDaCategoria));
        tipos.save(new TipoOcorrenciaEntity(null, nomeDoTipo, categoria));
        mundo.guardarId("categoria:" + nomeDaCategoria, categoria.getId());
    }

    @Dado("que estou autenticado como {string}")
    public void queEstouAutenticadoComo(String perfil) throws Exception {
        PerfilEnum papel = PerfilEnum.valueOf(perfil);
        String login = perfil.toLowerCase() + "@escola.com";
        if (usuarios.findByLogin(login).isEmpty()) {
            usuarios.save(new UsuarioEntity(null, "Usuario " + perfil, login,
                    passwordEncoder.encode(SENHA), papel, UsuarioEnum.ATIVO));
        }
        mundo.setTokenAtual(autenticar(login, SENHA));
    }

    @Dado("que o aluno {string} esta inativo")
    public void queOAlunoEstaInativo(String nome) {
        AlunoEntity aluno = alunos.findById(mundo.id(nome)).orElseThrow();
        aluno.setStatus(AlunoEnum.INATIVO);
        alunos.save(aluno);
    }

    @Dado("que a turma {string} esta cancelada")
    public void queATurmaEstaCancelada(String nome) {
        TurmaEntity turma = turmas.findById(mundo.id(nome)).orElseThrow();
        turma.setStatus(TurmaEnum.CANCELADA);
        turmas.save(turma);
    }

    @Dado("que existe uma ocorrencia registrada para {string}")
    public void queExisteUmaOcorrenciaRegistradaPara(String nomeDoAluno) throws Exception {
        registrarOcorrencia(nomeDoAluno, "DS-01", "Conversa excessiva durante a aula");
        JsonNode json = objectMapper.readTree(mundo.getCorpoDaUltimaResposta());
        mundo.setIdDaOcorrencia(json.get("id").asLong());
    }

    @Dado("que a ocorrencia foi levada ate a situacao {string}")
    public void queAOcorrenciaFoiLevadaAte(String situacao) throws Exception {
        mudarSituacao("ATENDENDO");
        if (!"ATENDENDO".equals(situacao)) {
            mudarSituacao(situacao);
        }
    }

    // ------------------------------------------------------------------ //
    // Quando
    // ------------------------------------------------------------------ //

    @Quando("eu registro uma ocorrencia para {string} na turma {string} com a descricao {string}")
    public void euRegistroUmaOcorrencia(String aluno, String turma, String descricao)
            throws Exception {
        registrarOcorrencia(aluno, turma, descricao);
    }

    @Quando("eu mudo a situacao da ocorrencia para {string}")
    public void euMudoASituacao(String situacao) throws Exception {
        mudarSituacao(situacao);
    }

    @Quando("eu cancelo a ocorrencia")
    public void euCanceloAOcorrencia() throws Exception {
        mundo.setUltimaResposta(mockMvc.perform(delete("/incidents/" + mundo.getIdDaOcorrencia())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + mundo.getTokenAtual())));
    }

    @Quando("eu consulto o resumo de ocorrencias")
    public void euConsultoOResumo() throws Exception {
        mundo.setUltimaResposta(mockMvc.perform(get("/incidents/summary")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + mundo.getTokenAtual())));
    }

    // ------------------------------------------------------------------ //
    // Entao
    // ------------------------------------------------------------------ //

    @Entao("a requisicao e' aceita com o status {int}")
    public void aRequisicaoEhAceitaComOStatus(int esperado) {
        assertEquals(esperado, mundo.getStatusDaUltimaResposta(),
                "corpo: " + mundo.getCorpoDaUltimaResposta());
    }

    @Entao("a requisicao e' recusada com o status {int}")
    public void aRequisicaoEhRecusadaComOStatus(int esperado) {
        assertEquals(esperado, mundo.getStatusDaUltimaResposta(),
                "corpo: " + mundo.getCorpoDaUltimaResposta());
    }

    @Entao("a mensagem de erro menciona {string}")
    public void aMensagemDeErroMenciona(String trecho) {
        assertTrue(mundo.getCorpoDaUltimaResposta().contains(trecho),
                "esperava encontrar \"" + trecho + "\" em: " + mundo.getCorpoDaUltimaResposta());
    }

    @Entao("a ocorrencia fica com a situacao {string}")
    public void aOcorrenciaFicaComASituacao(String situacao) throws Exception {
        JsonNode json = objectMapper.readTree(mundo.getCorpoDaUltimaResposta());
        assertEquals(situacao, json.get("status").asText());
        mundo.setIdDaOcorrencia(json.get("id").asLong());
    }

    @E("a ocorrencia aparece na listagem de ocorrencias")
    public void aOcorrenciaApareceNaListagem() throws Exception {
        String corpo = mockMvc.perform(get("/incidents")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mundo.getTokenAtual()))
                .andReturn().getResponse().getContentAsString();

        assertTrue(objectMapper.readTree(corpo).get("totalElements").asLong() >= 1,
                "a ocorrencia deveria aparecer na listagem: " + corpo);
    }

    @Entao("a ocorrencia nao aparece mais na listagem de ocorrencias")
    public void aOcorrenciaNaoApareceMais() throws Exception {
        String corpo = mockMvc.perform(get("/incidents")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mundo.getTokenAtual()))
                .andReturn().getResponse().getContentAsString();

        assertEquals(0, objectMapper.readTree(corpo).get("totalElements").asLong());
    }

    @Mas("a ocorrencia continua consultavel pelo seu id")
    public void aOcorrenciaContinuaConsultavel() throws Exception {
        String corpo = mockMvc.perform(get("/incidents/" + mundo.getIdDaOcorrencia())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mundo.getTokenAtual()))
                .andReturn().getResponse().getContentAsString();

        assertTrue(objectMapper.readTree(corpo).get("deleted").asBoolean(),
                "a ocorrencia deveria estar marcada como cancelada");
    }

    @Entao("o resumo mostra {int} ocorrencia em atendimento")
    public void oResumoMostraEmAtendimento(int quantidade) throws Exception {
        JsonNode json = objectMapper.readTree(mundo.getCorpoDaUltimaResposta());
        assertEquals(quantidade, json.get("progressing").asInt());
    }

    @E("o resumo agrupa {int} ocorrencia na categoria {string}")
    public void oResumoAgrupaNaCategoria(int quantidade, String categoria) throws Exception {
        JsonNode json = objectMapper.readTree(mundo.getCorpoDaUltimaResposta());
        JsonNode primeira = json.get("byCategory").get(0);

        assertNotNull(primeira, "o resumo nao trouxe agregacao por categoria");
        assertEquals(categoria, primeira.get("key").asText());
        assertEquals(quantidade, primeira.get("value").asInt());
    }

    // ------------------------------------------------------------------ //
    // Apoio
    // ------------------------------------------------------------------ //

    private void registrarOcorrencia(String aluno, String turma, String descricao)
            throws Exception {
        String corpo = """
                {"idSchoolClass": %d, "idStudent": %d,
                 "category": "DISCIPLINAR", "type": "INDISCIPLINA EM SALA",
                 "description": "%s"}"""
                .formatted(mundo.id(turma), mundo.id(aluno), descricao);

        mundo.setUltimaResposta(mockMvc.perform(post("/incidents")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + mundo.getTokenAtual())
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo)));
    }

    private void mudarSituacao(String situacao) throws Exception {
        mundo.setUltimaResposta(mockMvc.perform(patch("/incidents/status")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + mundo.getTokenAtual())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"id": %d, "status": "%s"}"""
                        .formatted(mundo.getIdDaOcorrencia(), situacao))));
    }

    private String autenticar(String login, String senha) throws Exception {
        String corpo = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login":"%s","password":"%s"}""".formatted(login, senha)))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("tokenJWT").asText();
    }
}
