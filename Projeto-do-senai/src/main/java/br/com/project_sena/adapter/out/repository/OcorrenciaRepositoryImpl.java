package br.com.project_sena.adapter.out.repository;
import br.com.project_sena.adapter.out.repository.mapper.OcorrenciaMapperEntity;
import br.com.project_sena.adapter.out.repository.persistence.OcorrenciaJpaRepository;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.port.out.OcorrenciaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class OcorrenciaRepositoryImpl implements OcorrenciaRepository {
    private final OcorrenciaJpaRepository ocorrenciaJpaRepository;
    private final OcorrenciaMapperEntity mapperEntity;

    public OcorrenciaRepositoryImpl(
        OcorrenciaJpaRepository ocorrenciaJpaRepository, OcorrenciaMapperEntity mapperEntity) {
        this.ocorrenciaJpaRepository = ocorrenciaJpaRepository;
        this.mapperEntity = mapperEntity;
    }

    public Page<Ocorrencia> listar(Pageable pagina) {
        return ocorrenciaJpaRepository.findAll(pagina).map(mapperEntity::toDomain);
    }
    public Page<Ocorrencia> listar(OcorrenciaEnum status, Pageable pagina) {
        return ocorrenciaJpaRepository.findByOcorrenciaEnum(status, pagina).map(mapperEntity::toDomain);
    }
    public long contar(OcorrenciaEnum status) {
        return ocorrenciaJpaRepository.countByOcorrenciaEnum(status);
    }
    @Override
    public Ocorrencia save(Ocorrencia dados) {
        return mapperEntity.toDomain(ocorrenciaJpaRepository.save(mapperEntity.toEntity(dados)));
    }

    @Override
    public Optional<Ocorrencia> findById(Long id) {
        return ocorrenciaJpaRepository.findById(id).map(mapperEntity::toDomain);
    }

    @Override
    public List<Ocorrencia> findAll() {
        return ocorrenciaJpaRepository.findAll().stream().map(mapperEntity::toDomain).toList();
    }
}
