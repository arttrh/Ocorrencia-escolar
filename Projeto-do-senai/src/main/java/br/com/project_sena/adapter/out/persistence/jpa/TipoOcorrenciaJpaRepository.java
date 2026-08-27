package br.com.project_sena.adapter.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.project_sena.adapter.out.persistence.entity.TipoOcorrenciaEntity;

public interface TipoOcorrenciaJpaRepository extends JpaRepository<TipoOcorrenciaEntity, Long> {

    Optional<TipoOcorrenciaEntity> findByNameIgnoreCase(String name);

    List<TipoOcorrenciaEntity> findAllByCategoriaIdOrderByNameAsc(Long categoriaId);

    List<TipoOcorrenciaEntity> findAllByOrderByNameAsc();
}
