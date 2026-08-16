package br.com.project_sena.adapter.out.repository.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  @Override
  public boolean equals(Object o){
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OcorrenciaEntity ocorrencia = (OcorrenciaEntity) o;
    return Objects.equals(id, ocorrencia.id);
  }

  @Override
  public int hashCode(){
    return Objects.hash(id);
  }
}
