package br.com.project_sena.application.port.out;

import br.com.project_sena.application.core.domain.model.Ocorrencia;

import java.util.List;
import java.util.Optional;

public interface OcorrenciaRepository {
    Ocorrencia save(Ocorrencia dados);

    Optional<Ocorrencia> findById(Long id);

    List<Ocorrencia> findAll();
}
