package br.com.project_sena.adapter.out.repository.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
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
@Table(name = "class")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TurmaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_class")
    private Long id;
    private String className;
    private LocalDateTime classYear;
    private LocalDateTime semestry;

    //Enum
    @Enumerated(EnumType.STRING)
    @Column(name = "turma_enum")
    private TurmaEnum turmaEnum;
    @Enumerated(EnumType.STRING)
    @Column(name = "turma_turno")
    private TurmaTurnoEnum turnoTurma;

    public TurmaEntity(Long id, String className, TurmaTurnoEnum turmaTurnoEnum, LocalDateTime classYear, TurmaEnum turmaEnum, LocalDateTime semestry) {
        this.id = id;
        this.className = className;
        this.turnoTurma = turmaTurnoEnum;
        this.classYear = classYear;
        this.turmaEnum = turmaEnum;
        this.semestry = semestry;
    }

  @Override
  public boolean equals(Object o){
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TurmaEntity turmaEntity = (TurmaEntity) o;
    return Objects.equals(id, turmaEntity.id);
  }

  @Override
  public int hashCode(){
    return Objects.hash(id);
  }
}
