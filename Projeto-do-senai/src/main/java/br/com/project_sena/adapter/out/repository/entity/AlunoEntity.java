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
    private Long id;
    private String photo;
    private String name;
    @Column(name = "date_of_birth")
    private LocalDate dateBirth;

    @Enumerated(EnumType.STRING)
    private AlunoEnum alunoEnum;

    @ManyToMany
    @JoinTable(name = "student_class",
            joinColumns = @JoinColumn(name = "id_student"),
            inverseJoinColumns = @JoinColumn(name = "id_class"))
    private List<TurmaEntity> turmas;


    public AlunoEntity(Long id, String photo, String name, LocalDate dateBirth, AlunoEnum alunoEnum) {

    }
}
