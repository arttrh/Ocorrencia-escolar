package br.com.project_sena.application.port.out;

import br.com.project_sena.application.core.domain.model.Ocorrencia;

/**
 * Publicacao de eventos de ocorrencia para consumidores externos (auditoria, avisos).
 *
 * <p>Substitui o {@code RabbitmqService} que ficava dentro de {@code application.core}
 * e importava {@code RabbitTemplate} — uma dependencia de infraestrutura no nucleo.</p>
 */
public interface EventoOcorrenciaPort {

    void ocorrenciaRegistrada(Ocorrencia ocorrencia);

    void statusAlterado(Ocorrencia ocorrencia);
}
