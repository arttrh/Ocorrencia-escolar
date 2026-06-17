package br.com.project_sena.config.security.rabbitmqConfig;

import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitmqFila {


//    É a classe construtora.
//    Ela não cria a fila no servidor imediatamente;
//    ela apenas prepara a definição (metadados) da fila em um objeto Java.

    @Bean
    public Queue fila(){
        return QueueBuilder.durable("sistema-escolar.audit")
                .build();
    }
}
