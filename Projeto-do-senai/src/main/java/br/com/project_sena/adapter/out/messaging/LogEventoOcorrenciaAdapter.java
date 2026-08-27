package br.com.project_sena.adapter.out.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.port.out.EventoOcorrenciaPort;

/**
 * Implementacao padrao de {@link EventoOcorrenciaPort}: registra o evento no log.
 *
 * <p>Existe para que a aplicacao suba e funcione sem um broker no ambiente — o
 * {@code RabbitmqService} anterior era obrigatorio e, alem disso, recebia a
 * {@code Ocorrencia} por injecao de dependencia, o que impedia o contexto de subir
 * porque nao existe bean de {@code Ocorrencia}.</p>
 */
public class LogEventoOcorrenciaAdapter implements EventoOcorrenciaPort {

    private static final Logger log = LoggerFactory.getLogger(LogEventoOcorrenciaAdapter.class);

    @Override
    public void ocorrenciaRegistrada(Ocorrencia ocorrencia) {
        log.info("Ocorrencia registrada: id={} aluno={} turma={} tipo={}",
                ocorrencia.getId(),
                ocorrencia.getStudent().getName(),
                ocorrencia.getTurma().getName(),
                ocorrencia.getType().getName());
    }

    @Override
    public void statusAlterado(Ocorrencia ocorrencia) {
        log.info("Ocorrencia {} mudou para o status {}",
                ocorrencia.getId(), ocorrencia.getStatus());
    }
}
