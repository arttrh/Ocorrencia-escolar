package br.com.project_sena.adapter.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.project_sena.adapter.out.persistence.entity.VinculoEntity;

public interface VinculoJpaRepository extends JpaRepository<VinculoEntity, Long> {

    long countByTurmaId(Long turmaId);

    List<VinculoEntity> findAllByTurmaIdOrderByAlunoNameAsc(Long turmaId);

    boolean existsByAlunoIdAndTurmaId(Long alunoId, Long turmaId);

    Optional<VinculoEntity> findByAlunoId(Long alunoId);
}
