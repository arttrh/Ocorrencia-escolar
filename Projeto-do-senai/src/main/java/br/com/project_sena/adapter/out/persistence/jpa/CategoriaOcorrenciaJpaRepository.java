package br.com.project_sena.adapter.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.project_sena.adapter.out.persistence.entity.CategoriaOcorrenciaEntity;

public interface CategoriaOcorrenciaJpaRepository extends JpaRepository<CategoriaOcorrenciaEntity, Long> {

    Optional<CategoriaOcorrenciaEntity> findByNameIgnoreCase(String name);

    List<CategoriaOcorrenciaEntity> findAllByOrderByNameAsc();
}
