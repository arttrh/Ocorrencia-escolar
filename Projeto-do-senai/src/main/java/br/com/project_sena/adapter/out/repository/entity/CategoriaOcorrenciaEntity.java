package br.com.project_sena.adapter.out.repository.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category_occurrence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaOcorrenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_category_occurrence")
    private Long id;
    private String nameCategory;
  @Override
  public boolean equals(Object o){
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CategoriaOcorrenciaEntity categoria = (CategoriaOcorrenciaEntity) o;
    return Objects.equals(id, categoria.id);
  }

  @Override
  public int hashCode(){
    return Objects.hash(id);
  }
}
