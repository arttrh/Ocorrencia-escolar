package br.com.project_sena.adapter.out.repository.entity;

import java.time.LocalDate;
import java.util.Objects;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlunoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_student")
    private Long id;
    private String photo;
    private String name;
    @Column(name = "date_birth")
    private LocalDate dateBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "aluno_enum")
    private AlunoEnum alunoEnum;

  @Override
  public boolean equals(Object o){
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AlunoEntity aluno = (AlunoEntity) o;
    return Objects.equals(id, aluno.id);
  }

  @Override
  public int hashCode(){
    return Objects.hash(id);
  }
}
