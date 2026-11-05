package br.com.project_sena.adapter.out.repository.persistence;
import br.com.project_sena.adapter.out.repository.entity.AlunoTurmaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AlunoTurmaJpaRepository extends JpaRepository<AlunoTurmaEntity, Long> {
    boolean existsByAlunoId(Long alunoId);
    boolean existsByAlunoIdAndTurmaId(Long alunoId, Long turmaId);
    long countByTurmaId(Long turmaId);
}
