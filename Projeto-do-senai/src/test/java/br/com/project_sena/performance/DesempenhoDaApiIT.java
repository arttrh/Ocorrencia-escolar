package br.com.project_sena.performance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;

import jakarta.persistence.EntityManagerFactory;
import tools.jackson.databind.JsonNode;

import br.com.project_sena.adapter.out.persistence.entity.AlunoEntity;
import br.com.project_sena.adapter.out.persistence.entity.OcorrenciaEntity;
import br.com.project_sena.adapter.out.persistence.entity.VinculoEntity;
import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.integration.TesteDeIntegracao;

/**
 * Desempenho da API com volume de dados.
 *
 * <p>Duas coisas sao verificadas aqui, e a primeira e' a que importa de verdade:</p>
 * <ol>
 *   <li><strong>numero de consultas SQL</strong> — metrica deterministica, que nao depende
 *       da maquina. E' o que denuncia N+1 e agregacao feita em memoria;</li>
 *   <li><strong>tempo de resposta</strong> — com limites folgados, so' para pegar
 *       regressao de ordem de grandeza.</li>
 * </ol>
 */
@Tag("performance")
@TestPropertySource(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@DisplayName("Desempenho da API (integracao)")
class DesempenhoDaApiIT extends TesteDeIntegracao {

    private static final int TOTAL_DE_OCORRENCIAS = 500;
    private static final int TOTAL_DE_ALUNOS = 30;

    @Autowired private EntityManagerFactory entityManagerFactory;

    private Statistics estatisticas() {
        return entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
    }

    /** Popula a base com um volume representativo de um semestre letivo. */
    private void popular() {
        List<AlunoEntity> turmaCheia = new ArrayList<>();
        for (int i = 0; i < TOTAL_DE_ALUNOS; i++) {
            AlunoEntity novo = alunoRepository.save(new AlunoEntity(
                    null, "Aluno " + i, LocalDate.of(2008, 1, 1), null, AlunoEnum.ATIVO));
            vinculoRepository.save(new VinculoEntity(null, novo, turma));
            turmaCheia.add(novo);
        }

        List<OcorrenciaEntity> lote = new ArrayList<>();
        OcorrenciaEnum[] situacoes = OcorrenciaEnum.values();
        for (int i = 0; i < TOTAL_DE_OCORRENCIAS; i++) {
            lote.add(new OcorrenciaEntity(
                    null,
                    turma,
                    turmaCheia.get(i % turmaCheia.size()),
                    categoria,
                    tipo,
                    LocalDateTime.now().minusDays(i % 60).minusHours(i % 24),
                    "Ocorrencia de carga numero " + i,
                    situacoes[i % situacoes.length],
                    null,
                    false,
                    LocalDateTime.now()));
        }
        ocorrenciaRepository.saveAll(lote);
    }

    /**
     * Uma pagina de 20 ocorrencias deve custar um numero pequeno e <em>constante</em> de
     * consultas — nao uma por linha para buscar turma, aluno, categoria e tipo.
     */
    @Test
    @DisplayName("listar ocorrencias nao dispara consulta por linha (N+1)")
    void listagemNaoTemNMaisUm() throws Exception {
        popular();
        String autorizacao = autorizacao(PerfilEnum.COORDENADOR);

        Statistics stats = estatisticas();
        stats.clear();

        mockMvc.perform(get("/incidents")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao)
                        .param("page", "0").param("size", "20"))
                .andExpect(status().isOk());

        long consultas = stats.getPrepareStatementCount();
        System.out.printf("listagem de 20 ocorrencias: %d consultas SQL%n", consultas);

        // Com o @EntityGraph do repositorio a pagina custa 3 consultas (dados,
        // contagem e a do usuario autenticado). Sem ele eram 26 para 20 linhas.
        assertTrue(consultas <= 8,
                "listagem disparou " + consultas + " consultas — indicio de N+1");
    }

    /**
     * O resumo do dashboard e' feito com {@code group by} no banco. Se alguem trocar por
     * {@code findAll()} e contar em memoria, este teste denuncia.
     */
    @Test
    @DisplayName("o resumo do dashboard agrega no banco, nao em memoria")
    void resumoAgregaNoBanco() throws Exception {
        popular();
        String autorizacao = autorizacao(PerfilEnum.COORDENADOR);

        Statistics stats = estatisticas();
        stats.clear();

        long inicio = System.nanoTime();
        String corpo = mockMvc.perform(get("/incidents/summary")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Duration decorrido = Duration.ofNanos(System.nanoTime() - inicio);

        long consultas = stats.getPrepareStatementCount();
        long linhasCarregadas = stats.getEntityLoadCount();

        System.out.printf("resumo: %d consultas, %d entidades carregadas, %d ms%n",
                consultas, linhasCarregadas, decorrido.toMillis());

        JsonNode json = objectMapper.readTree(corpo);
        assertEquals(TOTAL_DE_OCORRENCIAS, json.get("total").asInt());

        // 6 contagens por situacao + 4 agregacoes = 10 consultas, nenhuma delas
        // trazendo linhas de ocorrencia para a memoria.
        assertTrue(consultas <= 15,
                "o resumo usou " + consultas + " consultas");
        assertTrue(linhasCarregadas < TOTAL_DE_OCORRENCIAS,
                "o resumo carregou " + linhasCarregadas
                        + " entidades — a agregacao esta sendo feita em memoria");
        assertTrue(decorrido.toMillis() < 3_000,
                "resumo demorou " + decorrido.toMillis() + " ms");
    }

    @Test
    @DisplayName("a paginacao mantem o custo estavel entre a primeira e a ultima pagina")
    void paginacaoTemCustoEstavel() throws Exception {
        popular();
        String autorizacao = autorizacao(PerfilEnum.COORDENADOR);

        long primeira = tempoDaPagina(autorizacao, 0);
        long ultima = tempoDaPagina(autorizacao, TOTAL_DE_OCORRENCIAS / 20 - 1);

        System.out.printf("paginacao: primeira %d ms, ultima %d ms%n", primeira, ultima);

        // Buscar a ultima pagina nao pode custar ordens de grandeza a mais: seria sinal
        // de que a filtragem esta sendo feita depois de carregar tudo.
        assertTrue(ultima < Math.max(1_000, primeira * 10 + 500),
                "a ultima pagina custou " + ultima + " ms contra " + primeira + " ms da primeira");
    }

    @Test
    @DisplayName("suporta requisicoes simultaneas sem erro")
    void suportaCargaSimultanea() throws Exception {
        popular();
        String autorizacao = autorizacao(PerfilEnum.COORDENADOR);

        int threads = 8;
        int porThread = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Callable<Integer>> tarefas = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            tarefas.add(() -> {
                int ok = 0;
                for (int i = 0; i < porThread; i++) {
                    int status = mockMvc.perform(get("/incidents")
                                    .header(HttpHeaders.AUTHORIZATION, autorizacao)
                                    .param("size", "20"))
                            .andReturn().getResponse().getStatus();
                    if (status == 200) {
                        ok++;
                    }
                }
                return ok;
            });
        }

        long inicio = System.nanoTime();
        List<Future<Integer>> resultados = pool.invokeAll(tarefas, 120, TimeUnit.SECONDS);
        pool.shutdown();
        Duration decorrido = Duration.ofNanos(System.nanoTime() - inicio);

        int sucessos = 0;
        for (Future<Integer> resultado : resultados) {
            sucessos += resultado.get();
        }
        int esperado = threads * porThread;

        System.out.printf("carga: %d/%d requisicoes ok em %d ms (%.0f req/s)%n",
                sucessos, esperado, decorrido.toMillis(),
                esperado / Math.max(0.001, decorrido.toMillis() / 1000.0));

        assertEquals(esperado, sucessos, "houve requisicao com erro sob concorrencia");
    }

    private long tempoDaPagina(String autorizacao, int pagina) throws Exception {
        long inicio = System.nanoTime();
        mockMvc.perform(get("/incidents")
                        .header(HttpHeaders.AUTHORIZATION, autorizacao)
                        .param("page", String.valueOf(pagina))
                        .param("size", "20"))
                .andExpect(status().isOk());
        return Duration.ofNanos(System.nanoTime() - inicio).toMillis();
    }
}
