package br.com.project_sena.adapter.out.persistence.entity;

import java.util.Objects;

import br.com.project_sena.application.core.domain.enums.SemestreEnum;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Tabela {@code class}. */
@Entity
@Table(name = "class")
@Getter
@Setter
@NoArgsConstructor
public class TurmaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_class")
    private Long id;

    @Column(name = "class_name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "turma_turno", nullable = false, length = 20)
    private TurmaTurnoEnum shift;

    @Column(name = "class_year", nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "semester", nullable = false, length = 20)
    private SemestreEnum semester;

    @Enumerated(EnumType.STRING)
    @Column(name = "turma_enum", nullable = false, length = 20)
    private TurmaEnum status;

    public TurmaEntity(Long id, String name, TurmaTurnoEnum shift, Integer year,
                       SemestreEnum semester, TurmaEnum status) {
        this.id = id;
        this.name = name;
        this.shift = shift;
        this.year = year;
        this.semester = semester;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TurmaEntity outra)) {
            return false;
        }
        return id != null && Objects.equals(id, outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
