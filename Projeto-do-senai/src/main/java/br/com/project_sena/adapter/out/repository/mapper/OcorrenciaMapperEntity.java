package br.com.project_sena.adapter.out.repository.mapper;

import br.com.project_sena.adapter.out.repository.entity.OcorrenciaEntity;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import org.springframework.stereotype.Component;

@Component
public class OcorrenciaMapperEntity {
    private final AlunoMapperEntity alunoMapper;
    private final TurmaMapperEntity turmaMapper;
    private final TipoOcorrenciaMapperEntity tipoOcorrenciaMapper;
    private final CategoriaOcorrenciaMapperEntity categoriaOcorrenciaMapper;

    public OcorrenciaMapperEntity(
            AlunoMapperEntity alunoMapper,
            TurmaMapperEntity turmaMapper,
            TipoOcorrenciaMapperEntity tipoOcorrenciaMapper,
            CategoriaOcorrenciaMapperEntity categoriaOcorrenciaMapper){
        this.alunoMapper = alunoMapper;
        this.turmaMapper = turmaMapper;
        this.tipoOcorrenciaMapper = tipoOcorrenciaMapper;
        this.categoriaOcorrenciaMapper = categoriaOcorrenciaMapper;
    }

    public Ocorrencia toDomain(OcorrenciaEntity entity){
        return new Ocorrencia(
                entity.getId(),
                turmaMapper.toDomain(entity.getTurma()),
                alunoMapper.toDomain(entity.getStudent()),
                categoriaOcorrenciaMapper.toDomain(entity.getCategory()),
                tipoOcorrenciaMapper.toDomain(entity.getOccurrenceType()),
                entity.getDataOcorrencia(),
                entity.getTime(),
                entity.getDescricaoDaOcorrencia()
        );
    }

    public OcorrenciaEntity toEntity(Ocorrencia ocorrencia){
        return new OcorrenciaEntity(
                ocorrencia.getId(),
                turmaMapper.toEntity(ocorrencia.getTurma()),
                alunoMapper.toEntity(ocorrencia.getStudent()),
                categoriaOcorrenciaMapper.toEntity(ocorrencia.getCategory()),
                tipoOcorrenciaMapper.toEntity(ocorrencia.getOccurrenceType()),
                ocorrencia.getDataOcorrencia(),
                ocorrencia.getTime(),
                ocorrencia.getDescricaoDaOcorrencia()
        );
    }
}
