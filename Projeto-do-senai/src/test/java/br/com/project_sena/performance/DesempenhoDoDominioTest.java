package br.com.project_sena.performance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.CategoriaOcorrencia;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.TipoOcorrencia;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.domain.vo.Pagina;

/**
 * Desempenho do nucleo.
 *
 * <p>Os limites sao folgados de proposito: o objetivo nao e' cravar um numero de
 * maquina, e' detectar regressao de ordem de grandeza — uma regra que passe a varrer
 * lista, um {@code equals} que force carga, uma alocacao em laco quente.</p>
 *
 * <p>Marcados com a tag {@code performance} para que possam ser excluidos em maquinas
 * compartilhadas: {@code mvn test -Dgroups='!performance'}.</p>
 */
@Tag("performance")
@DisplayName("Desempenho do dominio")
class DesempenhoDoDominioTest {

    private static final int OPERACOES = 100_000;

    private static Ocorrencia novaOcorrencia() {
        Turma turma = new Turma(1L, "DS-01", TurmaTurnoEnum.MANHA, LocalDate.now().getYear(),
                SemestreEnum.PRIMEIRO, TurmaEnum.ATIVA);
        Aluno aluno = new Aluno(1L, "Ana", LocalDate.of(2008, 3, 27), null, AlunoEnum.ATIVO);
        return Ocorrencia.nova(turma, aluno,
                new CategoriaOcorrencia(1L, "DISCIPLINAR"),
                new TipoOcorrencia(1L, "INDISCIPLINA EM SALA", 1L),
                LocalDateTime.now().minusHours(1), "Descricao da ocorrencia");
    }

    @Test
    @DisplayName("criar 100 mil ocorrencias leva menos de 2 segundos")
    void criacaoEhBarata() {
        long inicio = System.nanoTime();
        for (int i = 0; i < OPERACOES; i++) {
            novaOcorrencia();
        }
        Duration decorrido = Duration.ofNanos(System.nanoTime() - inicio);

        System.out.printf("criacao: %d objetos em %d ms (%.0f/s)%n",
                OPERACOES, decorrido.toMillis(),
                OPERACOES / Math.max(0.001, decorrido.toMillis() / 1000.0));

        assertTrue(decorrido.toMillis() < 2_000,
                "criacao ficou lenta demais: " + decorrido.toMillis() + " ms");
    }

    @Test
    @DisplayName("a maquina de estados decide em tempo constante")
    void transicaoEhConstante() {
        long inicio = System.nanoTime();
        for (int i = 0; i < OPERACOES; i++) {
            Ocorrencia ocorrencia = novaOcorrencia();
            ocorrencia.alterarStatus(OcorrenciaEnum.ATENDENDO, null);
            ocorrencia.alterarStatus(OcorrenciaEnum.RESOLVIDA, null);
        }
        Duration decorrido = Duration.ofNanos(System.nanoTime() - inicio);

        System.out.printf("transicoes: %d ciclos em %d ms%n", OPERACOES, decorrido.toMillis());

        // O mapa de transicoes e' imutavel e consultado por hash: nao deve escalar com
        // a quantidade de status nem alocar por chamada.
        assertTrue(decorrido.toMillis() < 3_000,
                "transicao de status ficou lenta: " + decorrido.toMillis() + " ms");
    }

    @Test
    @DisplayName("mapear uma pagina de 10 mil itens nao copia a lista mais de uma vez")
    void mapeamentoDePaginaEhLinear() {
        List<Integer> conteudo = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            conteudo.add(i);
        }
        Pagina<Integer> pagina = new Pagina<>(conteudo, 0, 10_000, 10_000);

        long inicio = System.nanoTime();
        Pagina<String> mapeada = pagina.mapear(String::valueOf);
        Duration decorrido = Duration.ofNanos(System.nanoTime() - inicio);

        assertEquals(10_000, mapeada.conteudo().size());
        assertTrue(decorrido.toMillis() < 500,
                "mapeamento de pagina ficou lento: " + decorrido.toMillis() + " ms");
    }

    @Test
    @DisplayName("o dominio e' seguro para uso concorrente por ser imutavel na leitura")
    void leituraConcorrenteEhSegura() throws Exception {
        int threads = 8;
        int porThread = 5_000;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch largada = new CountDownLatch(1);
        CountDownLatch chegada = new CountDownLatch(threads);
        AtomicInteger sucessos = new AtomicInteger();

        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                try {
                    largada.await();
                    for (int i = 0; i < porThread; i++) {
                        Ocorrencia ocorrencia = novaOcorrencia();
                        ocorrencia.alterarStatus(OcorrenciaEnum.ATENDENDO, null);
                        if (ocorrencia.getStatus() == OcorrenciaEnum.ATENDENDO) {
                            sucessos.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    chegada.countDown();
                }
            });
        }

        long inicio = System.nanoTime();
        largada.countDown();
        assertTrue(chegada.await(30, TimeUnit.SECONDS), "as threads nao terminaram a tempo");
        pool.shutdown();
        Duration decorrido = Duration.ofNanos(System.nanoTime() - inicio);

        System.out.printf("concorrencia: %d operacoes em %d threads, %d ms%n",
                threads * porThread, threads, decorrido.toMillis());

        assertEquals(threads * porThread, sucessos.get(),
                "alguma transicao se perdeu sob concorrencia");
    }
}
