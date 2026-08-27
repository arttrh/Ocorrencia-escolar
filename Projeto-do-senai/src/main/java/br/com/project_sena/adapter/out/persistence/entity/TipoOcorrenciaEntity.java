package br.com.project_sena.adapter.out.persistence.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/**
 * Tabela {@code type_occurrence}.
 *
 * <p>Antes se chamava {@code TipoCategoriaEntity} — nome que sugeria "tipo de categoria"
 * quando na verdade e' "tipo de ocorrencia". O construtor de dois argumentos daquela
 * versao tinha corpo vazio, entao qualquer objeto criado por ele nascia com todos os
 * campos nulos.</p>
 */
@Entity
@Table(name = "type_occurrence")
@Getter
@Setter
@NoArgsConstructor
public class TipoOcorrenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_occurrence")
    private Long id;

    @Column(name = "name_occurrence", nullable = false, unique = true, length = 60)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_category_occurrence", nullable = false)
    private CategoriaOcorrenciaEntity categoria;

    public TipoOcorrenciaEntity(Long id, String name, CategoriaOcorrenciaEntity categoria) {
        this.id = id;
        this.name = name;
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TipoOcorrenciaEntity outro)) {
            return false;
        }
        return id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
