package br.com.project_sena.adapter.out.persistence.entity;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tabela {@code vinculo} — matricula de um aluno em uma turma.
 *
 * <p>Antes se chamava {@code AlunoTurmaEntity} e nao era usada por nenhum repositorio,
 * de modo que o vinculo aluno/turma nunca era gravado.</p>
 */
@Entity
@Table(name = "vinculo")
@Getter
@Setter
@NoArgsConstructor
public class VinculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_student", nullable = false, unique = true)
    private AlunoEntity aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_class", nullable = false)
    private TurmaEntity turma;

    public VinculoEntity(Long id, AlunoEntity aluno, TurmaEntity turma) {
        this.id = id;
        this.aluno = aluno;
        this.turma = turma;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VinculoEntity outro)) {
            return false;
        }
        return id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
