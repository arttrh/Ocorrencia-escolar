package br.com.project_sena.adapter.out.repository.entity;

import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "occurrence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OcorrenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_class")
    private TurmaEntity turma;

    @ManyToOne
    @JoinColumn(name = "id_student")
    private AlunoEntity student;

    @ManyToOne
    @JoinColumn(name = "id_category_occurrence")
    private CategoriaOcorrenciaEntity category;

    @ManyToOne
    @JoinColumn(name = "id_type_of_occurrence")
    private TipoCategoriaEntity occurrenceType;

    private LocalDate dataOcorrencia;
    private LocalTime time;
    private String descricaoDaOcorrencia;

    //Enums
    @Enumerated(EnumType.STRING)
    private OcorrenciaEnum ocorrenciaEnum;
}
