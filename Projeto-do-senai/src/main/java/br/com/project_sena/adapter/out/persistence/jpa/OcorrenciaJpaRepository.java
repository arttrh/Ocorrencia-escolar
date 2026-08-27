package br.com.project_sena.adapter.out.persistence.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.project_sena.adapter.out.persistence.entity.OcorrenciaEntity;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.vo.ContagemPorChave;

public interface OcorrenciaJpaRepository extends JpaRepository<OcorrenciaEntity, Long> {

    /**
     * Relacionamentos trazidos na mesma consulta.
     *
     * <p>Sem este grafo, cada linha da pagina dispara selects adicionais para turma,
     * aluno, categoria e tipo — o classico N+1. Uma pagina de 20 ocorrencias custava 26
     * consultas; com o grafo, custa uma (mais a de contagem da paginacao).</p>
     */
    @EntityGraph(attributePaths = {"turma", "student", "category", "type"})
    Page<OcorrenciaEntity> findAllByDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"turma", "student", "category", "type"})
    Page<OcorrenciaEntity> findAllByStatusAndDeletedFalse(OcorrenciaEnum status, Pageable pageable);

    @EntityGraph(attributePaths = {"turma", "student", "category", "type"})
    List<OcorrenciaEntity> findAllByStudentIdAndDeletedFalse(Long studentId);

    long countByStatusAndDeletedFalse(OcorrenciaEnum status);

    // As agregacoes do dashboard sao feitas no banco: trazer todas as ocorrencias para
    // contar em memoria nao escala e era exatamente o que a versao anterior fazia.
    @Query("""
            select new br.com.project_sena.application.core.domain.vo.ContagemPorChave(
                       o.category.name, count(o))
              from OcorrenciaEntity o
             where o.deleted = false
             group by o.category.name
             order by count(o) desc""")
    List<ContagemPorChave> contarPorCategoria();

    @Query("""
            select new br.com.project_sena.application.core.domain.vo.ContagemPorChave(
                       o.type.name, count(o))
              from OcorrenciaEntity o
             where o.deleted = false
             group by o.type.name
             order by count(o) desc""")
    List<ContagemPorChave> contarPorTipo();

    @Query("""
            select new br.com.project_sena.application.core.domain.vo.ContagemPorChave(
                       o.turma.name, count(o))
              from OcorrenciaEntity o
             where o.deleted = false
             group by o.turma.name
             order by count(o) desc""")
    List<ContagemPorChave> contarPorTurma();

    @Query("""
            select new br.com.project_sena.application.core.domain.vo.ContagemPorChave(
                       o.student.name, count(o))
              from OcorrenciaEntity o
             where o.deleted = false
             group by o.student.name
             order by count(o) desc""")
    List<ContagemPorChave> contarPorAluno();
}
