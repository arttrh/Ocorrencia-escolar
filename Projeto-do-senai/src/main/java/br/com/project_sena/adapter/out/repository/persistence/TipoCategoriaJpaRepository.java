package br.com.project_sena.adapter.out.repository.persistence;

import br.com.project_sena.adapter.out.repository.entity.TipoCategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoCategoriaJpaRepository extends JpaRepository<TipoCategoriaEntity, Long> {
}
