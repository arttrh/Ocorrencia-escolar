package br.com.project_sena.adapter.out.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "type_of_occurrence")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TipoCategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nameOccurrence;

    @ManyToOne
    @JoinColumn(name = "id_category")
    private CategoriaOcorrenciaEntity categorias;

    public TipoCategoriaEntity(Long id, String nameOccurrence) {
    }
}
