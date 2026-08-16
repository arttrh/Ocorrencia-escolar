package br.com.project_sena.adapter.out.repository.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
  @Override
  public boolean equals(Object o){
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TipoCategoriaEntity tipoOcorrencia = (TipoCategoriaEntity) o;
    return Objects.equals(id, tipoOcorrencia.id);
  }

  @Override
  public int hashCode(){
    return Objects.hash(id);
  }
}
