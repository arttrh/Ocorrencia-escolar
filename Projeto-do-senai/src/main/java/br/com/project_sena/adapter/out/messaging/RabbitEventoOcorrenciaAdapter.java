package br.com.project_sena.adapter.out.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.port.out.EventoOcorrenciaPort;

/**
 * Publica eventos de ocorrencia em uma fila RabbitMQ.
 *
 * <p>Falhas de publicacao sao registradas e engolidas de proposito: uma auditoria
 * indisponivel nao pode derrubar o registro de uma ocorrencia que ja foi gravada.</p>
 */
public class RabbitEventoOcorrenciaAdapter implements EventoOcorrenciaPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitEventoOcorrenciaAdapter.class);

    private final RabbitTemplate rabbitTemplate;
    private final String fila;

    public RabbitEventoOcorrenciaAdapter(RabbitTemplate rabbitTemplate, String fila) {
        this.rabbitTemplate = rabbitTemplate;
        this.fila = fila;
    }

    @Override
    public void ocorrenciaRegistrada(Ocorrencia ocorrencia) {
        publicar("OCORRENCIA_REGISTRADA", ocorrencia);
    }

    @Override
    public void statusAlterado(Ocorrencia ocorrencia) {
        publicar("STATUS_ALTERADO", ocorrencia);
    }

    private void publicar(String evento, Ocorrencia ocorrencia) {
        String mensagem = String.join(";",
                evento,
                String.valueOf(ocorrencia.getId()),
                String.valueOf(ocorrencia.getStudent().getId()),
                String.valueOf(ocorrencia.getTurma().getId()),
                String.valueOf(ocorrencia.getStatus()),
                String.valueOf(ocorrencia.getRegisterDate()));
        try {
            rabbitTemplate.convertAndSend(fila, mensagem);
        } catch (AmqpException e) {
            log.error("Falha ao publicar {} da ocorrencia {}: {}",
                    evento, ocorrencia.getId(), e.getMessage());
        }
    }
}
