package br.com.project_sena.application.port.out;
import br.com.project_sena.application.core.domain.model.*;
import java.util.*;
public interface CategoriaOcorrenciaRepository {
    CategoriaOcorrencia save(CategoriaOcorrencia dados);
    List<CategoriaOcorrencia> listar();
    List<TipoOcorrencia> tipos(Long categoriaId);
    Optional<CategoriaOcorrencia> buscar(Long id);
    Optional<TipoOcorrencia> buscarTipo(Long id, Long categoriaId);
}
