package br.com.project_sena.adapter.out.persistence.entity;

import java.time.LocalDate;
import java.util.Objects;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Tabela {@code student}. */
@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
public class AlunoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_student")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "date_birth", nullable = false)
    private LocalDate birthDate;

    /** Data URI ou URL da foto. Opcional: o cadastro do front nao envia imagem. */
    @Lob
    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "aluno_enum", nullable = false, length = 20)
    private AlunoEnum status;

    public AlunoEntity(Long id, String name, LocalDate birthDate, String imageUrl, AlunoEnum status) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlunoEntity outro)) {
            return false;
        }
        return id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
