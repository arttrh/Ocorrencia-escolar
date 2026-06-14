package br.com.project_sena.adapter.out.repository.entity;

import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Column(name = "id_occurrence")
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
    @JoinColumn(name = "id_type_occurrence")
    private TipoCategoriaEntity occurrenceType;

    @Column(name = "date_occurrence")
    private LocalDate dataOcorrencia;
    private LocalTime time;
    @Column(name = "description_occurrence")
    private String descricaoDaOcorrencia;

    //Enums
    @Enumerated(EnumType.STRING)
    @Column(name = "occurrence_enum")
    private OcorrenciaEnum ocorrenciaEnum;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
