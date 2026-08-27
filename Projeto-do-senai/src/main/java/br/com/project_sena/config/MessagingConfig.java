package br.com.project_sena.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.project_sena.adapter.out.messaging.LogEventoOcorrenciaAdapter;
import br.com.project_sena.adapter.out.messaging.RabbitEventoOcorrenciaAdapter;
import br.com.project_sena.application.port.out.EventoOcorrenciaPort;

/**
 * Escolhe a implementacao de {@link EventoOcorrenciaPort}.
 *
 * <p>Com {@code app.messaging.rabbit.enabled=true} os eventos vao para o RabbitMQ; sem
 * isso, ficam no log. A aplicacao sobe nos dois casos — antes o RabbitMQ era obrigatorio,
 * embora nem estivesse declarado no {@code pom.xml}.</p>
 */
@Configuration
public class MessagingConfig {

    @Bean
    @ConditionalOnProperty(name = "app.messaging.rabbit.enabled", havingValue = "true")
    public Queue filaDeOcorrencias(@Value("${app.messaging.rabbit.queue}") String fila) {
        return QueueBuilder.durable(fila).build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.messaging.rabbit.enabled", havingValue = "true")
    public EventoOcorrenciaPort eventoOcorrenciaRabbit(RabbitTemplate rabbitTemplate,
                                                       @Value("${app.messaging.rabbit.queue}") String fila) {
        return new RabbitEventoOcorrenciaAdapter(rabbitTemplate, fila);
    }

    @Bean
    @ConditionalOnMissingBean(EventoOcorrenciaPort.class)
    public EventoOcorrenciaPort eventoOcorrenciaLog() {
        return new LogEventoOcorrenciaAdapter();
    }
}
