package br.com.project_sena.adapter.out.ratelimit;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.project_sena.application.port.out.RateLimiterPort;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;

/**
 * Limitador de tentativas de login, em memoria, com Bucket4j.
 *
 * <p>Problemas da implementacao anterior que esta classe resolve:</p>
 * <ul>
 *   <li><strong>o limite nao valia nada</strong>: o {@code RateLimitFilter} tinha um unico
 *       {@code Bucket} {@code final} compartilhado por todo o servidor — cinco tentativas
 *       de qualquer pessoa bloqueavam o login do sistema inteiro — enquanto o bucket por
 *       IP obtido no {@code LoginController} nunca chegava a ser consumido;</li>
 *   <li><strong>vazamento de memoria</strong>: o mapa de buckets crescia sem limite, um
 *       registro por IP visto, sem nunca expirar;</li>
 *   <li>os limites eram constantes no codigo, agora vem de configuracao.</li>
 * </ul>
 *
 * <p>Para varias instancias da aplicacao este armazenamento em memoria deixa de ser
 * suficiente — o Bucket4j oferece backends distribuidos (Redis/Hazelcast) e trocar
 * significa escrever outro adaptador para a mesma porta.</p>
 */
@Component
public class LoginRateLimiterAdapter implements RateLimiterPort {

    private static final Logger log = LoggerFactory.getLogger(LoginRateLimiterAdapter.class);

    private final Map<String, Entrada> buckets = new ConcurrentHashMap<>();
    private final RateLimitProperties properties;

    public LoginRateLimiterAdapter(RateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    public Veredito consumir(String chave) {
        if (!properties.habilitado()) {
            return Veredito.permitido(properties.tentativas());
        }
        removerExcedentes();

        Entrada entrada = buckets.computeIfAbsent(chave, k -> new Entrada(novoBucket()));
        entrada.marcarUso();

        ConsumptionProbe probe = entrada.bucket().tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            return Veredito.permitido(probe.getRemainingTokens());
        }

        long segundos = Math.max(1, Duration.ofNanos(probe.getNanosToWaitForRefill()).toSeconds());
        log.warn("Rate limit de login atingido para a chave {} — liberar em {}s", chave, segundos);
        return Veredito.bloqueado(segundos);
    }

    /** Zera o estado. Existe para os testes nao herdarem contagem de casos anteriores. */
    public void limpar() {
        buckets.clear();
    }

    private Bucket novoBucket() {
        Bandwidth limite = Bandwidth.builder()
                .capacity(properties.tentativas())
                .refillGreedy(properties.tentativas(), properties.janela())
                .build();
        return Bucket.builder().addLimit(limite).build();
    }

    /**
     * Descarta as chaves usadas ha' mais tempo quando o mapa passa do teto configurado.
     * Sem isso, variar o IP a cada requisicao consumiria heap indefinidamente.
     */
    private void removerExcedentes() {
        int maximo = properties.maximoDeChaves();
        if (buckets.size() <= maximo) {
            return;
        }
        List<String> maisAntigas = buckets.entrySet().stream()
                .sorted(Comparator.comparingLong(e -> e.getValue().ultimoUso()))
                .limit(Math.max(1, buckets.size() - maximo))
                .map(Map.Entry::getKey)
                .toList();
        maisAntigas.forEach(buckets::remove);
    }

    private record Entrada(Bucket bucket, AtomicLong ultimoUsoEm) {

        Entrada(Bucket bucket) {
            this(bucket, new AtomicLong(System.nanoTime()));
        }

        void marcarUso() {
            ultimoUsoEm.set(System.nanoTime());
        }

        long ultimoUso() {
            return ultimoUsoEm.get();
        }
    }
}
