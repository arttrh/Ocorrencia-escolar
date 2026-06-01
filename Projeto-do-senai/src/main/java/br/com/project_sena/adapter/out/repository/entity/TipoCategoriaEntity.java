package br.com.project_sena.adapter.out.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "type_occurrence")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TipoCategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_occurrence")
    private Long id;
    @Column(name = "name_occurrence")
    private String nameOccurrence;

    @ManyToOne
    @JoinColumn(name = "id_category_occurrence")
    private CategoriaOcorrenciaEntity categorias;

    public TipoCategoriaEntity(Long id, String nameOccurrence) {
    }
}
