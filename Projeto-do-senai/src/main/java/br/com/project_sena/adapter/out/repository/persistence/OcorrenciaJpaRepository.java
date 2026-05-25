package br.com.project_sena.adapter.out.repository.persistence;

import br.com.project_sena.adapter.out.repository.entity.OcorrenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcorrenciaJpaRepository extends JpaRepository<OcorrenciaEntity, Long> {
}
