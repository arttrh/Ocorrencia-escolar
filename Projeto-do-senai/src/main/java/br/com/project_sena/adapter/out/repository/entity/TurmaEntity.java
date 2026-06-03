package br.com.project_sena.adapter.out.repository.entity;

import br.com.project_sena.adapter.in.controller.request.ClassRegisterDTO;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String shift;
    private Integer classYear;

    //Enum
    @Enumerated(EnumType.STRING)
    @Column(name = "turma_enum")
    private TurmaEnum turmaEnum;
    @OneToMany(mappedBy = "turma")
    private List<AlunoEntity> alunos;

    public TurmaEntity(Long id, String className, String shift, Integer classYear, TurmaEnum turmaEnum) {
        this.id = id;
        this.className = className;
        this.shift = shift;
        this.classYear = classYear;
        this.turmaEnum = turmaEnum;
    }

    public void atualizarTurma(ClassRegisterDTO dto) {

    }
}
