package br.com.project_sena.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.entity.OcorrenciaEntity;
import br.com.project_sena.application.core.domain.model.Ocorrencia;

@Component
public class OcorrenciaMapperEntity {

    private final AlunoMapperEntity alunoMapper;
    private final TurmaMapperEntity turmaMapper;
    private final CategoriaOcorrenciaMapperEntity categoriaMapper;
    private final TipoOcorrenciaMapperEntity tipoMapper;

    public OcorrenciaMapperEntity(AlunoMapperEntity alunoMapper,
                                  TurmaMapperEntity turmaMapper,
                                  CategoriaOcorrenciaMapperEntity categoriaMapper,
                                  TipoOcorrenciaMapperEntity tipoMapper) {
        this.alunoMapper = alunoMapper;
        this.turmaMapper = turmaMapper;
        this.categoriaMapper = categoriaMapper;
        this.tipoMapper = tipoMapper;
    }

    public Ocorrencia toDomain(OcorrenciaEntity entity) {
        return new Ocorrencia(
                entity.getId(),
                turmaMapper.toDomain(entity.getTurma()),
                alunoMapper.toDomain(entity.getStudent()),
                categoriaMapper.toDomain(entity.getCategory()),
                tipoMapper.toDomain(entity.getType()),
                entity.getRegisterDate(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getUpdateDate(),
                entity.isDeleted(),
                entity.getCreatedAt());
    }
}
