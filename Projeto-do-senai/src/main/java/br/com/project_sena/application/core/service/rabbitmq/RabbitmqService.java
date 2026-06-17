package br.com.project_sena.application.core.service.rabbitmq;

import br.com.project_sena.application.core.domain.model.Ocorrencia;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitmqService {

    private final RabbitTemplate rabbitTemplate;
    private Ocorrencia ocorrencia;

    public RabbitmqService(RabbitTemplate rabbitTemplate, Ocorrencia ocorrencia){
        this.rabbitTemplate = rabbitTemplate;
        this.ocorrencia = ocorrencia;
    }


    public void enviarOcorrencia(){
    String mensagem = ocorrencia.getId() + " - " + ocorrencia.getDataOcorrencia() + " - " + ocorrencia.getDescricaoDaOcorrencia();

    rabbitTemplate.convertAndSend("Sistema-Escolar", mensagem);
        System.out.println("Mensagem enviada");
    }
}
