package br.com.project_sena.adapter.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Tabela {@code occurrence}. */
@Entity
@Table(name = "occurrence")
@Getter
@Setter
@NoArgsConstructor
public class OcorrenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_occurrence")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_class", nullable = false)
    private TurmaEntity turma;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_student", nullable = false)
    private AlunoEntity student;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_category_occurrence", nullable = false)
    private CategoriaOcorrenciaEntity category;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_type_occurrence", nullable = false)
    private TipoOcorrenciaEntity type;

    /** Data e hora do fato, consolidadas (antes eram duas colunas: date_occurrence + time). */
    @Column(name = "register_date", nullable = false)
    private LocalDateTime registerDate;

    @Column(name = "description_occurrence", nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "occurrence_enum", nullable = false, length = 30)
    private OcorrenciaEnum status;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    /** Exclusao logica: registros marcados somem das listagens mas ficam no historico. */
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public OcorrenciaEntity(Long id, TurmaEntity turma, AlunoEntity student,
                            CategoriaOcorrenciaEntity category, TipoOcorrenciaEntity type,
                            LocalDateTime registerDate, String description,
                            OcorrenciaEnum status, LocalDateTime updateDate,
                            boolean deleted, LocalDateTime createdAt) {
        this.id = id;
        this.turma = turma;
        this.student = student;
        this.category = category;
        this.type = type;
        this.registerDate = registerDate;
        this.description = description;
        this.status = status;
        this.updateDate = updateDate;
        this.deleted = deleted;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OcorrenciaEntity outra)) {
            return false;
        }
        return id != null && Objects.equals(id, outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
