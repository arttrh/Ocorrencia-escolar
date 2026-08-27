package br.com.project_sena.bdd;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Estado compartilhado entre os steps de um mesmo cenario.
 *
 * <p>Cucumber cria uma instancia por cenario (escopo {@code cucumber-glue}), entao um
 * cenario nunca enxerga o que outro deixou para tras.</p>
 */
@Component
public class MundoDoCenario {

    private final Map<String, Long> idsPorNome = new HashMap<>();

    private ResultActions ultimaResposta;
    private String corpoDaUltimaResposta = "";
    private String mensagemDeErroGuardada;
    private String tokenAtual;
    private Long idDaOcorrencia;

    public void guardarId(String nome, Long id) {
        idsPorNome.put(nome, id);
    }

    public Long id(String nome) {
        Long id = idsPorNome.get(nome);
        if (id == null) {
            throw new IllegalStateException("Nenhum id registrado para \"" + nome + "\"");
        }
        return id;
    }

    public boolean conhece(String nome) {
        return idsPorNome.containsKey(nome);
    }

    public ResultActions getUltimaResposta() {
        return ultimaResposta;
    }

    public void setUltimaResposta(ResultActions ultimaResposta) throws Exception {
        this.ultimaResposta = ultimaResposta;
        this.corpoDaUltimaResposta = ultimaResposta.andReturn().getResponse().getContentAsString();
    }

    public String getCorpoDaUltimaResposta() {
        return corpoDaUltimaResposta;
    }

    public int getStatusDaUltimaResposta() {
        return ultimaResposta.andReturn().getResponse().getStatus();
    }

    public String getMensagemDeErroGuardada() {
        return mensagemDeErroGuardada;
    }

    public void setMensagemDeErroGuardada(String mensagem) {
        this.mensagemDeErroGuardada = mensagem;
    }

    public String getTokenAtual() {
        return tokenAtual;
    }

    public void setTokenAtual(String tokenAtual) {
        this.tokenAtual = tokenAtual;
    }

    public Long getIdDaOcorrencia() {
        return idDaOcorrencia;
    }

    public void setIdDaOcorrencia(Long idDaOcorrencia) {
        this.idDaOcorrencia = idDaOcorrencia;
    }
}
