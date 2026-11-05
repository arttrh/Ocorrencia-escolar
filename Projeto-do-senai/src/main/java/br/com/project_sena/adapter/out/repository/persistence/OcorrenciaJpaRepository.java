package br.com.project_sena.adapter.out.repository.persistence;
import br.com.project_sena.adapter.out.repository.entity.OcorrenciaEntity;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcorrenciaJpaRepository extends JpaRepository<OcorrenciaEntity, Long> {
    long countByOcorrenciaEnum(OcorrenciaEnum status);
    Page<OcorrenciaEntity> findByOcorrenciaEnum(OcorrenciaEnum status, Pageable pagina);
}
