package br.com.project_sena.application.port.out;

import java.util.List;
import java.util.Optional;

import br.com.project_sena.application.core.domain.model.CategoriaOcorrencia;

public interface CategoriaOcorrenciaRepository {

    CategoriaOcorrencia save(CategoriaOcorrencia categoria);

    Optional<CategoriaOcorrencia> findById(Long id);

    Optional<CategoriaOcorrencia> findByName(String name);

    List<CategoriaOcorrencia> findAll();
}
