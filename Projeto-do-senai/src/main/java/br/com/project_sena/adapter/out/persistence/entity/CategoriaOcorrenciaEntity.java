package br.com.project_sena.adapter.out.persistence.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Tabela {@code category_occurrence}. */
@Entity
@Table(name = "category_occurrence")
@Getter
@Setter
@NoArgsConstructor
public class CategoriaOcorrenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_category_occurrence")
    private Long id;

    @Column(name = "name_category", nullable = false, unique = true, length = 60)
    private String name;

    public CategoriaOcorrenciaEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoriaOcorrenciaEntity outra)) {
            return false;
        }
        return id != null && Objects.equals(id, outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
