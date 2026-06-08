package br.com.project_sena.adapter.out.repository.entity;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "id_class")
    private TurmaEntity turma;
}
