package br.com.project_sena.application.port.out;

import java.util.function.Supplier;

/**
 * Unidade de trabalho.
 *
 * <p>Os use cases precisam de atomicidade (ler-e-gravar sem corrida), mas anotar o
 * nucleo com {@code @Transactional} do Spring o acoplaria ao framework. Esta porta
 * expressa a intencao "execute isto atomicamente" e deixa a implementacao para o
 * adaptador.</p>
 */
public interface TransacaoPort {

    <T> T executar(Supplier<T> operacao);

    default void executar(Runnable operacao) {
        executar(() -> {
            operacao.run();
            return null;
        });
    }
}
