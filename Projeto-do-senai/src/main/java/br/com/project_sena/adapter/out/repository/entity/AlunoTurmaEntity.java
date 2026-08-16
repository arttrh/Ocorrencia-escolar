package br.com.project_sena.adapter.out.repository.entity;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vinculo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlunoTurmaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_student")
    private AlunoEntity aluno;

    @ManyToOne
    @JoinColumn(name = "id_class")
    private TurmaEntity turma;

  @Override
  public boolean equals(Object o){
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AlunoTurmaEntity aluno = (AlunoTurmaEntity) o;
    return Objects.equals(id, aluno.id);
  }

  @Override
  public int hashCode(){
    return Objects.hash(id);
  }
}
