package br.com.project_sena.application.port.out;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OcorrenciaRepository {
    Page<Ocorrencia> listar(Pageable pagina);
    Page<Ocorrencia> listar(OcorrenciaEnum status, Pageable pagina);
    long contar(OcorrenciaEnum status);
    Ocorrencia save(Ocorrencia dados);

    Optional<Ocorrencia> findById(Long id);

    List<Ocorrencia> findAll();
}
