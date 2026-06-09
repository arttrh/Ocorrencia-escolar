package br.com.project_sena.application.port.out;

import br.com.project_sena.application.core.domain.model.CategoriaOcorrencia;

public interface CategoriaOcorrenciaRepository {
    CategoriaOcorrencia save(CategoriaOcorrencia dados);
}
