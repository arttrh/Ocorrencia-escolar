package br.com.project_sena.adapter.out.transaction;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import br.com.project_sena.application.port.out.TransacaoPort;

/**
 * Implementa a unidade de trabalho com {@code TransactionTemplate}.
 *
 * <p>Usa a forma programatica em vez de {@code @Transactional} justamente porque os use
 * cases sao POJOs criados por {@code @Bean}: uma anotacao neles so' funcionaria com proxy,
 * e o proxy exigiria que o nucleo conhecesse o Spring.</p>
 */
@Component
public class SpringTransacaoAdapter implements TransacaoPort {

    private final TransactionTemplate transactionTemplate;

    public SpringTransacaoAdapter(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public <T> T executar(Supplier<T> operacao) {
        return transactionTemplate.execute(status -> operacao.get());
    }
}
