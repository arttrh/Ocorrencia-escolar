package br.com.project_sena.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import br.com.project_sena.adapter.out.persistence.entity.UsuarioEntity;
import br.com.project_sena.adapter.out.persistence.jpa.UsuarioJpaRepository;
import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

/**
 * Steps dos cenarios de autenticacao.
 *
 * <p>A limpeza da base fica no {@code @Before} de {@link OcorrenciaSteps}, que o Cucumber
 * executa para todos os cenarios do glue.</p>
 */
public class AutenticacaoSteps {

    private static final String SENHA = "senha123";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UsuarioJpaRepository usuarios;
    @Autowired private MundoDoCenario mundo;

    @Dado("que existe o usuario {string} com o perfil {string}")
    public void queExisteOUsuario(String login, String perfil) {
        if (usuarios.findByLogin(login).isEmpty()) {
            usuarios.save(new UsuarioEntity(null, "Usuario " + perfil, login,
                    passwordEncoder.encode(SENHA), PerfilEnum.valueOf(perfil), UsuarioEnum.ATIVO));
        }
    }

    @Dado("que o usuario {string} esta inativo")
    public void queOUsuarioEstaInativo(String login) {
        UsuarioEntity usuario = usuarios.findByLogin(login).orElseThrow();
        usuario.setStatus(UsuarioEnum.INATIVO);
        usuarios.save(usuario);
    }

    @Quando("eu tento entrar com o login {string} e a senha {string}")
    public void euTentoEntrar(String login, String senha) throws Exception {
        mundo.setUltimaResposta(mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"login":"%s","password":"%s"}""".formatted(login, senha))));
    }

    @Quando("eu erro a senha de {string} {int} vezes")
    public void euErroASenha(String login, int vezes) throws Exception {
        for (int i = 0; i < vezes; i++) {
            euTentoEntrar(login, "errada");
        }
    }

    @Quando("eu consulto as ocorrencias sem token")
    public void euConsultoSemToken() throws Exception {
        mundo.setUltimaResposta(mockMvc.perform(get("/incidents")));
    }

    @E("eu guardo a mensagem de erro recebida")
    public void euGuardoAMensagemDeErro() {
        JsonNode json = objectMapper.readTree(mundo.getCorpoDaUltimaResposta());
        mundo.setMensagemDeErroGuardada(json.get("message").asText());
    }

    @Entao("a mensagem de erro e' igual a anterior")
    public void aMensagemDeErroEhIgualAAnterior() {
        JsonNode json = objectMapper.readTree(mundo.getCorpoDaUltimaResposta());

        assertEquals(mundo.getMensagemDeErroGuardada(), json.get("message").asText(),
                "mensagens diferentes permitiriam descobrir quais contas existem");
    }

    @Entao("eu recebo um token de acesso")
    public void euReceboUmToken() {
        JsonNode json = objectMapper.readTree(mundo.getCorpoDaUltimaResposta());

        assertTrue(json.has("tokenJWT"), "resposta sem tokenJWT: " + mundo.getCorpoDaUltimaResposta());
        assertFalse(json.get("tokenJWT").asText().isBlank());
    }

    @Entao("a resposta informa em quantos segundos posso tentar de novo")
    public void aRespostaInformaOTempoDeEspera() {
        String retryAfter = mundo.getUltimaResposta()
                .andReturn().getResponse().getHeader("Retry-After");

        assertTrue(retryAfter != null && Long.parseLong(retryAfter) > 0,
                "esperava o cabecalho Retry-After com o tempo de espera");
    }
}
