package br.com.project_sena.adapter.out.repository.persistence;

import br.com.project_sena.adapter.out.repository.entity.TurmaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurmaJpaRepository extends JpaRepository<TurmaEntity, Long> {
}
