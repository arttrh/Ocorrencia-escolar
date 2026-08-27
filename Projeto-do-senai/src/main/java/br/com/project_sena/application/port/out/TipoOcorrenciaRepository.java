package br.com.project_sena.application.port.out;

import java.util.List;
import java.util.Optional;

import br.com.project_sena.application.core.domain.model.TipoOcorrencia;

public interface TipoOcorrenciaRepository {

    TipoOcorrencia save(TipoOcorrencia tipo);

    Optional<TipoOcorrencia> findById(Long id);

    Optional<TipoOcorrencia> findByName(String name);

    List<TipoOcorrencia> findByCategoriaId(Long categoriaId);

    List<TipoOcorrencia> findAll();
}
