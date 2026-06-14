package br.com.project_sena.adapter.out.repository.entity;

import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
}
