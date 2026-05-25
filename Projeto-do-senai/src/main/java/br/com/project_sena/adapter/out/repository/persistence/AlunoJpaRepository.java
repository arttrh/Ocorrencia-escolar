package br.com.project_sena.adapter.out.repository.persistence;

import br.com.project_sena.adapter.out.repository.entity.AlunoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoJpaRepository extends JpaRepository<AlunoEntity, Long> {
}
